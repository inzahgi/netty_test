package nia.test.factorial;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;

public class FactorialClientInitializer  extends ChannelInitializer<SocketChannel>{
    private final SslContext sslCtx;

    public FactorialClientInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();

        if(sslCtx != null){
            cp.addLast(sslCtx.newHandler(socketChannel.alloc(), FactorialClient.HOST, FactorialClient.PORT));
        }

        cp.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        cp.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        cp.addLast(new BigIntegerDecoder());
        cp.addLast(new NumberEncoder());

        cp.addLast(new FactorialClientHandler());
    }
}
