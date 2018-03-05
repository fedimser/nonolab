package io.github.fedimser.nonolab.util;

import java.util.Arrays;

public class BitArray {
    int length;
    int chunks;
    long[] bits;

    public BitArray(int length) {
        this.length = length;
        this.chunks = (length-1)/64+1;
        this.bits = new long[chunks];
        for(int i=0;i<chunks;i++){
            bits[i]=0;
        }
    }

    public boolean getBit(int index) {
        assert (index<length);
        return ((bits[index/64] & (1L<<(index%64)))!=0);
    }

    public void setBit(int index, boolean value) {
        assert (index< length);
        long indicator =(1L<<(index%64));
        if(value) {
            bits[index/64] |= indicator;
        } else {
            if( (bits[index/64] & indicator)!=0)bits[index/64]-=indicator;
        }
    }

    public void andWith(BitArray a) {
        assert(a.length==length);
        for(int i=0;i<chunks;i++) {
            bits[i] &= a.bits[i];
        }
    }

    public void orWith(BitArray a) {
        assert(a.length==length);
        for(int i=0;i<chunks;i++) {
            bits[i] |= a.bits[i];
        }
    }

    public int getLength(){
        return length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<length;i++)sb.append(getBit(i)?'1':'0');
        return sb.toString();
    }
}
