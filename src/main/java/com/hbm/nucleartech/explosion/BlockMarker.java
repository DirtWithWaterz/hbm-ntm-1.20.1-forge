package com.hbm.nucleartech.explosion;

import java.util.TreeSet;

import com.sun.jna.platform.win32.COM.EnumMoniker;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;

public class BlockMarker extends FastBitSet {
    private final long[] affectedChunks;
    //public final long[] affectedSections;
    public TreeSet<Long> affectedSections;//= new ArrayList<Long>();
    private int chunksUsed = 0;
    //private int numofsections;
    //private final int blocksPerChunk = 24 * 4096;

    // max chunks, we want no resizing coz its slow...
    public BlockMarker(int max_expected_chunks, int numofsections) {
        super(max_expected_chunks * numofsections * 4096);
        affectedChunks = new long[max_expected_chunks];
        affectedSections = new TreeSet<>();
        longs_per_chunk = numofsections * 64;
    }

    public FriendlyByteBuf writeChunk(ByteBuf byteBuf, long chunkLong, int chunkIndex) {
        byteBuf.writeLong(chunkLong);
        int maxChunkBits=(chunkIndex+1)*longs_per_chunk;
        byteBuf.writeInt(longs_per_chunk);
        for (int i = chunkIndex*longs_per_chunk; i < maxChunkBits; i++) {
            byteBuf.writeLong(bits[i]);
        }
        return new FriendlyByteBuf(byteBuf);
    }

    public long[] getAffectedChunks() {
        return this.affectedChunks;
    }

    public int getUsedChunks() {
        return this.chunksUsed;
    }

    // as long for x and z (no y)
    private long asLong(int x, int z) {
        return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
    }

    // get index, add if not there
    private int getChunkIndex(long curChunkLong) {
        for (int i = 0; i < chunksUsed; i++) {
            if (affectedChunks[i] == curChunkLong) {
                return i;
            }
        }
        int t_index = chunksUsed;
        affectedChunks[chunksUsed] = curChunkLong;
        chunksUsed++;
        return t_index;
    }

    //private long m; //perma predefined long
    private int t_index;//perma predefined int

    // may be faster in some occasions
    private long lastChunkLong = -1;
    //private int blocks = 0;

    private final int longs_per_chunk;
    private int index; // per define perma value

    public void markBlock(long curChunkLong, int sectionIndex, int x, int y, int z) {
        if (curChunkLong != lastChunkLong) {
            t_index = getChunkIndex(curChunkLong);
            lastChunkLong = curChunkLong;
        }
        this.index = getIndex(x, y, z); // 0-4096 index, >>6 to get longs & 63 to get "leftovers"
        setBit(((t_index * longs_per_chunk)) + (sectionIndex << 6) + (this.index >> 6), this.index & 63);
    }

    //from local block pos
    public void markBlock(int chunkX, int chunkZ, int sectionIndex, int x, int y, int z) {
        markBlock(asLong(chunkX, chunkZ), sectionIndex, x, y, z);
    }

    public boolean markSection(long id){
        return affectedSections.add(id);
    }
    public boolean hasSec(long id){
        return affectedSections.contains(id);
    }


}
