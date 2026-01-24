package com.hbm.nucleartech.network.packet;

import java.util.function.Supplier;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SimpleBitStorage;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import com.hbm.nucleartech.explosion.VeryFastRaycast;
import net.minecraftforge.network.NetworkEvent.Context;

public class ClearMarkedChunkPack {
    private final FriendlyByteBuf buff;

    public ClearMarkedChunkPack(FriendlyByteBuf buffer) {
        this.buff = buffer;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBytes(this.buff);
    }

    private void removeMarked(ClientLevel world) {
        long chunkIndex = this.buff.readLong();
        ClientChunkCache cache = world.getChunkSource();
        int minSec = world.getMinSection();
        LevelLightEngine lightEngine = cache.getLightEngine();
        LightEngine<?, ?> block_engine = (LightEngine<?, ?>)lightEngine.getLayerListener(LightLayer.BLOCK);
        Long2ObjectOpenHashMap<DataLayer> block_map = block_engine.storage.visibleSectionData.map;
        long[] markerBits = new long[this.buff.readInt()];

        for (int m = 0; m < markerBits.length; m++) {
            markerBits[m] = this.buff.readLong();
        }

        int chunkX = (int)chunkIndex;
        int chunkZ = (int)(chunkIndex >> 32);
        LevelChunk chunk = cache.getChunkNow(chunkX, chunkZ);
        if (chunk != null) {
            LevelChunkSection[] sections = chunk.getSections();
            long longID = VeryFastRaycast.asLong(chunkX, minSec, chunkZ);

            for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
                LevelChunkSection section = sections[sectionIndex];
                int blockLongOffset = 0;
                if (!section.hasOnlyAir()) {
                    int sectionOffset = sectionIndex << 6;

                    for (int i = sectionOffset; i < 64 && markerBits[i] == 0L; i++) {
                        blockLongOffset++;
                    }

                    if (blockLongOffset != 64) {
                        int sectionY = minSec + sectionIndex;
                        longID = VeryFastRaycast.asLongY(longID, (long)sectionY);
                        int air_id = section.states.data.palette.idFor(VeryFastRaycast.AirState);
                        SimpleBitStorage bitstorage = (SimpleBitStorage)section.states.data.storage;
                        VeryFastRaycast.MagicEntry magicEntry = VeryFastRaycast.other_magic[bitstorage.getBits()];
                        long maskedAirId = (long)air_id & magicEntry.mask;
                        DataLayer layer = (DataLayer)block_map.get(longID);
                        if (layer == null) {
                            layer = new DataLayer(0);
                            block_map.put(longID, layer);
                        }

                        byte[] blockData = layer.getData();
                        long[] rawData = bitstorage.getRaw();
                        int realBlockLongOffset = sectionOffset + blockLongOffset;

                        for (int bigLong = blockLongOffset; bigLong < 64; bigLong++) {
                            if (markerBits[realBlockLongOffset] != 0L) {
                                int maxSingle = (bigLong << 6) + 64;

                                for (int blockIndex = bigLong << 6; blockIndex < maxSingle; blockIndex++) {
                                    if ((markerBits[realBlockLongOffset] & 1L << (blockIndex & 63)) != 0L) {
                                        int x = blockIndex & 15;
                                        int y = blockIndex >> 4 & 15;
                                        int z = blockIndex >> 8;
                                        int rindex = (y << 4 | z) << 4 | x;
                                        int e = (int)((long)rindex * magicEntry.divideMulLong + magicEntry.divideAddLong >> 32 >> magicEntry.divideShift);
                                        int k = (rindex - e * magicEntry.valuesPerLong) * magicEntry.bits;
                                        long j = rawData[e];
                                        rawData[e] = j & ~(magicEntry.mask << k) | maskedAirId << k;
                                        int index = y << 8 | z << 4 | x;
                                        blockData[index >> 1] = (byte)(blockData[index >> 1] | 15 << 4 * (index & 1));
                                    }
                                }
                            }

                            realBlockLongOffset++;
                        }
                    }
                }
            }

            chunk.setUnsaved(true);
        }
    }

    private void Clear() {
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level != null) {
            this.removeMarked(level);
        }
    }

    public void handle(Supplier<Context> ctx) {
        ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::Clear));
        ctx.get().setPacketHandled(true);
    }
}
