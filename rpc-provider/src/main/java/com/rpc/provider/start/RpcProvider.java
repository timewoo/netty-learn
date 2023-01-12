package com.rpc.provider.start;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * initialize the interface that should to provider,and register to the register center
 *
 * @author yanglin
 * @date 2023/1/4 11:02
 */
@Slf4j
@AllArgsConstructor
public class RpcProvider implements InitializingBean {

    private int servicePort;

    private void start() throws UnknownHostException, InterruptedException {
        // start the netty server
        String serverAddress = InetAddress.getLocalHost().getHostAddress();

        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup work = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // todo add encode,decode and other channel handler
                            socketChannel.pipeline();
                        }
                    })
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = serverBootstrap.bind(serverAddress, servicePort).sync();
            log.info("server start, addr:{},port:{}", serverAddress, servicePort);
            future.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            work.shutdownGracefully();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
