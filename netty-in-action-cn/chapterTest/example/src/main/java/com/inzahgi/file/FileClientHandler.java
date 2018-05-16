package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import com.inzahgi.file.module.FileDownloadStatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

public class FileClientHandler extends SimpleChannelInboundHandler<FileDownloadEntity>{

    private String fileName = "test.txt";
    private String filePath = "";
    private FileDownloadStatus fileStatus = null;
    private RandomAccessFile rdf = null ;       // 声明RandomAccessFile类的对象
    //byte[] blockArray = new



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.copiedBuffer(fileName, CharsetUtil.UTF_8));
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileDownloadEntity e) throws Exception {
        switch (e.getHeadType()){
            case 1: initFileDownload(e);break;
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

    private void initFileDownload(FileDownloadEntity e){
        fileStatus = new FileDownloadStatus(e.getFileName(), ".\\", e.getFileLength(),
                        e.getMaxFileBlockLength(), e.getMd5());

        File f = new File("d:" + File.separator + "test.txt") ; // 指定要操作的文件
        try {
            rdf = new RandomAccessFile(f, "rw");// 读写模式，如果文件不存在，会自动创建
        }catch (java.io.FileNotFoundException e1){
            e1.printStackTrace();
        }
    }

    private void writeFile(FileDownloadEntity e){
        buf.writeBytes(e.getFileBlock());

    }
}
