package nia.test;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import nia.test.echo.EchoClient;
import nia.test.echo.EchoClientHandler;

public class JustTest {

    static final boolean hasSSL = System.getProperty("ssl") != null;
    static final String HOST = System.getProperty("host", "127.0.0.1");
    static final int PORT = Integer.parseInt(System.getProperty("port", "12345"));
    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));


    public static void main(String[] args) throws Exception{
        final SslContext sslCtx;
        if(hasSSL){
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build();
        }else {
            sslCtx = null;
        }

        final ByteBuf delimiter = Unpooled.copiedBuffer("\n".getBytes());

        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline cp = socketChannel.pipeline();
                            cp.addLast(new LineBasedFrameDecoder(1024));
                            cp.addLast(new StringDecoder());
                            cp.addLast(new SimpleChannelInboundHandler<String>() {
                                private ByteBuf firstMessage;


                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    System.out.println("active");
                                    ByteBuf firstMessage = Unpooled.buffer(JustTest.SIZE);
//                                    for(int i = 0; i < firstMessage.capacity()-1; i++){
//                                        firstMessage.writeByte('h');
//                                    }
                                    firstMessage.writeBytes("hello world\n".getBytes());
                                    //firstMessage.writeByte('\n');
                                    ctx.writeAndFlush(firstMessage);
                                    //ctx.writeAndFlush("sayHello\nsaygjfgfk\nfhjdhfsj\n");
//                                    ctx.writeAndFlush("sayHello\nsaygjfgfk\nfhjdhfsj\n1231231231231");

                                }

                                @Override
                                public void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
                                    //ctx.write(msg);
                                    System.out.println(msg);
                                }

                                @Override
                                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
                                    ctx.flush();
                                    //--------------------------------------------------------------
                                    System.out.println(" close active !!!!!!!!!!!!!!!!!!!!!!!!");
                                    ctx.close();
                                }

                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    cause.printStackTrace();
                                    ctx.close();
                                }
                            });
                        }
                    });
                    //.handler(new EchoClientHandler());

            ChannelFuture f = b.connect(HOST, PORT).sync();
            f.channel().closeFuture().sync();

        }finally {
            workerGroup.shutdownGracefully();
        }

    }
}
