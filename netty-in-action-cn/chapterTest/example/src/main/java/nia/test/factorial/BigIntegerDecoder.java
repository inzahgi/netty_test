package nia.test.factorial;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;

import java.math.BigInteger;
import java.util.List;

public class BigIntegerDecoder extends ByteToMessageDecoder{

    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        if(in.readableBytes() < 5){
            return;
        }

        in.markReaderIndex();

        int magicNum = in.readUnsignedByte();
        if(magicNum != 'F'){
            System.out.println("data package is error");
            in.resetReaderIndex();
            throw new CorruptedFrameException("error magic num : " + magicNum);
        }

        int dataLen = in.readInt();
        if(dataLen < in.readableBytes()){
            System.out.println("the byteBuf readableBytes len is short than dataLen");
            in.resetReaderIndex();
            return;
        }

        byte[] data = new byte[dataLen];
        in.readBytes(data);

        out.add(data);
    }
}
