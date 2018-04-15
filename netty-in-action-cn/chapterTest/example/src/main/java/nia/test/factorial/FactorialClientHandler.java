package nia.test.factorial;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {
    private ChannelHandlerContext ctx;
    private int receivedMessages;
    private int next = 1;
    final BlockingQueue<BigInteger> answear = new LinkedBlockingQueue<BigInteger>();

    public BigInteger getFactorial(){
        boolean interrupted = false;
        try{
            for(;;){
                try {

                    return answear.take();
                }catch (InterruptedException ingore){
                    interrupted = true;
                }
            }
        } finally {
            if(interrupted){
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        sendNumbers();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, final BigInteger num) throws Exception {
        receivedMessages++;
        if(receivedMessages == FactorialClient.COUNT){
            ctx.channel().close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    boolean offserd = answear.offer(num);
                    assert offserd;
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendNumbers(){
        ChannelFuture future = null;
        for(int i = 0; i<4096&&next<=FactorialClient.COUNT; i++){
            future = ctx.write(Integer.valueOf(next));
            next++;
        }
        if(next <= FactorialClient.COUNT){
            assert future != null;
            future.addListener(numberSender);
        }
        ctx.flush();
    }

    private final ChannelFutureListener numberSender = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture channelFuture) throws Exception {
            if(channelFuture.isSuccess()){
                sendNumbers();
            }else {
                channelFuture.cause().printStackTrace();
                channelFuture.channel();
            }
        }
    };


}
