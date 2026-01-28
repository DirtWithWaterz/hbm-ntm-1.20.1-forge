package com.hbm.nucleartech.network.packet;

import java.util.TreeSet;
import java.util.function.Supplier;

import com.hbm.nucleartech.mixin.MixinData;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher.RenderChunk;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import com.hbm.nucleartech.explosion.VeryFastRaycastedExplosion;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClearSectionListPack {
    private final TreeSet<Long> affectedSections;

    public ClearSectionListPack(TreeSet<Long> sections) {
        this.affectedSections = sections;
    }

    public ClearSectionListPack(FriendlyByteBuf buffer) {
        this.affectedSections = new TreeSet<>();
        int size = buffer.readInt();

        for (int i = 0; i < size; i++) {
            this.affectedSections.add(buffer.readLong());
        }
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.affectedSections.size());

        for (Long l : this.affectedSections) {
            buffer.writeLong(l);
        }
    }

    private void removeMarked(ClientLevel world) {
        ClientChunkCache cache = world.getChunkSource();
        int minSec = world.getMinSection();
        LevelLightEngine lightEngine = cache.getLightEngine();
        LightEngine<?, ?> block_engine = (LightEngine<?, ?>)lightEngine.getLayerListener(LightLayer.SKY);
        Long2ObjectOpenHashMap<DataLayer> block_map = block_engine.storage.visibleSectionData.map;

        for (long l : this.affectedSections) {
            int x = VeryFastRaycastedExplosion.x(l);
            int y = VeryFastRaycastedExplosion.y(l);
            int z = VeryFastRaycastedExplosion.z(l);
            LevelChunk chunk = world.getChunk(x, z);
            LevelChunkSection section = chunk.getSection(VeryFastRaycastedExplosion.y(l));
            VeryFastRaycastedExplosion.setAll(((MixinData<BlockState>)(Object)section.states.data).getPalette(), ((MixinData<BlockState>)(Object)section.states.data).getStorage());
            long longID = VeryFastRaycastedExplosion.asLong(x, y + minSec, z);
            DataLayer layer = block_map.get(longID);
            if (layer == null) {
                layer = new DataLayer(15);
                block_map.put(longID, layer);
            }

            layer.fill(15);
            chunk.setUnsaved(true);
        }
    }

    private void Clear() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != null) {
            this.removeMarked(level);
            LevelRenderer renderer = minecraft.levelRenderer;
            RenderChunk[] renderChunks = renderer.viewArea.chunks;

            for (RenderChunk renderChunk : renderChunks) {
                renderChunk.setDirty(false);
            }
        }
    }

    public void handle(Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::Clear));
        ctx.get().setPacketHandled(true);
    }
}
