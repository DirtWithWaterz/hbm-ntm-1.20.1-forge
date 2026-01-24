package com.hbm.nucleartech.explosion;

import java.util.Arrays;
import java.util.BitSet;

public class FastBitSet {
    protected long[] bits;
    private int size;

    public FastBitSet(int capacity) {
        this.size = capacity;
        this.bits = new long[(capacity + 63) / 64];
    }

    public int size() {
        return size;
    }

    public void setBit(int index) {
        //ensureCapacity(index);
        int arrayIndex = index >>> 6;
        int bitIndex = index & 63;
        bits[arrayIndex] |= (1L << bitIndex);
    }

    public void setBit(int arrayIndex,int bitIndex) {
        bits[arrayIndex] |= (1L << bitIndex);
    }

    public int get(int arrayIndex,int bitIndex) {
        return (bits[arrayIndex] & (1L << bitIndex)) != 0 ? 1 : 0;
    }

    public int get(int index) {
        int arrayIndex = index >>> 6;
        int bitIndex = index & 63;
        return (bits[arrayIndex] & (1L << bitIndex)) != 0 ? 1 : 0;
    }

    public void clear() {
        Arrays.fill(bits, 0);
    }

    public int getIndex(int x, int y, int z) {
        //int index = x + (y * 16) + (z * 16 * 16);
        int index = x + (y << 4) + (z << 8);
        if (index < 0) { // shouldnt ever happen since x y z are always positive 0-15
            throw new IndexOutOfBoundsException("Index cannot be negative. " + index);
        }
        return index;
    }

    public long[] getBits(){
        return this.bits;
    }
}
