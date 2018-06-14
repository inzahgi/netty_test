package com.inzahgi.file;

import com.inzahgi.file.module.ConstantStatus;
import com.inzahgi.file.module.FileDownloadEntity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
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
                case ConstantStatus.REQUEST_HEAD_TYPE.requestInfo : res = encodeRequestStart(e);break;
                case ConstantStatus.RESPON_HEAD_TYPE.responeInfo: res = encodeResponseStart(e);break;
                case ConstantStatus.REQUEST_HEAD_TYPE.requestBlock: res = encodeRequestBlock(e);break;
                case ConstantStatus.RESPON_HEAD_TYPE.responseBlock: res = encodeResponseBlock(e);break;
                case ConstantStatus.REQUEST_HEAD_TYPE.requestEnd: res = encodeRequestEnd(e);break;
                case ConstantStatus.RESPON_HEAD_TYPE.responseEnd:res = encodeResponseEnd(e);break;
                default: return;
            }
            list.add(res);
        }
    }

    public ByteBuf encodeRequestStart(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(256);
        return encodeFileNameAndPath(buf, e);

    }

    public ByteBuf encodeResponseStart(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(256);
        encodeFileNameAndPath(buf, e)
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
        encodeFileNameAndPath(buf, e)
                .writeInt(e.getFileBlockCurNo());
        return buf;
    }

    public ByteBuf encodeResponseBlock(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(1024+128);
        encodeFileNameAndPath(buf, e)
                .writeInt(e.getFileBlockCurNo())
                .writeLong(e.getBlockStartPos())
                .writeLong(e.getBlockEndPos())
                .writeInt(e.getFileBlock().length)
                .writeBytes(e.getFileBlock());
        return buf;
    }

    public ByteBuf encodeRequestEnd(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(128);
        return encodeFileNameAndPath(buf, e);

    }

    public ByteBuf encodeResponseEnd(FileDownloadEntity e){
        ByteBuf buf = Unpooled.buffer(128);
        encodeFileNameAndPath(buf, e)
                .writeBytes(e.getEndInfo().getBytes(CharsetUtil.UTF_8));
        return buf;
    }

    private ByteBuf encodeFileNameAndPath(ByteBuf buf, FileDownloadEntity e){
        byte[] nameBytes = e.getFileName().getBytes();
        byte[] filePathBytes = e.getFilePath().getBytes();
        buf.writeInt(e.getHeadType())
                .writeInt(nameBytes.length)
                .writeBytes(nameBytes)
                .writeInt(filePathBytes.length)
                .writeBytes(filePathBytes);
        return buf;
    }
}
