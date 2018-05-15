package com.mie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.CharsetUtil;
import org.junit.Test;

public class BufferTest {

    @Test
    public void testWrite(){
        ByteBuf sBuf = Unpooled.copiedBuffer("hello world", CharsetUtil.UTF_8);
        ByteBuf dBuf = Unpooled.buffer(1024);
        System.out.println("write before:");
        System.out.println("sBuf " + sBuf);
        System.out.println("dBuf " + dBuf);
        System.out.println("================  write after ===============");
        //dBuf.writeBytes(sBuf, 5);
        sBuf.readBytes(dBuf, sBuf.readableBytes());
        System.out.println("sBuf " + sBuf);
        System.out.println("dBuf " + dBuf);

        System.out.println(dBuf.indexOf(0, dBuf.readableBytes(), (byte)'h'));
        System.out.println("dBuf " + dBuf);

        System.out.println(dBuf.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte b) throws Exception {
                if(b == (byte)'o')
                    return false;
                return true;
            }
        }));


    }

    @Test
    public void testByteBufUtil(){
        ByteBuf dBuf = Unpooled.buffer(1024);
        ByteBufUtil.copy(new AsciiString("hello world"), dBuf);
        System.out.println("dBuf " + dBuf);
        System.out.println("dBuf " + dBuf.toString(CharsetUtil.UTF_8));
        System.out.println("dBuf " + dBuf);
        dBuf.readByte();
        System.out.println("dBuf " + dBuf);


    }



}
