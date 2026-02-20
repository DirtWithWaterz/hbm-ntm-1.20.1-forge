package com.hbm.nucleartech.handler;

import com.hbm.nucleartech.handler.HbmRadiationSystem.ChunkStorageCompat;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RadiationWorldHandler {

    private static final int MAX_CHUNKS_PER_TICK = 2; // Process up to 2 chunks per tick
    private static final Random random = ThreadLocalRandom.current();

    public static void handleWorldDestruction(Level level) {
        if (!(level instanceof ServerLevel world)) return;

        Collection<HbmRadiationSystem.RadPocket> active = HbmRadiationSystem.getActivePockets();
        if (active.isEmpty()) return;

        int threshold = 5;
        List<HbmRadiationSystem.RadPocket> pockets = getPocketsAboveThreshold(active, world, threshold);
        if (pockets.isEmpty()) return;

        List<HbmRadiationSystem.RadPocket> theChosenONESSSSS = choosePocketsFromList(pockets, world, MAX_CHUNKS_PER_TICK);
//        String str = "";
//        for(RadiationSystemChunksNT.RadPocket p : theChosenONESSSSS)
//            str = str.concat(p.parent.parentChunk.chunk.getPos().toString() + " idx: " + p.index + ", other pockets in subchunk: " + (p.parent.pockets.length - 1) + "\n");
//        System.err.println("================\n" + str + "================");
        if(theChosenONESSSSS.isEmpty()) return;

        // Process multiple pockets in parallel
        int processed = 0;
        for (HbmRadiationSystem.RadPocket pocket : theChosenONESSSSS) {
            if (processed >= MAX_CHUNKS_PER_TICK) break;
            
            HbmRadiationSystem.SubChunkRadiationStorage sc = pocket.parent;
            if (sc == null || sc.parentChunk == null || sc.parentChunk.chunk == null) continue;

            // Queue the chunk section for async processing
            LevelChunk chunk = sc.parentChunk.chunk;
            int sectionY = ChunkStorageCompat.getIndexFromWorldY(sc.yLevel);
            
//            if (sectionY >= 0 && sectionY < chunk.getSections().length) {
                AsyncChunkProcessor.queueChunkForProcessing(world, chunk, sectionY, pocket);
                processed++;
//            }
        }
    }

    private static List<HbmRadiationSystem.RadPocket> choosePocketsFromList(List<HbmRadiationSystem.RadPocket> pockets, ServerLevel world, int i) {

        List<HbmRadiationSystem.RadPocket> list = new ArrayList<>();

        for(int j = 0; j < i; j++)
            list.add(pockets.get(world.random.nextInt(pockets.size())));

        return list;
    }

    // Get all pockets above the threshold
    private static List<HbmRadiationSystem.RadPocket> getPocketsAboveThreshold(
            Collection<HbmRadiationSystem.RadPocket> pockets, ServerLevel world, int threshold) {
        
        if (pockets == null || pockets.isEmpty()) return List.of();

        List<HbmRadiationSystem.RadPocket> snapshot = new ArrayList<>(pockets);
        List<HbmRadiationSystem.RadPocket> result = new ArrayList<>(Math.min(snapshot.size(), 16));

        for (HbmRadiationSystem.RadPocket pocket : snapshot) {
            if (pocket == null) continue;
            if (Float.isNaN(pocket.radiation)) continue; // Skip invalid radiation values

            if (pocket.radiation >= threshold) {
                result.add(pocket);
            }
        }

        // Shuffle to distribute processing across chunks
        if (result.size() > 1) {
            for (int i = result.size() - 1; i > 0; i--) {
                int index = random.nextInt(i + 1);
                HbmRadiationSystem.RadPocket temp = result.get(index);
                result.set(index, result.get(i));
                result.set(i, temp);
            }
        }

        return result;
    }

    public static void shutdown() {
        AsyncChunkProcessor.shutdown();
    }
}
