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
        FileDownloadEntity e;
        int type = buf.readInt();
        switch (type){
            case 0: e = decodeRequestStart(buf);break;
            case 1: e = decodeResponseStart(buf);break;
            case 2: e = decodeRequestBlock(buf);break;
            case 3: e = decodeResponseBlock(buf);break;
            case 4: e = decodeRequestEnd(buf);break;
            case 5: e = decodeResponseEnd(buf);break;
            default: return;
        }
        e.setHeadType(type);
        list.add(e);

    }

    public FileDownloadEntity decodeRequestStart(ByteBuf buf){
        FileDownloadEntity e = new FileDownloadEntity();
        int nameLen = buf.readInt();
        byte[] nameArray = new byte[nameLen];
        buf.readBytes(nameArray);
        e.setFileName(new String(nameArray));
        return e;
    }

    public FileDownloadEntity decodeResponseStart(ByteBuf buf){
        FileDownloadEntity e = new FileDownloadEntity();
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

    public FileDownloadEntity decodeRequestBlock(ByteBuf buf){
        FileDownloadEntity e = new FileDownloadEntity();
        int nameLen = buf.readInt();
        byte[] nameArray = new byte[nameLen];
        buf.readBytes(nameArray);
        e.setFileName(new String(nameArray));
        int fileBlockCurNo = buf.readInt();
        e.setFileBlockCurNo(fileBlockCurNo);
        return e;
    }

    public FileDownloadEntity decodeResponseBlock(ByteBuf buf){
        FileDownloadEntity e = new FileDownloadEntity();
        int fileBlockNo = buf.readInt();
        e.setFileBlockCurNo(fileBlockNo);
        long start = buf.readLong();
        long end = buf.readLong();
        e.setBlockStartPos(start);
        e.setBlockEndPos(end);
        int fileBlockLen = buf.readInt();
        byte[] fileBlock = new byte[fileBlockLen];
        e.setFileBlock(fileBlock);

        return e;
    }

    public FileDownloadEntity decodeRequestEnd(ByteBuf buf){
        FileDownloadEntity e = new FileDownloadEntity();
        int nameLen = buf.readInt();
        byte[] nameArray = new byte[nameLen];
        buf.readBytes(nameArray);
        e.setFileName(new String(nameArray));
        return e;

    }

    public FileDownloadEntity decodeResponseEnd(ByteBuf buf){
        FileDownloadEntity e = new FileDownloadEntity();
        e.setEndInfo(buf.toString(CharsetUtil.UTF_8));
        return e;
    }

}
