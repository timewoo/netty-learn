package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import org.example.handler.client.HttpClientHandler;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author yanglin
 * @date 2022/12/16 17:41
 */
public class HttpClient {

    public void connect(String host, int port) throws InterruptedException, URISyntaxException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // connect之前初始化
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline()
                                    .addLast(new HttpResponseDecoder())// 解码 input
                                    .addLast(new HttpRequestEncoder())// 编码 output
                                    .addLast(new HttpClientHandler());// 自定义处理 input
                        }
                    });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            URI uri = new URI("http://127.0.0.1:8080");
            String content = "Hello World";
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1,
                    HttpMethod.GET, uri.toASCIIString(),
                    Unpooled.wrappedBuffer(content.getBytes(CharsetUtil.UTF_8)));
            request.headers().set(HttpHeaderNames.HOST, host);
            request.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            future.channel().write(request);
            future.channel().flush();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws URISyntaxException, InterruptedException {
        HttpClient httpClient = new HttpClient();
        httpClient.connect("127.0.0.1", 8080);
    }
}
