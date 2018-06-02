package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import com.inzahgi.file.module.FileDownloadStatus;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileServerHandler extends SimpleChannelInboundHandler<Object> {

    private final String filePath = ".";
    private FileDownloadStatus fds = null;
    private RandomAccessFile raf = null;



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }



}
