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
            initDownloadFile(f, e.getFileLength());
            rdf = new RandomAccessFile(f, "rw");// 读写模式，如果文件不存在，会自动创建
        }catch (Exception e1){
            e1.printStackTrace();
        }
    }

    private boolean initDownloadFile(File file, long fileLen) throws Exception{
        FileOutputStream outSTr = new FileOutputStream(file);
        BufferedOutputStream Buff = new BufferedOutputStream(outSTr);
        long begin0 = System.currentTimeMillis();
        byte[] ar = new byte[1024];
        int count = (int)(fileLen/1024);
        int lastByte = (int)(fileLen%1024);
        for (int i = 0; i < count; i++) {
            Buff.write(ar);
        }
        if(lastByte != 0){
            Buff.write(new byte[lastByte]);
        }
        Buff.flush();
        Buff.close();
        long end0 = System.currentTimeMillis();
        System.out.println("BufferedOutputStream执行耗时:" + (end0 - begin0) + " 毫秒");
        return true;
    }

    private void getFileContent(FileDownloadEntity e){
        fileStatus.getMap().put(e.getFileBlockCurNo(), e);
        boolean writeFlag = false;
        if(fileStatus.getMap().size() > 10){
            int i = 0;
            int lastBlockNum=-1;
            for(Map.Entry<Integer, FileDownloadEntity> entry : fileStatus.getMap().entrySet()){
                int b = entry.getValue().getFileBlockCurNo();
                if(b - lastBlockNum == 1 ){
                    i++;
                }
                lastBlockNum = b;
            }
            if(i >= 10){
                writeFlag = true;
            }
        }

        if(writeFlag){
            writeFile(fileStatus);
        }

    }
    private void writeFile(FileDownloadStatus fds){
        int startKey = 0;
        int i = 0;
        int lastBlockNum=-10;
        for(Map.Entry<Integer, FileDownloadEntity> entry : fileStatus.getMap().entrySet()){
            int b = entry.getValue().getFileBlockCurNo();
            if(b - lastBlockNum != 1 && i < 10){
                startKey = entry.getKey();
            }else {
                i++;
            }
            lastBlockNum = b;

        }
        try {
            for (int j = startKey; j < startKey+i ; j++) {
                Map<Integer, FileDownloadEntity> map = fileStatus.getMap();
                FileDownloadEntity e = map.get(j);
            }
            //rdf.write(e.getFileBlock(), (int) e.getBlockStartPos(), e.getFileBlock().length);

        }catch (java.io.IOException e1){
            e1.printStackTrace();
        }

    }
}
