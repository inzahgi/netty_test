package nia.test.http.websocketx.client;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;

public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {

    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakerFuture;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker){
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture(){
        return handshakerFuture;
    }
}
