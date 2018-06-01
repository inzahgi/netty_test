package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import com.inzahgi.file.module.FileDownloadStatus;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.FileOutputStream;
import java.io.RandomAccessFile;

public class FileServerHandler extends SimpleChannelInboundHandler<Object> {

    private final String filePath = ".";
    private FileDownloadStatus fds = null;
    private RandomAccessFile raf = null;



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        if(o instanceof  FileDownloadEntity) {
            FileDownloadEntity e = (FileDownloadEntity) o;
            switch (e.getHeadType()){
                case 1: break;
                case 2: break;
                case 3: break;
                default: return;
            }

        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }

    public void initDownload(FileDownloadEntity e){
        try {
            raf = new RandomAccessFile(e.getFileName(), "rw");
            fds = new FileDownloadStatus(e.getFileName(), filePath, e.getFileLength(),
                    e.getMaxFileBlockLength(),e.getMd5());
        }catch (java.io.FileNotFoundException e0){
            e0.printStackTrace();
        }
    }


}
