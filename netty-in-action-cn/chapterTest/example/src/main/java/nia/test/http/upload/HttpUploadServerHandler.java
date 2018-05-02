package nia.test.http.upload;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject>{

    private static final Logger logger = Logger.getLogger(HttpUploadServer.class.getName());

    private HttpRequest request;

    private boolean readingChunks;

    private HttpData partialContent;

    private final StringBuilder responseContent = new StringBuilder();

    private static final HttpDataFactory factory=
                new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

    private HttpPostRequestDecoder decoder;

    static {
        DiskFileUpload.deleteOnExitTemporaryFile = true;

        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;

        DiskAttribute.baseDirectory = null;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(decoder != null){
            decoder.cleanFiles();
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
        if(msg instanceof  HttpRequest) {
            HttpRequest request = this.request = (HttpRequest)msg;
            URI uri = new URI(request.uri());
            if(!uri.getPath().startsWith("/form")){
                writeMenu(ctx);
                return;
            }
            responseContent.setLength(0);
            responseContent.append("WELCOME TO THE WILD WILD WEB SERVER\r\n");
            responseContent.append("===================================\r\n");
            responseContent.append("VERSION: " + request.protocolVersion().text() + "\r\n");

            responseContent.append("REQUEST_URI: " + request.uri() + "\r\n\r\n");

            responseContent.append("\r\n\r\n");

            for(Map.Entry<String, String> entry : request.headers()){
                responseContent.append("HEADER: " + entry.getKey() + '=' + entry.getValue() + "\r\n");
            }
            responseContent.append("\r\n\r\n");

            Set<Cookie> cookies;
            String value = request.headers().get(HttpHeaderNames.COOKIE);
            if(value == null){
                cookies = Collections.emptySet();
            }else {
                cookies = ServerCookieDecoder.STRICT.decode(value);
            }
            for (Cookie cookie : cookies){
                responseContent.append("COOKIE: " + cookie + "\r\n");
            }
            responseContent.append("\r\n\r\n");

            QueryStringDecoder decoderQuery = new QueryStringDecoder(request.uri());
            Map<String, List<String>> uriAttributes = decoderQuery.parameters();
            for(Map.Entry<String, List<String>> attr : uriAttributes.entrySet()){
                for(String attrVal : attr.getValue()){
                    responseContent.append("URI: " +attr.getKey() + '=' + attrVal + "\r\n");
                }
            }
            responseContent.append("\r\n\r\n");

            if(request.method().equals(HttpMethod.GET)){
                responseContent.append("\r\n\r\nEND OF GET CONTENT\r\n");
                return;
            }
            try{
                decoder = new HttpPostRequestDecoder(factory, request);
            }catch (HttpPostRequestDecoder.ErrorDataDecoderException e1){
                e1.printStackTrace();
                responseContent.append(e1.getMessage());
                writeResponse(ctx.channel());
                ctx.channel().close();
                return;
            }

            readingChunks = HttpUtil.isTransferEncodingChunked(request);
            responseContent.append("Is Chunked: " + readingChunks + "\r\n");
            responseContent.append("IsMultipart: " + decoder.isMultipart() + "\r\n");
            if(readingChunks){
                responseContent.append("Chunks: ");
                readingChunks = true;
            }
        }
        if(decoder != null){
            if(msg instanceof HttpContent){
                HttpContent chunk = (HttpContent)msg;
                try{
                    decoder.offer(chunk);
                }catch (HttpPostRequestDecoder.ErrorDataDecoderException e1){
                    e1.printStackTrace();
                    responseContent.append(e1.getMessage());
                    writeResponse(ctx.channel);
                    ctx.channel.cloe();
                    return;
                }
                responseContent.append('o');

                readHttpDataChunkByChunk();

                if(chunk instanceof LastHttpContent){
                    writeResponse(ctx.channel());
                    readingChunks = false;

                    reset();
                }
            }
        }else {
            writeResponse(ctx.channel());
        }


    }




}
