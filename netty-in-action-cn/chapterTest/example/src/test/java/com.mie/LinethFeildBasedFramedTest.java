package com.mie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import static org.junit.Assert.*;


public class LinethFeildBasedFramedTest {

    @Test
    public void testOne(){
        ByteBuf buf = Unpooled.buffer(1024);
        byte[] ar = "hello world ".getBytes();
        buf.writeShort(ar.length);
        buf.writeBytes(ar);

        System.out.println("buf: " + buf);
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(2048, 0, 2,
                        0, 2),
                new LengthFieldPrepender(2)
                );

        assertTrue(embeddedChannel.writeInbound(buf.retain()));
        assertTrue(embeddedChannel.finish());

        ByteBuf resBuf = (ByteBuf) embeddedChannel.readInbound();
        System.out.println(resBuf.getShort(0));
        System.out.println(resBuf);
        System.out.println(new String(ByteBufUtil.getBytes(resBuf)));
    }


    @Test
    public void testTwo(){
        ByteBuf buf = Unpooled.buffer(1024);
        byte[] head = "11".getBytes();
        byte[] ar = "hello world!!!!".getBytes();
        buf.writeBytes(head);
        buf.writeShort(ar.length);
        buf.writeBytes(ar);

        System.out.println("buf: " + buf);
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(2048, 2, 2,
                        0, 4),
                new LengthFieldPrepender(2)
        );

        assertTrue(embeddedChannel.writeInbound(buf.retain()));
        assertTrue(embeddedChannel.finish());

        ByteBuf resBuf = (ByteBuf) embeddedChannel.readInbound();
        System.out.println(resBuf.getShort(0));
        System.out.println(resBuf);
        System.out.println(new String(ByteBufUtil.getBytes(resBuf)));
    }


    @Test
    public void testThree(){
        ByteBuf buf = Unpooled.buffer(1024);
        byte[] head1 = "11".getBytes();
        byte[] head2 = "1122".getBytes();
        byte[] ar = "hello world!!!!".getBytes();
        buf.writeBytes(head1);
        buf.writeInt(ar.length+ head2.length + 4);
        buf.writeBytes(head2);
        buf.writeBytes(ar);

        System.out.println("buf: " + buf);
        System.out.println(new String(ar));
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(2048, 2, 4,
                        -4, 6),
                new LengthFieldPrepender(2)
        );

        assertTrue(embeddedChannel.writeInbound(buf.retain()));
        assertTrue(embeddedChannel.finish());

        ByteBuf resBuf = (ByteBuf) embeddedChannel.readInbound();
        System.out.println(resBuf.getShort(0));
        System.out.println(resBuf);
        System.out.println(new String(ByteBufUtil.getBytes(resBuf)));
    }


    @Test
    public void testFour(){
        ByteBuf buf = Unpooled.buffer(1024);
        byte[] head1 = "11".getBytes();
        byte[] head2 = "1122".getBytes();
        byte[] ar = "hello world!!!!".getBytes();
        buf.writeBytes(head1);
        buf.writeInt(ar.length);
        buf.writeBytes(head2);
        buf.writeBytes(ar);

        System.out.println("buf: " + buf);
        System.out.println(new String(ar));
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(
                new LengthFieldBasedFrameDecoder(2048, 2, 4,
                        4, 6),
                new LengthFieldPrepender(2)
        );

        assertTrue(embeddedChannel.writeInbound(buf.retain()));
        assertTrue(embeddedChannel.finish());

        ByteBuf resBuf = (ByteBuf) embeddedChannel.readInbound();
        System.out.println(resBuf.getShort(0));
        System.out.println(resBuf);
        System.out.println(new String(ByteBufUtil.getBytes(resBuf)));
    }
}
