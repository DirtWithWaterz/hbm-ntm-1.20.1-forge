package com.hbm.nucleartech.network.packet;

import java.util.function.Supplier;

import com.hbm.nucleartech.mixin.MixinData;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientChunkCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.SimpleBitStorage;
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

public class ClearMarkedChunkPack {
    private final FriendlyByteBuf buff;

    public ClearMarkedChunkPack(FriendlyByteBuf buffer) {
        this.buff = buffer;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBytes(this.buff);
    }

    private void removeMarked(ClientLevel level) {
        ClientChunkCache cache = level.getChunkSource();
        int chunkLongOffset = 0;
        int blockX, blockY, blockZ, cellIndex, $$1, $$2, index, chunkX, sectionY, chunkZ, sectionOffset, airId, realBlockLongOffset, maxSingle;
        long $$3, maskedAirId, chunkIndex, longID;
        byte[] skyData = new byte[0];
        byte[] blockData = new byte[0];
        long[] rawData;
        LevelChunkSection[] sections;
        LevelChunkSection section;
        DataLayer skyLayer, blockLayer;
        SimpleBitStorage bitstorage;
        VeryFastRaycastedExplosion.MagicEntry storage;
        chunkIndex = buff.readLong();
        int minSec = level.getMinSection();
        LevelLightEngine lightEngine = cache.getLightEngine();
        LightEngine<?, ?> skyEngine = lightEngine.getLayerListener(LightLayer.SKY) instanceof LightEngine<?, ?> engine ? engine : null;
        Long2ObjectOpenHashMap<DataLayer> skyMap = skyEngine != null ? skyEngine.storage.visibleSectionData.map : null;
        LightEngine<?, ?> blockEngine = lightEngine.getLayerListener(LightLayer.BLOCK) instanceof LightEngine<?, ?> engine ? engine : null;
        Long2ObjectOpenHashMap<DataLayer> blockMap = blockEngine != null ? blockEngine.storage.visibleSectionData.map : null;
        long[] markerBits = new long[buff.readInt()];
        Minecraft minecraft = Minecraft.getInstance();
        LevelRenderer renderer = minecraft.levelRenderer;
        for (int m = 0; m < markerBits.length; m++) {
            markerBits[m] = buff.readLong();
        }
        short[] markedBlocksInSec = new short[buff.readInt()];
        for (int m = 0; m < markedBlocksInSec.length; m++) {
            markedBlocksInSec[m] = buff.readShort();
        }
        int xx = buff.readInt();
        int yy = buff.readInt();
        int zz = buff.readInt();
        int radiusx = buff.readInt();
        int radiusy = buff.readInt();
        VeryFastRaycastedExplosion.Shape shape = VeryFastRaycastedExplosion.Shape.values()[buff.readShort()];
        chunkX = (int) chunkIndex;
        chunkZ = (int) (chunkIndex >> 32L);
        int basedx = (chunkX << 4) - xx;
        int basedz = (chunkZ << 4) - zz;
        int basedy;
        BlockState[] block = new BlockState[2];
        LevelChunk chunk = cache.getChunkNow(chunkX, chunkZ);
        if (chunk != null) {
            sections = chunk.getSections();
            longID = VeryFastRaycastedExplosion.asLong(chunkX, minSec, chunkZ);
            LevelChunkSection lastSec = null;
            for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
                section = sections[sectionIndex];
                if (!section.hasOnlyAir()) {
                    sectionOffset = chunkLongOffset + (sectionIndex << 6);
                    if (markedBlocksInSec[sectionOffset>>6] == 0) {
                        continue;
                    }
                    sectionY = minSec + sectionIndex;
                    longID = VeryFastRaycastedExplosion.asLongY(longID, sectionY);
                    basedy = (sectionY << 4) - yy;
                    if (markedBlocksInSec[sectionOffset>>6] < 4096) {
                        airId = section.states.data.palette.idFor(VeryFastRaycastedExplosion.AirState);
                        if (!(section.states.data.storage instanceof net.minecraft.util.ZeroBitStorage)) {
                            bitstorage = (SimpleBitStorage) section.states.data.storage;
                            storage = VeryFastRaycastedExplosion.storage[bitstorage.getBits()];
                            maskedAirId = airId & storage.mask;
                            if (skyMap != null) {
                                skyLayer = skyMap.get(longID);
                                if (skyLayer == null) {
                                    skyLayer = new DataLayer(0);
                                    skyMap.put(longID, skyLayer);
                                }
                                skyData = skyLayer.getData();
                            }
                            if (blockMap != null) {
                                blockLayer = blockMap.get(longID);
                                if (blockLayer == null) {
                                    blockLayer = new DataLayer(0);
                                    blockMap.put(longID, blockLayer);
                                }
                                blockData = blockLayer.getData();
                            }
                            rawData = bitstorage.getRaw();
                            realBlockLongOffset = sectionOffset;
                            for (int bigLong = 0; bigLong < 64; bigLong++) {
                                if (markerBits[realBlockLongOffset] != 0L) {
                                    maxSingle = (bigLong << 6) + 64;
                                    for (int blockIndex = bigLong << 6; blockIndex < maxSingle; blockIndex++) {
                                        if ((markerBits[realBlockLongOffset] & 1L << (blockIndex & 63)) != 0L) {
                                            blockX = blockIndex & 0xF;
                                            blockY = blockIndex >> 4 & 0xF;
                                            blockZ = blockIndex >> 8;
                                            cellIndex = (blockY << 4 | blockZ) << 4 | blockX;
                                            $$1 = (int) (cellIndex * storage.divideMulLong + storage.divideAddLong >> 32L >> storage.divideShift);
                                            $$2 = (cellIndex - $$1 * storage.valuesPerLong) * storage.bits;
                                            $$3 = rawData[$$1];
                                            rawData[$$1] = $$3 & (~(storage.mask << $$2)) | maskedAirId << $$2;
                                            index = blockY << 8 | blockZ << 4 | blockX;
                                            if (skyMap != null) skyData[index >> 1] = (byte) (skyData[index >> 1] | 15 << 4 * (index & 1)); //set sky light
                                            if (blockMap != null) blockData[index >> 1] = (byte) 0; //set block light
                                        }
                                    }
                                }
                                realBlockLongOffset++;
                            }

                        }
                    } else {
                        VeryFastRaycastedExplosion.clearSectionWhole(section, -1);
                        if (skyMap != null) {
                            skyLayer = skyMap.get(longID); //set sky light
                            if (skyLayer == null) {
                                skyLayer = new DataLayer(15);
                                skyMap.put(longID, skyLayer);
                            } else {
                                skyLayer.fill(15);
                            }
                        }
                        if (blockMap != null) {
                            blockLayer = blockMap.get(longID); //set block light
                            if (blockLayer == null) {
                                blockLayer = new DataLayer(0);
                                blockMap.put(longID, blockLayer);
                            } else {
                                blockLayer.fill(0);
                            }
                        }
                    }
                    renderer.setSectionDirtyWithNeighbors(chunkX, sectionY, chunkZ);
                }
                lastSec = section;
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
