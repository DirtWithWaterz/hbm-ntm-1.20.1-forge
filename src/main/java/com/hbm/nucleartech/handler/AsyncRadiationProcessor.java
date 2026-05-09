package com.hbm.nucleartech.handler;

import com.hbm.nucleartech.HBM;
import com.hbm.nucleartech.network.HbmPacketHandler;
import com.hbm.nucleartech.network.packet.ClientboundSpawnRadioactiveDustParticlePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AsyncRadiationProcessor {

    private static volatile boolean isShutdown = false;

    private static final ThreadFactory threadFactory = r -> {
        Thread t = new Thread(r, "Async Radiation Processor");
        t.setDaemon(true);
        t.setUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Uncaught exception in " + thread.getName() + ": " + throwable.getMessage());
            throwable.printStackTrace();
        });
        return t;
    };

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor(threadFactory);

    // Lightweight immutable snapshot of one pocket (created on main thread)
    public static final class PocketSnapshot {
        public final ServerLevel level;
        public final BlockPos subChunkPos;   // world position of the 16x16x16 subchunk
        public final int pocketIndex;
        public final float sourceRads;
        public final float radiation;
        public final float accumulatedRads;
        public final List<Integer>[] connectionIndices; // deep-copied per direction
        public final boolean sealed;

        PocketSnapshot(ServerLevel level, HbmRadiationSystem.RadPocket p) {
            this.level = level;
            this.subChunkPos = p.parent.parentChunk.getWorldPos(p.parent.yLevel);
            this.pocketIndex = p.index;
            this.sourceRads = p.sourceRads;
            this.radiation = p.radiation;
            this.accumulatedRads = p.accumulatedRads;
            this.connectionIndices = p.connectionIndices;
            this.sealed = p.isSealed();
        }

        @Override
        public boolean equals(Object obj) {

            HBM.LOGGER.debug("checking if pocket is already in the snapshot...");
            if(obj instanceof PocketSnapshot other)
                return other.subChunkPos == this.subChunkPos && other.pocketIndex == this.pocketIndex;

            HBM.LOGGER.error("pocket is already in the snapshot! Skipping...");
            return false;
        }
    }

    public record PocketUpdate(BlockPos subChunkPos, int pocketIndex, float newRadiation) {}
    public record PocketKey(BlockPos subChunkPos, int pocketIndex) {}
    public record RadiationUpdateResult(
            ServerLevel level,
            List<PocketUpdate> pocketUpdates,
            Map<PocketKey, Float> accumulatedAdds,
            List<BlockPos> rebuildSubChunks,
            List<BlockPos> dirtyChunkMins,           // min block pos of chunk (for setUnsaved)
            List<BlockPos> subChunksForParticles
    ) {}

    public static void submit(Runnable task) {
        if(!isShutdown)
            executorService.execute(task);
    }

    private static boolean updating = false;

    public static void tick(ServerLevel level) {

        if(!updating)
            update(level);
    }

    private static void update(ServerLevel level) {

        updating = true;
        List<PocketSnapshot> snapshots = new ArrayList<>();

        // Safe copy under lock (we will make the set synchronized in step 3)
        synchronized (HbmRadiationSystem.getActivePockets()) {
            for (HbmRadiationSystem.RadPocket p : HbmRadiationSystem.getActivePockets()) {
                if (p.parent == null || p.parent.parentChunk == null ||
                        p.parent.parentChunk.chunk.getLevel() != level) continue;
                snapshots.add(new PocketSnapshot(level, p));
                for(Direction e : Direction.values()) {

                    // Get the blockPos for the next sub chunk in that direction
                    BlockPos nPos = p.parent.parentChunk.getWorldPos(p.parent.yLevel).relative(e, 16);

                    // If it's not loaded or it's out of bounds, do nothing
                    if(!p.parent.parentChunk.chunk.getLevel().isLoaded(nPos) || nPos.getY() < -64 || nPos.getY() > 319)
                        continue;

                    if(p.connectionIndices[e.ordinal()].size() == 1 && p.connectionIndices[e.ordinal()].get(0) == -1) {

                        // If the chunk in this direction isn't loaded, load it
                        HbmRadiationSystem.rebuildChunkPockets(p.parent.parentChunk.chunk.getLevel().getChunkAt(nPos), HbmRadiationSystem.ChunkStorageCompat.getIndexFromWorldY(nPos.getY()));
                    } else {

                        // Otherwise, for every pocket this chunk is connected to in this direction, add radiation to it
                        // Also add those pockets to the active pockets set
                        HbmRadiationSystem.SubChunkRadiationStorage sc2 = HbmRadiationSystem.getSubChunkStorage(p.parent.parentChunk.chunk.getLevel(), nPos);
                        for(int idx : p.connectionIndices[e.ordinal()]) {

                            // Don't spread to sealed pockets
                            if(!sc2.pockets[idx].isSealed()) {

                                // Only accumulated rads get updated so the system doesn't interfere with itself while working
//                                sc2.pockets[idx].accumulatedRads += p.sourceRads + p.radiation * amountPer;
//                                HbmRadiationSystem.addActivePocket(sc2.pockets[idx]);
                                PocketSnapshot snap = new PocketSnapshot(level, sc2.pockets[idx]);
                                HBM.LOGGER.debug("[Debug] sc at: {} found! Adding to snapshots...", sc2.subChunkPos);
                                if(!snapshots.contains(snap))
                                    snapshots.add(snap);
                            }
                        }
                    }
                }
            }
        }

        if (snapshots.isEmpty()) return;

        submit(
                () -> {

                    RadiationUpdateResult result = process(snapshots);
                    level.getServer().execute(() -> {

                        apply(result);
                    });
                }
        );
    }

    private static RadiationUpdateResult process(List<PocketSnapshot> snapshots) {
        List<PocketUpdate> updates = new ArrayList<>();
        Map<PocketKey, Float> accAdds = new HashMap<>();
        List<BlockPos> rebuilds = new ArrayList<>();
        List<BlockPos> dirtyMins = new ArrayList<>();
        List<BlockPos> particles = new ArrayList<>();

        for (PocketSnapshot p : snapshots) {
            // === Phase 1: Decay ===
            float rad = p.radiation + p.sourceRads / 10f;
            rad *= 0.7f;
            rad -= 0.4f;

            // === Spreading (ALWAYS do this, even on the dying tick) ===
            boolean willDie = (p.sourceRads + rad <= 0);

            float count = 0;
            for (List<Integer> list : p.connectionIndices) count += list.size();
            float amountPer = (count == 0 || rad < 1) ? 0 : 0.5f / count;

            if (p.sourceRads + rad > 0 && amountPer > 0) {
                for (int d = 0; d < 6; d++) {
                    Direction dir = Direction.values()[d];
                    BlockPos neighborSub = p.subChunkPos.relative(dir, 16);

                    for (int idx : p.connectionIndices[d]) {
                        if (idx == -1) {
                            rebuilds.add(neighborSub);
                        } else {
                            accAdds.merge(new PocketKey(neighborSub, idx),
                                    p.sourceRads + rad * amountPer, Float::sum);
                        }
                    }
                }
            }

            if (amountPer != 0) {
                accAdds.merge(new PocketKey(p.subChunkPos, p.pocketIndex),
                        rad * 0.5f, Float::sum);
            }

            // Particles
            if (rad > 1f) {
                particles.add(p.subChunkPos);
            }

            // Final value for this pocket
            float finalRad = willDie ? 0 : rad;
            updates.add(new PocketUpdate(p.subChunkPos, p.pocketIndex, finalRad));

            // Mark chunk dirty
            BlockPos chunkMin = new BlockPos(
                    p.subChunkPos.getX() & ~15,
                    p.subChunkPos.getY(),
                    p.subChunkPos.getZ() & ~15);
            dirtyMins.add(chunkMin);
        }

        return new RadiationUpdateResult(
                snapshots.get(0).level,   // all snapshots are from the same level
                updates, accAdds, rebuilds, dirtyMins, particles);
    }

    private static void apply(RadiationUpdateResult res) {
        long time = System.currentTimeMillis();
        ServerLevel level = res.level();

        // 1. Apply new radiation values
        for (PocketUpdate u : res.pocketUpdates()) {
            HbmRadiationSystem.SubChunkRadiationStorage sc =
                    HbmRadiationSystem.getSubChunkStorage(level, u.subChunkPos());
            if (sc != null && u.pocketIndex() < sc.pockets.length) {
                HbmRadiationSystem.RadPocket p = sc.pockets[u.pocketIndex()];
                if (p != null) {
                    p.radiation = u.newRadiation();
                    if (p.radiation <= 0) HbmRadiationSystem.removeActivePocket(p);
                }
            }
        }

        // 2. Apply accumulated radiation adds
        for (var entry : res.accumulatedAdds().entrySet()) {
            PocketKey k = entry.getKey();
            HbmRadiationSystem.SubChunkRadiationStorage sc =
                    HbmRadiationSystem.getSubChunkStorage(level, k.subChunkPos());
            if (sc != null && k.pocketIndex() < sc.pockets.length) {
                HbmRadiationSystem.RadPocket p = sc.pockets[k.pocketIndex()];
                if (p != null) {
                    p.accumulatedRads += entry.getValue();
                    HbmRadiationSystem.addActivePocket(p);
                }
            }
        }

        // 3. Rebuilds (rare after world is loaded)
        for (BlockPos sub : res.rebuildSubChunks()) {
            if (level.isLoaded(sub)) {
                LevelChunk chunk = level.getChunk(sub.getX() >> 4, sub.getZ() >> 4);
                HbmRadiationSystem.rebuildChunkPockets(chunk,
                        HbmRadiationSystem.ChunkStorageCompat.getIndexFromWorldY(sub.getY()));
            }
        }

        // 4. Mark chunks dirty
        for (BlockPos min : res.dirtyChunkMins()) {
            LevelChunk chunk = level.getChunk(min.getX() >> 4, min.getZ() >> 4);
            chunk.setUnsaved(true);
        }

        // 5. Spawn radioactive dust particles (on main thread, using level.random)
        for (BlockPos sub : res.subChunksForParticles()) {
            for (int i = 0; i < 10; i++) {   // original amount – feel free to lower
                BlockPos randPos = sub.offset(
                        level.random.nextInt(15),
                        level.random.nextInt(15),
                        level.random.nextInt(15));

                BlockState state = level.getBlockState(randPos);
                Vec3 rPos = new Vec3(randPos.getX() + 0.5, randPos.getY() + 0.5, randPos.getZ() + 0.5);

                BlockHitResult trace = level.clip(new ClipContext(
                        rPos, rPos.add(0, -6, 0),
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));

                if (state.isAir() && trace.getType() == HitResult.Type.BLOCK) {
                    ClientboundSpawnRadioactiveDustParticlePacket pkt =
                            new ClientboundSpawnRadioactiveDustParticlePacket(rPos.x, rPos.y, rPos.z, 0, 0, 0);

                    HbmPacketHandler.INSTANCE.send(
                            PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(sub)),
                            pkt);
                }
            }
        }

        if(System.currentTimeMillis() - time > 20) {

            updating = false;
            return;
        }

        HbmRadiationSystem.applyAccumulatedDecay();
        updating = false;
    }

    public static void shutdown() {
        isShutdown = true;
        executorService.shutdown();
    }
}