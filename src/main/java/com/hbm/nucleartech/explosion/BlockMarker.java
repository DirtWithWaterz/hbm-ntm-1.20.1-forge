package com.hbm.nucleartech.explosion;

import java.util.TreeSet;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public class BlockMarker {
    protected long[] blocks;
    protected short[] markedBitsInSec;
    private final int size;
    private final long[] markedChunks;
    public TreeSet<Long> markedSections;
    private int numOfMarkedChunks = 0;
    private int lastIndex;
    private long lastChunkLong = -1;
    private final int longsPerChunk;

    public BlockMarker(int maxChunks, int sectionsPerChunk) {
        longsPerChunk = sectionsPerChunk * 64;
        size = maxChunks * sectionsPerChunk * 4096;
        blocks = new long[(size + 63) / 64];
        markedBitsInSec = new short[maxChunks * sectionsPerChunk]; // how many blocks are marked in a section, if 4096 mark whole section
        markedChunks = new long[maxChunks];
        markedSections = new TreeSet<>();
    }

    public int size() { return size; }

    public void setBit(int arrayIndex,int bitIndex) {
        if ((blocks[arrayIndex] & 1L << bitIndex) == 0) {
            blocks[arrayIndex] |= (1L << bitIndex);
            ++markedBitsInSec[arrayIndex>>6];
        }
    }

    public int get(int arrayIndex,int bitIndex) { return (blocks[arrayIndex] & (1L << bitIndex)) != 0 ? 1 : 0; }

    public int get(int index) {
        int arrayIndex = index >>> 6;
        int bitIndex = index & 63;
        return (blocks[arrayIndex] & (1L << bitIndex)) != 0 ? 1 : 0;
    }

    public int getIndex(int x, int y, int z) {
        int index = x + (y << 4) + (z << 8);
        if (index < 0) throw new IndexOutOfBoundsException("Index invalid. " + index);
        return index;
    }

    public long[] getBlocks(){ return this.blocks; }

    public FriendlyByteBuf writeChunk(ByteBuf byteBuf, long chunkLong, int index, int x, int y, int z, int radiusx, int radiusy, short shape) {
        byteBuf.writeLong(chunkLong);
        int maxChunkBits=(index+1)* longsPerChunk;
        byteBuf.writeInt(longsPerChunk);
        for (int i = index* longsPerChunk; i < maxChunkBits; i++) {
            byteBuf.writeLong(blocks[i]);
        }
        byteBuf.writeInt(longsPerChunk >>6);
        for (int i = index* longsPerChunk >>6; i < maxChunkBits>>6; i++) {
            byteBuf.writeShort(markedBitsInSec[i]);
        }
        byteBuf.writeInt(x);
        byteBuf.writeInt(y);
        byteBuf.writeInt(z);
        byteBuf.writeInt(radiusx);
        byteBuf.writeInt(radiusy);
        byteBuf.writeShort(shape);
        return new FriendlyByteBuf(byteBuf);
    }

    public long[] getMarkedChunks() { return this.markedChunks; }

    public int getNumOfMarkedChunks() { return this.numOfMarkedChunks; }

    private long toLong(int x, int z) { return (long) x & 4294967295L | ((long) z & 4294967295L) << 32; }

    private int getChunkIndex(long curChunkLong) {
        for (int i = 0; i < numOfMarkedChunks; i++) {
            if (markedChunks[i] == curChunkLong) {
                return i;
            }
        }
        lastIndex = numOfMarkedChunks;
        markedChunks[numOfMarkedChunks] = curChunkLong;
        numOfMarkedChunks++;
        return lastIndex;
    }

    public void markBlock(long curChunkLong, int sectionIndex, int sx, int sy, int sz) {
        if (curChunkLong != lastChunkLong) {
            lastIndex = getChunkIndex(curChunkLong);
            lastChunkLong = curChunkLong;
        }
        int index = getIndex(sx, sy, sz); // 0-4096 index in section
        setBit((lastIndex * longsPerChunk) + (sectionIndex << 6) + (index >> 6), index & 63);
    }

    public void markBlock(int cx, int cz, int sectionIndex, int sx, int sy, int sz) {
        markBlock(toLong(cx, cz), sectionIndex, sx, sy, sz);
    }

    public boolean markSection(long id){ return markedSections.add(id); }

    public boolean hasSection(long id){ return markedSections.contains(id); }


}
