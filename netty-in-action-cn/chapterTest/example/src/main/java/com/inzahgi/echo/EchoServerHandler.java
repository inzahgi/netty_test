package com.inzahgi.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Date;

@ChannelHandler.Sharable
public class EchoServerHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("=========== active =============" + (new Date()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        ByteBuf response = Unpooled.buffer(1024);
        ByteBuf t = (ByteBuf)o;
        System.out.println("line = 24 \n" + t);
        byte[] contentArray= null;
        if(t.isReadable()){
            contentArray = new byte[t.readableBytes()];
            t.readBytes(contentArray);
            response.writeBytes(contentArray);
        }
        System.out.println(new String(contentArray));

        //System.out.println(response);
        ctx.writeAndFlush(response);

        //t.release();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("+++++++++  inactive  ++++++++++++++\n");
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
