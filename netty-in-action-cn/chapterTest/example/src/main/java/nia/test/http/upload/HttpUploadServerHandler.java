package nia.test.http.upload;

import com.sun.org.apache.bcel.internal.classfile.AttributeReader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.netty.buffer.Unpooled.copiedBuffer;

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
//                    writeResponse(ctx.channel);
//                    ctx.channel.cloe();
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

    private void reset(){
        request = null;

        decoder.destroy();
        decoder = null;
    }

    private void readHttpDataChunkByChunk(){
        try{
            while(decoder.hasNext()){
                InterfaceHttpData data = decoder.next();
                if(data != null){
                    if(partialContent == data){
                        logger.info(" 100% (FinalSize: " + partialContent.length() + ")");
                        partialContent = null;
                    }
                    try{
                        writeHttpData(data);
                    } finally {
                        data.release();
                    }
                }
            }
            InterfaceHttpData data = decoder.currentPartialHttpData();
            if(data != null){
                StringBuilder builder = new StringBuilder();
                if(partialContent == null){
                    partialContent = (HttpData) data;
                    if(partialContent instanceof FileUpload){
                        builder.append("Start fileUpload: ")
                                .append(((FileUpload)partialContent).getFilename())
                                .append(" ");
                    }else {
                        builder.append("Start Attribute: ")
                                .append(partialContent.getName()).append(" ");
                    }
                    builder.append("DefinedSize: ").append(partialContent.definedLength())
                            .append(")");
                }
                if(partialContent.definedLength() > 0){
                    builder.append(" ").append(partialContent.length() *100 / partialContent.definedLength())
                            .append("% ");
                    logger.info(builder.toString());
                }else {
                    builder.append(" ").append(partialContent.length()).append(" ");
                    logger.info(builder.toString());
                }
            }
        }catch (HttpPostRequestDecoder.EndOfDataDecoderException e1){
            responseContent.append("\r\n\r\nEND OF CONTENT CHUNK BY CHUNK\r\n\r\n");
        }
    }

    private void writeHttpData(InterfaceHttpData data){
        if(data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute){
            Attribute attribute = (Attribute) data;
            String value;
            try{
                value = attribute.getValue();
            }catch (IOException e1){
                e1.printStackTrace();
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name()
                  + ": " + attribute.getName() + " Error while reading value: "
                        + e1.getMessage() + "\r\n");
                return;
            }
            if (value.length() > 100){
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name()
                        + ": " + attribute.getName() + " data too long\r\n");
            }else {
                responseContent.append("\r\nBODY Attribute: " + attribute.getHttpDataType().name()
                        + ": " + attribute + "\r\n");
            }
        }else{
            responseContent.append("\r\nBODY FileUpload: " + data.getHttpDataType().name()
                 + ": " + data + "\r\n");
            if(data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload){
                FileUpload fileUpload = (FileUpload) data;
                if(fileUpload.isCompleted()){
                    if(fileUpload.length() < 10000){
                        responseContent.append("\tContent of file\r\n");
                        try{
                            responseContent.append(fileUpload.getString(fileUpload.getCharset()));
                        }catch (IOException e1){
                            e1.printStackTrace();
                        }
                        responseContent.append("\r\n");
                    }else{
                        responseContent.append("\tFile too long to be printed out:" + fileUpload.length()
                        + "\r\n");
                    }
                }else {
                    responseContent.append("\tFile to be continued but should not!\r\n");
                }
            }
        }
    }

    private void writeResponse(Channel channel){
        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        responseContent.setLength(0);

        boolean close = request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE, true)
                                || request.protocolVersion().equals(HttpVersion.HTTP_1_0)
                && !request.headers().contains(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE, true);

        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/pain;charset=UTF-8");
        if(!close){
            response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());
        }

        Set<Cookie> cookies;
        String value = request.headers().get(HttpHeaderNames.COOKIE);
        if(value == null){
            cookies = Collections.emptySet();
        }else {
            cookies = ServerCookieDecoder.STRICT.decode(value);
        }
        if(!cookies.isEmpty()){
            for (Cookie cookie : cookies){
                //response.headers().add(HttpHeaderNames.SET_COOKIE, ServerCookieDecoder.STRICT.encode(cookie));
            }
        }

        ChannelFuture future = channel.writeAndFlush(response);
        if(close) {
            future.addListener(ChannelFutureListener.CLOSE);

        }

    }


    private void writeMenu(ChannelHandlerContext ctx) {
        // print several HTML forms
        // Convert the response content to a ChannelBuffer.
        responseContent.setLength(0);

        // create Pseudo Menu
        responseContent.append("<html>");
        responseContent.append("<head>");
        responseContent.append("<title>Netty Test Form</title>\r\n");
        responseContent.append("</head>\r\n");
        responseContent.append("<body bgcolor=white><style>td{font-size: 12pt;}</style>");

        responseContent.append("<table border=\"0\">");
        responseContent.append("<tr>");
        responseContent.append("<td>");
        responseContent.append("<h1>Netty Test Form</h1>");
        responseContent.append("Choose one FORM");
        responseContent.append("</td>");
        responseContent.append("</tr>");
        responseContent.append("</table>\r\n");

        // GET
        responseContent.append("<CENTER>GET FORM<HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");
        responseContent.append("<FORM ACTION=\"/formget\" METHOD=\"GET\">");
        responseContent.append("<input type=hidden name=getform value=\"GET\">");
        responseContent.append("<table border=\"0\">");
        responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"info\" size=10></td></tr>");
        responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"secondinfo\" size=20>");
        responseContent
                .append("<tr><td>Fill with value: <br> <textarea name=\"thirdinfo\" cols=40 rows=10></textarea>");
        responseContent.append("</td></tr>");
        responseContent.append("<tr><td><INPUT TYPE=\"submit\" NAME=\"Send\" VALUE=\"Send\"></INPUT></td>");
        responseContent.append("<td><INPUT TYPE=\"reset\" NAME=\"Clear\" VALUE=\"Clear\" ></INPUT></td></tr>");
        responseContent.append("</table></FORM>\r\n");
        responseContent.append("<CENTER><HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");

        // POST
        responseContent.append("<CENTER>POST FORM<HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");
        responseContent.append("<FORM ACTION=\"/formpost\" METHOD=\"POST\">");
        responseContent.append("<input type=hidden name=getform value=\"POST\">");
        responseContent.append("<table border=\"0\">");
        responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"info\" size=10></td></tr>");
        responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"secondinfo\" size=20>");
        responseContent
                .append("<tr><td>Fill with value: <br> <textarea name=\"thirdinfo\" cols=40 rows=10></textarea>");
        responseContent.append("<tr><td>Fill with file (only file name will be transmitted): <br> "
                + "<input type=file name=\"myfile\">");
        responseContent.append("</td></tr>");
        responseContent.append("<tr><td><INPUT TYPE=\"submit\" NAME=\"Send\" VALUE=\"Send\"></INPUT></td>");
        responseContent.append("<td><INPUT TYPE=\"reset\" NAME=\"Clear\" VALUE=\"Clear\" ></INPUT></td></tr>");
        responseContent.append("</table></FORM>\r\n");
        responseContent.append("<CENTER><HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");

        // POST with enctype="multipart/form-data"
        responseContent.append("<CENTER>POST MULTIPART FORM<HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");
        responseContent.append("<FORM ACTION=\"/formpostmultipart\" ENCTYPE=\"multipart/form-data\" METHOD=\"POST\">");
        responseContent.append("<input type=hidden name=getform value=\"POST\">");
        responseContent.append("<table border=\"0\">");
        responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"info\" size=10></td></tr>");
        responseContent.append("<tr><td>Fill with value: <br> <input type=text name=\"secondinfo\" size=20>");
        responseContent
                .append("<tr><td>Fill with value: <br> <textarea name=\"thirdinfo\" cols=40 rows=10></textarea>");
        responseContent.append("<tr><td>Fill with file: <br> <input type=file name=\"myfile\">");
        responseContent.append("</td></tr>");
        responseContent.append("<tr><td><INPUT TYPE=\"submit\" NAME=\"Send\" VALUE=\"Send\"></INPUT></td>");
        responseContent.append("<td><INPUT TYPE=\"reset\" NAME=\"Clear\" VALUE=\"Clear\" ></INPUT></td></tr>");
        responseContent.append("</table></FORM>\r\n");
        responseContent.append("<CENTER><HR WIDTH=\"75%\" NOSHADE color=\"blue\"></CENTER>");

        responseContent.append("</body>");
        responseContent.append("</html>");

        ByteBuf buf = copiedBuffer(responseContent.toString(), CharsetUtil.UTF_8);
        // Build the response object.
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buf);

        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, buf.readableBytes());

        // Write the response.
        ctx.channel().writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.log(Level.WARNING, responseContent.toString(), cause);
        ctx.channel().close();
    }


}
