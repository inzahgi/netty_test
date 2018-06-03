package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import com.inzahgi.file.module.FileDownloadStatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class FileClientHandler extends SimpleChannelInboundHandler<FileDownloadEntity>{

    private String fileName = "test.txt";
    private String filePath = "";
    private FileDownloadStatus fds = null;
    private RandomAccessFile raf = null ;


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        FileDownloadEntity e = new FileDownloadEntity();
        e.setFileName(fileName);
        e.setHeadType(FileDownloadEntity.HEAD_TYPE.FIND_FILE.getType());
        ctx.writeAndFlush(e);
        //ctx.writeAndFlush(Unpooled.copiedBuffer(fileName, CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileDownloadEntity e) throws Exception {
        switch (e.getHeadType()){
                case 1: initDownload(e);break;
                case 2: saveFileBlock(e);break;
                case 3: finishDownload(e);break;
                default: return;
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

    public void saveFileBlock(FileDownloadEntity e){
        fds.getMap().put(e.getFileBlockCurNo(), e);
        calcFileIndex(e);
        if(fds.getEndIndex() - fds.getStartIndex() > 10) {
            saveToFile(false);
        }
    }

    public void finishDownload(FileDownloadEntity e){
        try{
            raf.write(e.getFileBlock());
            raf.close();
        }catch (java.io.IOException e1){
            e1.printStackTrace();
        }
    }

    private void calcFileIndex(FileDownloadEntity e){
        if(fds.getStartIndex() == -1 && fds.getEndIndex() == -1){
            fds.setStartIndex(e.getFileBlockCurNo());
            fds.setEndIndex(e.getFileBlockCurNo());
            return;
        }
        if( e.getFileBlockCurNo() - fds.getEndIndex() == 1){
            fds.setEndIndex(e.getFileBlockCurNo());
        }
        while(true){
            int nextIndex = fds.getEndIndex() + 1;
            if(fds.getMap().get(nextIndex) != null){
                fds.setEndIndex(nextIndex);
            }
            break;
        }

    }

    private void saveToFile(boolean isContainLast){
        int start = fds.getStartIndex();
        int end = fds.getEndIndex();
        if(isContainLast) {
            end++;
        }
        //byte[]  writeBuf = new byte[(end-start)*(int)fds.getMaxFileBlockLength()];
        while(start > end){
            FileDownloadEntity e = fds.getMap().get(start);
            try {
                raf.write(e.getFileBlock());
            }catch (java.io.IOException e1){
                e1.printStackTrace();
            }
            start++;
        }
    }



}
