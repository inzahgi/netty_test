package nia.test.factorial;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.math.BigInteger;

public class FactorialServerHandler extends SimpleChannelInboundHandler<BigInteger> {

    private BigInteger lastData = new BigInteger("1");
    private BigInteger factorialNum = new BigInteger("1");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        lastData = msg;
        factorialNum = factorialNum.multiply(msg);
        ctx.writeAndFlush(factorialNum);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.format("the last data is lastData = %s,  factorialNum = %s",
                lastData.toString(), factorialNum.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
