package nia.test.http.websocketx.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;


public class WebSocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame>{
    private static final Logger logger = LoggerFactory.getLogger(WebSocketFrame.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        if(frame instanceof TextWebSocketFrame){
            String request = ((TextWebSocketFrame)frame).text();
            logger.info("{} received {}", ctx.channel(), request);
            ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase(Locale.US)));
        }else {
            String message = "unsupported framed frame type: " + frame.getClass().getName();
            throw new UnsupportedOperationException(message);
        }

    }
}
