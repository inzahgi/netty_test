package nia.test.http.snoop;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;


import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class HttpSnoopServerHandler extends SimpleChannelInboundHandler<Object> {

    private HttpRequest request;
    private final StringBuilder buf = new StringBuilder();

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof HttpRequest){
            HttpRequest request = this.request = (HttpRequest)msg;

            if(HttpUtil.is100ContinueExpected(request)){
                send100Continue(ctx);
            }

            buf.setLength(0);
            buf.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
            buf.append("===================================\r\n");


            buf.append("VERSION: ")
                    .append(request.protocolVersion())
                    .append("\r\n");
            buf.append("HOSTNAME: ")
                    .append(request.headers().get(HttpHeaderNames.HOST, "null"))
                    .append("\r\n");
            buf.append("REQUET_URI:").append(request.uri()).append("\r\n");

            HttpHeaders headers = request.headers();
            if(!headers.isEmpty()){
                for(Map.Entry<String, String>h : headers) {
                    CharSequence key = h.getKey();
                    CharSequence value = h.getValue();
                    buf.append("HEADER:")
                            .append(key)
                            .append("=")
                            .append(value)
                            .append("\r\n");
                }
                buf.append("\r\n");
            }

            QueryStringDecoder queryStringDecoder = new QueryStringDecoder();
            Map<String, List<String>> params = queryStringDecoder.parameters();
            if(!params.isEmpty()){
                for(Map.Entry<String, List<String>> p: params.entrySet()){
                    String key = p.getKey();
                    List<String> vals = p.getValue();
                    for(String val : vals){
                        buf.append("PARAM:")
                                .append(key)
                                .append("=")
                                .append(val)
                                .append("\r\n");
                    }
                }
                buf.append("\r\n");
            }

            appendDecoderResult(buf, request);

        }


        
    }
}
