package com.inzahgi.file;


import com.inzahgi.file.module.FileDownloadEntity;
import com.inzahgi.file.module.FileDownloadStatus;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.Md5Crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class FileServerHandler extends SimpleChannelInboundHandler<FileDownloadEntity> {

    private final String filePath = ".";
    private FileDownloadStatus fds = null;
    private RandomAccessFile raf = null;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileDownloadEntity e) throws Exception {
        FileDownloadEntity responseEntity = new FileDownloadEntity();
        switch (e.getHeadType()){
            case 0: break;
            case 1: break;
            case 2: break;
            case 3: break;
            default:return;
        }
        channelHandlerContext.writeAndFlush(responseEntity);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();

    }

    public FileDownloadEntity getFileInfo(FileDownloadEntity e){
        FileDownloadEntity resEntity = new FileDownloadEntity();
        File f = new File(e.getFileName());
        if(f == null || f.exists() == false){
            return null;
        }
        resEntity.setHeadType(FileDownloadEntity.HEAD_TYPE.FILE_INFO.getType());
        resEntity.setFileName(e.getFileName());
        resEntity.setFileLength(f.length());
        //resEntity.setMaxFileBlockLength();
        int total = f.length()/ resEntity.getMaxFileBlockLength() +
             f.length()%resEntity.getMaxFileBlockLength()==0?0:1;
        resEntity.setFileBlockTotal(total);

        return resEntity;
    }

    public FileDownloadEntity getFileBlock(FileDownloadEntity e){
        FileDownloadEntity resEntity = new FileDownloadEntity();
        File f = new File(e.getFileName());
        if(f == null || f.exists() == false){
            return null;
        }
        try {
            String md5 = DigestUtils.md5Hex(new FileInputStream(f));
        }catch (java.io.IOException e0){
            e0.printStackTrace();
        }


    }



}
