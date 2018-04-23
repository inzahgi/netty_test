package nia.test.factorial;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class FactorialClient {

    static final boolean hasSSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "8322"));
    static final int COUNT = Integer.parseInt(System.getProperty("count", "1000"));

    public static void main(String[] args) throws Exception {
        final SslContext sslCtx;
        if(hasSSL){
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        }else {
            sslCtx = null;
        }

        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(worker)
                    .channel(NioSocketChannel.class)
                    .handler(new FactorialClientInitializer(sslCtx));

            ChannelFuture future = b.connect(HOST, PORT).sync();

            FactorialClientHandler handler =
                    (FactorialClientHandler) future.channel().pipeline().last();
            System.out.format("the factorial of %d is %d",
                    FactorialClient.COUNT, handler.getFactorial());

        }finally {
            worker.shutdownGracefully();
        }

    }

}
