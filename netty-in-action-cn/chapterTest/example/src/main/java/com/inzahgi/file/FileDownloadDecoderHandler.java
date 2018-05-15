package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.CharsetUtil;

import java.util.List;

public class FileDownloadDecoderHandler extends MessageToMessageDecoder<Object> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        ByteBuf buf = (ByteBuf)o;
        FileDownloadEntity e = new FileDownloadEntity();
        e.setHeadType(buf.readInt());
        switch (e.getHeadType()){
            case 1: break;
            case 2: break;
            case 3: break;
            default: return;
        }


    }

    public FileDownloadEntity decodeFileStartInfo(FileDownloadEntity e, ByteBuf buf){
        int nameLen = buf.readInt();
        byte[] nameArray = new byte[nameLen];
        buf.readBytes(nameArray);
        e.setFileName(new String(nameArray));
        long fileLen = buf.readLong();
        e.setFileLength(fileLen);
        long maxFileBlockLen = buf.readLong();
        e.setMaxFileBlockLength(maxFileBlockLen);
        int fileBlockTotal = buf.readInt();
        e.setFileBlockTotal(fileBlockTotal);
        int md5Len = buf.readInt();
        byte[] md5Array = new byte[md5Len];
        buf.readBytes(md5Array);
        e.setMd5(new String(md5Array));

        return e;
    }

    public FileDownloadEntity decodeFileBlock(FileDownloadEntity e, ByteBuf buf){
        int fileBlockNo = buf.readInt();
        e.setFileBlockCurNo(fileBlockNo);
        long start = buf.readLong();
        long end = buf.readLong();
        int fileBlockLen = buf.readInt();
        byte[] fileBlock = new byte[fileBlockLen];
        e.setFileBlock(fileBlock);

        return e;
    }

    public FileDownloadEntity decodeFileEndInfo(FileDownloadEntity e, ByteBuf buf){
        e.setEndInfo(buf.toString(CharsetUtil.UTF_8));
        return e;
    }

}
