package nia.test.http.upload;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslContext;

public class HttpUploadServerInitializer extends ChannelInitializer<SocketChannel>{
    private final SslContext sslCtx;

    public HttpUploadServerInitializer(SslContext sslCtx){
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        if(sslCtx != null){
            pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
        }

        pipeline.addLast(new HttpRequestDecoder());
        pipeline.addLast(new HttpResponseEncoder());

        pipeline.addLast(new HttpContentCompressor());

        pipeline.addLast(new HttpUploadServerHandler());
    }




}
