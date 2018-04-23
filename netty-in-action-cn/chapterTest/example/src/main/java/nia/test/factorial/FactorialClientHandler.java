package nia.test.factorial;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {

    private ChannelHandlerContext ctx;
    private int receivedMessage = 1;
    private int next = 1;
    final BlockingQueue<BigInteger> answear = new LinkedTransferQueue<>();

    public BigInteger getFactorial(){
        for(;;){
            boolean isInterrputed = false;
            try{
                return answear.take();
            }catch (java.lang.InterruptedException e){
                isInterrputed = true;
            }finally {
                if(isInterrputed){
                    Thread.currentThread().interrupt();
                }
            }

        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        sendNums();
    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, final BigInteger msg) throws Exception {
        receivedMessage++;
        if(receivedMessage == FactorialClient.COUNT){
            ctx.close().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    boolean offerd = answear.offer(msg);
                    assert offerd;
                }
            });
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    private void sendNums(){
        ChannelFuture future = null;
        for (int i = 0; i < 4096 && next < FactorialClient.COUNT; i++) {
            future = ctx.write(Integer.valueOf(next));
            next++;
        }
        if(next < FactorialClient.COUNT){
            assert future != null;
            future.addListener(numSender);
        }
        ctx.flush();
    }

    private ChannelFutureListener numSender = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if(future.isSuccess()){
                sendNums();
            }else {
                future.cause().printStackTrace();
                future.channel().close();
            }
        }
    };

}
