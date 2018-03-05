package io.github.fedimser.nonolab.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BitArrayTest {
    private BitArray createBitArray(int... trueBits) {
        BitArray a = new BitArray(200);
        for(int bit: trueBits){
            a.setBit(bit, true);
        }
        return a;
    }

    @Test
    void getBit() {
        BitArray a = createBitArray(60,100);
        assertFalse(a.getBit(1));
        assertTrue(a.getBit(100));
        assertTrue(a.getBit(60));
    }

    @Test
    void andWith() {
        BitArray a1 = createBitArray(1,3,5);
        BitArray a2 = createBitArray(2,4,6);
        a1.andWith(a2);
        for(int i=0;i<=6;i++) assertFalse(a1.getBit(i));
    }

    @Test
    void orWith() {
        BitArray a1 = createBitArray(0,2,4);
        BitArray a2 = createBitArray(1,3,5);
        a1.orWith(a2);
        for(int i=0;i<6;i++) assertTrue(a1.getBit(i));
    }

    @Test
    void getLength() {
        BitArray a = new BitArray(200);
        assertEquals(a.getLength(),200);
    }

    @Test
    void testFill(){
        BitArray a = new BitArray(41);
        for(int i=0;i<35;i++){
            a.setBit(i,true);
        }
        for(int i=0;i<35;i++)assertTrue(a.getBit(i));
        for(int i=35;i<41;i++)assertFalse(a.getBit(i));

    }
}