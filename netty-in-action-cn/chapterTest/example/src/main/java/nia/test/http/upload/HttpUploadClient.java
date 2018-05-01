package nia.test.http.upload;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.ClientCookieEncoder;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.multipart.*;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.internal.SocketUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.Map;

public class HttpUploadClient {

    private static final String BASE_URL =System.getProperty("baseUrl", "http://127.0.0.1:8080");
    private static final String FILE = System.getProperty("file", "upload.txt");

    public static void main(String[] args) throws Exception {
        String postSimple, postFile, get;
        if (BASE_URL.endsWith("/")) {
            postSimple = BASE_URL + "formpost";
            postFile = BASE_URL + "formpostmultipart";
            get = BASE_URL + "formget";
        } else {
            postSimple = BASE_URL + "/formpost";
            postFile = BASE_URL + "/formpostmultipart";
            get = BASE_URL + "formget";
        }
        URI uriSimple = new URI(postSimple);
        String scheme = uriSimple.getScheme() == null ? "http" : uriSimple.getScheme();
        String host = uriSimple.getHost() == null ? "127.0..0.1" : uriSimple.getHost();
        int port = uriSimple.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(scheme)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(scheme)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
            System.err.println("Only HTTP(S) is supported");
            return;
        }

        final boolean ssl = "https".equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        URI uriFile = new URI(postFile);
        File file = new File(FILE);
        if (!file.canRead()) {
            throw new FileNotFoundException(FILE);
        }

        EventLoopGroup group = new NioEventLoopGroup();

        HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

        DiskFileUpload.deleteOnExitTemporaryFile = true;
        DiskFileUpload.baseDirectory = null;
        DiskAttribute.deleteOnExitTemporaryFile = true;
        DiskAttribute.baseDirectory = null;

        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new HttpUploadClientInitializer(sslCtx));
            List<Map.Entry<String, String>> headers = formget(b, host, port, get, uriSimple);
            if (headers == null) {
                factory.cleanAllHttpData();
                return;
            }

            List<InterfaceHttpData> bodyList = formpost(b, host, port, uriSimple, file, factory, headers);
            if (bodyList == null) {
                factory.cleanAllHttpData();
                return;
            }

            formpostmultipart(b, host, uriFile, factory, headers, bodyList);
        } finally {
            group.shutdownGracefully();

            factory.cleanAllHttpData();
        }
    }

    private static List<Map.Entry<String, String>> formget(
            Bootstrap bootstrap, String host, int port, String get, URI uriSimple) throws Exception{
            Channel channel = bootstrap.connect(host, port).sync().channel();

            QueryStringEncoder encoder = new QueryStringEncoder(get);

            encoder.addParam("getform", "GET");
            encoder.addParam("info", "first value");
            encoder.addParam("secondinfo", "secondvalue xxxxx");

            encoder.addParam("thirdinfo", "third value\r\ntest second line\r\n\rnew line\r\n");
            encoder.addParam("Send", "Send");

            URI uriGet = new URI(encoder.toString());
            HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                            HttpMethod.GET, uriGet.toASCIIString());
            HttpHeaders headers = request.headers();
            headers.set(HttpHeaderNames.HOST, host);
            headers.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.CLOSE);
            headers.set(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP + "," + HttpHeaderValues.DEFLATE);

            headers.set(HttpHeaderNames.ACCEPT_CHARSET, "ISO-8859-1, utf-8;q=0.7,*;q=0.7");
            headers.set(HttpHeaderNames.ACCEPT_LANGUAGE, "zh-cmn-Hans");
            headers.set(HttpHeaderNames.REFERER, uriSimple.toString());
            headers.set(HttpHeaderNames.USER_AGENT, "Netty Simple Http Client side");
            headers.set(HttpHeaderNames.ACCEPT, "text/html, application/xhtml, application/xml;q=0.9,*/*;q=0.8");
            headers.set(
                    HttpHeaderNames.COOKIE, ClientCookieEncoder.STRICT.encode(
                            new DefaultCookie("my-cookie", "foo"),
                            new DefaultCookie("another-cookie", "bar")
                    )
            );

            channel.writeAndFlush(request);

            channel.closeFuture().sync();
            return headers.entries();
    }

    private static List<InterfaceHttpData> formpost(
            Bootstrap bootstrap,
            String host, int port, URI uriSimple, File file, HttpDataFactory factory,
            List<Map.Entry<String, String>> headers) throws Exception{

        ChannelFuture future = bootstrap.connect(SocketUtils.socketAddress(host, port));

        Channel channel = future.sync().channel();

        HttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, uriSimple.toASCIIString());

        HttpPostRequestEncoder bodyRequestEncoder =
                new HttpPostRequestEncoder(factory, request, false);

        for (Map.Entry<String, String> entry : headers){
            request.headers().set(entry.getKey(), entry.getValue());
        }

        bodyRequestEncoder.addBodyAttribute("getform", "POST");
        bodyRequestEncoder.addBodyAttribute("info", "first value");
        bodyRequestEncoder.addBodyAttribute("secondifo", "secondvalue xxxxx");
        bodyRequestEncoder.addBodyAttribute("thirdinfo", textArea);
        bodyRequestEncoder.addBodyAttribute("fourthinfo", textAreaLong);

    }








}
