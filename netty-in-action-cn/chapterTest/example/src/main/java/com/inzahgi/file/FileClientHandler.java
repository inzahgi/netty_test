package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

public class FileClientHandler extends SimpleChannelInboundHandler<FileDownloadEntity>{

    private String fileName = "test.txt";
    private String filePath = "";




    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer(fileName, CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileDownloadEntity e) throws Exception {
        switch (e.getHeadType()){
            case 1: break;
            case 2: break;
            case 3: break;
            default: return;
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
