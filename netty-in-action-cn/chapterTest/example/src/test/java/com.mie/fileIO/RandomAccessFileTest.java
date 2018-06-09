package com.mie.fileIO;

import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.util.CharsetUtil;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class RandomAccessFileTest {
    public static void firstTest (RandomAccessFile raf) {
        try {
            raf.write("1111111111\r\n".getBytes("utf-8"));
        }catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    public static void secondTest(RandomAccessFile raf){
        try{
            raf.write("22222222222222222\r\n".getBytes("utf-8"));
        }catch (java.io.IOException e){
            e.printStackTrace();
        }
    }

    public static void thirdTest(RandomAccessFile raf){
        try{
            FileChannel fc = raf.getChannel();
            for (int i = 0; i < 10; i++) {
                fc.write(Unpooled.copiedBuffer(String.valueOf(100000*(i+1)),
                        CharsetUtil.UTF_8).nioBuffer());

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            RandomAccessFile raf = new RandomAccessFile("rafTest.txt", "rw");
            //firstTest(raf);
            secondTest(raf);
            raf.seek(raf.length());
            thirdTest(raf);
            raf.close();
        }catch (java.io.IOException e){
            e.printStackTrace();
        }
    }






}
