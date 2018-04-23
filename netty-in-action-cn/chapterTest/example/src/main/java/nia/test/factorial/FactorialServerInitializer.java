package nia.test.factorial;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import io.netty.handler.ssl.SslContext;


public class FactorialServerInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;

    public FactorialServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
        if(sslCtx != null){
            cp.addLast(sslCtx.newHandler(ch.alloc()));
        }

        cp.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));
        cp.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));

        cp.addLast(new BigIntegerDecoder());
        cp.addLast(new NumberEncoder());

        cp.addLast(new FactorialServerHandler());
    }
}
