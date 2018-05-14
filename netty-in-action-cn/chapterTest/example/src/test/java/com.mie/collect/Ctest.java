package com.mie.collect;

import org.junit.Test;

public class Ctest {

    @Test
    public void mapTest(){
        for (int i = 0; i < 10; i++) {
            System.out.println("i = " + i + " n = " + tableSizeFor(i));
        }
    }

    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return n;
        //return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
}
