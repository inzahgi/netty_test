package com.inzahgi.file;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

public class FileClientMain {

    private static int PORT = 12345;
    private static String HOST = "127.0.0.1";

    private void start(String host, int port){
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(worker)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline cp = socketChannel.pipeline();
                            cp.addLast(new LengthFieldBasedFrameDecoder(1024, 0, 2));
                            cp.addLast(new LengthFieldPrepender(4));
                            cp.addLast(new FileClientHandler());
                        }
                    });
            ChannelFuture future = b.connect(new InetSocketAddress(host, port)).sync();
            future.channel().closeFuture().sync();
            //this.wait();

        }catch (Exception e) {
            e.printStackTrace();
        }finally{
            worker.shutdownGracefully();
        }
    }

    public static void main(String[] args) {

        FileClientMain test = new FileClientMain();
        test.start(HOST, PORT);
    }
}
