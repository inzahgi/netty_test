package nia.test.http.helloworld;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.AsciiString;

public class HttpHelloWorldServerHandler extends ChannelInboundHandlerAdapter {

    private static final byte[] CONNTENT = {'H', 'e', 'l', 'l', 'o', ',', 'W', 'o', 'r', 'l', 'd'};

    private static final AsciiString CONTENT_TYPE = AsciiString.
}
