package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class FileDownloadEncoderHandler extends MessageToMessageEncoder<FileDownloadEntity> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, FileDownloadEntity e, List<Object> list) throws Exception {
        if(e != null ){
            ByteBuf res;
            switch (e.getHeadType()){
                case 0: res = encodeRequestStart(e);break;
                case 1: res = encodeResponseStart(e);break;
                case 2: res = encodeRequestBlock(e);break;
                case 3: res = encodeResponseBlock(e);break;
                case 4: res = encodeRequestEnd(e);break;
                case 5:res = encodeResponseEnd(e);break;
                default: return;
            }
            list.add(res);
        }
    }

    public ByteBuf encodeRequestStart(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(256);
        byte[] nameArray = e.getFileName().getBytes();
        buf.writeInt(e.getHeadType())
                .writeInt(nameArray.length)
                .writeBytes(nameArray);
        return buf;

    }

    public ByteBuf encodeResponseStart(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(256);
        buf.writeInt(e.getHeadType());
        byte[] nameArray = e.getFileName().getBytes();
        buf.writeInt(e.getHeadType())
                .writeInt(nameArray.length)
                .writeBytes(nameArray)
                .writeLong(e.getFileLength())
                .writeLong(e.getMaxFileBlockLength())
                .writeInt(e.getFileBlockTotal());
        byte[] md5Array = e.getMd5().getBytes(CharsetUtil.UTF_8);
        buf.writeInt(md5Array.length)
                .writeBytes(e.getMd5().getBytes(CharsetUtil.UTF_8));
        return buf;
    }

    public ByteBuf encodeRequestBlock(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(256);
        byte[] nameArray = e.getFileName().getBytes();
        buf.writeInt(e.getHeadType())
                .writeInt(nameArray.length)
                .writeBytes(nameArray)
                .writeInt(e.getFileBlockCurNo());
        return buf;
    }

    public ByteBuf encodeResponseBlock(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(1024+128);
        buf.writeInt(e.getHeadType())
                .writeInt(e.getFileBlockCurNo())
                .writeLong(e.getBlockStartPos())
                .writeLong(e.getBlockEndPos())
                .writeInt(e.getFileBlock().length)
                .writeBytes(e.getFileBlock());
        return buf;
    }

    public ByteBuf encodeRequestEnd(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(128);
        byte[] nameArray = e.getFileName().getBytes();
        buf.writeInt(e.getHeadType())
                .writeInt(nameArray.length)
                .writeBytes(nameArray);
        return buf;

    }

    public ByteBuf encodeResponseEnd(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(128);
        buf.writeInt(e.getHeadType())
                .writeBytes(e.getEndInfo().getBytes(CharsetUtil.UTF_8));
        return buf;
    }
}
