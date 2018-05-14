package com.inzahgi.file;

import com.inzahgi.file.module.FileDownloadEntity;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class FileDownloadDecoderHandler extends MessageToMessageDecoder<Object> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, Object o, List<Object> list) throws Exception {
        ByteBuf buf = (ByteBuf)o;
        FileDownloadEntity e = new FileDownloadEntity();
        e.setHeadType(buf.readInt());
        switch ()


    }
}
