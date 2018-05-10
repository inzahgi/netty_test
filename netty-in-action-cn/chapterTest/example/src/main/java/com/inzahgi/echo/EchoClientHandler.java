package com.inzahgi.echo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class EchoClientHandler extends SimpleChannelInboundHandler<Object> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer("from client hello world".getBytes()));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object s) throws Exception {
        ByteBuf tmp = (ByteBuf)s;
        System.out.println("channelRead0: ");
        if(tmp.isReadable()){
            byte[] ar = new byte[tmp.readableBytes()];
            tmp.readBytes(ar);
            System.out.println(new String(ar));
        }
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
