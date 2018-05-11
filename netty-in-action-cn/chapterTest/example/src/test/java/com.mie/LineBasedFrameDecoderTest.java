package com.mie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import static org.junit.Assert.*;

public class LineBasedFrameDecoderTest {

    @Test
    public void testDecode(){
        ByteBuf buf = Unpooled.buffer(1024);
        byte[] ar = "hello world ".getBytes();
        for(int i = 0; i < 10; i++){
            buf.writeBytes(ar);
            buf.writeBytes(String.valueOf(i).getBytes());
            buf.writeByte('\n');
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(
                new LineBasedFrameDecoder(1024),
                new StringDecoder(CharsetUtil.UTF_8),
                new StringEncoder(CharsetUtil.UTF_8));


        assertTrue(channel.writeInbound(input.retain()));

        assertTrue(channel.finish());

        String read = null;
        for(int i = 0; i < 10; i++) {
            read = (String) channel.readInbound();
            System.out.println(read);
        }


    }

    @Test
    public void testEncode(){
        ByteBuf buf = Unpooled.buffer(1024);
        byte[] ar = "hello world ".getBytes();
        for(int i = 0; i < 10; i++){
            buf.writeBytes(ar);
            buf.writeBytes(String.valueOf(i).getBytes());
            buf.writeByte('\n');
        }

        ByteBuf input = buf.duplicate();
        EmbeddedChannel channel = new EmbeddedChannel(
                new LineBasedFrameDecoder(1024),
                new StringDecoder(CharsetUtil.UTF_8),
                new StringEncoder(CharsetUtil.UTF_8));

        EmbeddedChannel channelNext = new EmbeddedChannel(
                new LineBasedFrameDecoder(1024),
                new StringDecoder(CharsetUtil.UTF_8),
                new StringEncoder(CharsetUtil.UTF_8));


        assertTrue(channel.writeOutbound(input.retain()));

        assertTrue(channel.finish());

        String read = null;

        ByteBuf tmp = (ByteBuf)( channel.readOutbound());
        channelNext.writeInbound(tmp);
        channelNext.finish();
        System.out.println(tmp);
        for(int i = 0; i < 10; i++) {
            read = (String) channelNext.readInbound();
            System.out.println(read);
        }

    }



}
