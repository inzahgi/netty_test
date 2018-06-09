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
        return parseFileName(buf);
    }

    public FileDownloadEntity decodeResponseStart(ByteBuf buf){
        FileDownloadEntity e = parseFileName(buf);
        e.setFileLength(buf.readLong());
        e.setMaxFileBlockLength(buf.readLong());
        e.setFileBlockTotal(buf.readInt());
        byte[] md5Bytes = new byte[buf.readInt()];
        buf.readBytes(md5Bytes);
        e.setMd5(new String(md5Bytes));
        return e;
    }

    public FileDownloadEntity decodeRequestBlock(ByteBuf buf){
        FileDownloadEntity e = parseFileName(buf);
        e.setFileBlockCurNo(buf.readInt());
        return e;
    }

    public FileDownloadEntity decodeResponseBlock(ByteBuf buf){
        FileDownloadEntity e = parseFileName(buf);
        e.setFileBlockCurNo(buf.readInt());
        e.setBlockStartPos(buf.readLong());
        e.setBlockEndPos( buf.readLong());
        byte[] fileBlock = new byte[buf.readInt()];
        buf.readBytes(fileBlock);
        e.setFileBlock(fileBlock);
        return e;
    }

    public FileDownloadEntity decodeRequestEnd(ByteBuf buf){
        return parseFileName(buf);
    }

    public FileDownloadEntity decodeResponseEnd(ByteBuf buf){
        FileDownloadEntity e = parseFileName(buf);
        e.setEndInfo(buf.toString(CharsetUtil.UTF_8));
        return e;
    }

    private FileDownloadEntity parseFileName(ByteBuf buf){
        FileDownloadEntity e = new FileDownloadEntity();
        int nameLen = buf.readInt();
        byte[] nameBytes = new byte[nameLen];
        buf.readBytes(nameBytes);
        e.setFileName(new String(nameBytes));
        int filepathLen = buf.readInt();
        byte[] filePathBytes = new byte[filepathLen];
        buf.readBytes(filePathBytes);
        e.setFilePath(new String(filePathBytes));
        return e;
    }
}
