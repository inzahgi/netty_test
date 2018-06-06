package com.inzahgi.file;


import com.inzahgi.file.module.FileDownloadEntity;
import com.inzahgi.file.module.FileDownloadStatus;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

import java.util.concurrent.atomic.AtomicInteger;

@ChannelHandler.Sharable
public class FileServerHandler extends SimpleChannelInboundHandler<FileDownloadEntity> {

    private final String filePath = ".";
    private FileDownloadStatus fds = null;
    private RandomAccessFile raf = null;
    private AtomicInteger count = new AtomicInteger(1);
    private int total = 0;


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileDownloadEntity e) throws Exception {
        FileDownloadEntity responseEntity = new FileDownloadEntity();
        switch (e.getHeadType()){
            case 0: getFileInfo(e);break;
            case 1: getFileBlock(e)break;
            case 2: getFileEndInfo()break;
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

    private FileDownloadEntity getFileInfo(FileDownloadEntity e){
        FileDownloadEntity resEntity = new FileDownloadEntity();
        File f = new File(e.getFileName());
        if(f == null || f.exists() == false){
            return null;
        }
        resEntity.setHeadType(FileDownloadEntity.HEAD_TYPE.FILE_INFO.getType());
        resEntity.setFileName(e.getFileName());
        resEntity.setFileLength(f.length());
        //resEntity.setMaxFileBlockLength();
        total = f.length()/ resEntity.getMaxFileBlockLength() +
             f.length()%resEntity.getMaxFileBlockLength()==0?0:1;
        resEntity.setFileBlockTotal(total);
        resEntity.setMd5(getFileMd5(e.getFilePath()));
        try {
            raf = new RandomAccessFile(e.getFilePath(), "rw");
        }catch (java.io.IOException e0){
            e0.printStackTrace();
        }
        return resEntity;
    }

    private FileDownloadEntity getFileBlock(FileDownloadEntity e){
        FileDownloadEntity resEntity = new FileDownloadEntity();
        resEntity.setHeadType(FileDownloadEntity.HEAD_TYPE.FILE_BLOCK.getType());
        resEntity.setFileBlockCurNo(count.get());
        try {
            long startIndex = count.get() * resEntity.getMaxFileBlockLength();
            long endIndex = count.get() == total ?
                    raf.length() : startIndex + resEntity.getMaxFileBlockLength();
            raf.seek(startIndex);
            byte[] fileBlock = new byte[(int)(endIndex -startIndex)];
            raf.read(fileBlock);
            resEntity.setBlockStartPos(startIndex);
            resEntity.setBlockEndPos(endIndex);
            resEntity.setFileBlock(fileBlock);
        }catch (java.io.IOException e1){
            e1.printStackTrace();
        }
        return resEntity;
    }

    private FileDownloadEntity getFileEndInfo(){
        FileDownloadEntity resEntity = new FileDownloadEntity();
        resEntity.setHeadType(FileDownloadEntity.HEAD_TYPE.FILE_END.getType());
        resEntity.setEndInfo("download it!");
    }

    private String getFileMd5(String filePath){
        File f = new File(filePath);
        if(f == null || f.exists() == false){
            return null;
        }
        try {
            return DigestUtils.md5Hex(new FileInputStream(f));
        }catch (java.io.IOException e0){
            e0.printStackTrace();
        }
        return null;
    }



}
