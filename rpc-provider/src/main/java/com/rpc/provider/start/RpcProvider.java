package com.rpc.provider.start;

import com.rpc.provider.annotation.RpcService;
import com.rpc.provider.enums.RegistryTypeEnum;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * initialize the interface that should to provider,and register to the register center
 *
 * @author yanglin
 * @date 2023/1/4 11:02
 */
@Slf4j
@AllArgsConstructor
public class RpcProvider implements InitializingBean, BeanPostProcessor {

    private int servicePort;

    private String registryAddr;

    private RegistryTypeEnum registryType;

    /**
     * the cache map that storage rpcService
     */
    private final Map<String,Object> rpcServiceCacheMap = new HashMap<>();

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
        // must use sync to start server,because netty will block the thread to receive client request.
        // and that will block the bean initialization
        new Thread(()->{
            try {
                start();
            } catch (Exception e) {
                log.error("start server error,error:{}",e.getMessage());
            }
        }).start();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // register all method that has RpcService annotation to registry center
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService!=null){
            String serviceName = rpcService.serviceInterface().getName();
            String serviceVersion = rpcService.serviceVersion();
            try {
                // todo register to the registry center
                rpcServiceCacheMap.put(serviceName+"-"+serviceVersion,bean);
            }catch (Exception e){
                log.error("register rpcService to registry center error,serviceName:{},serviceVersion:{},error:{}",
                        serviceName,serviceVersion,e.getMessage());
            }
            log.info("register rpcService to registry center success,serviceName:{},serviceVersion:{}",serviceName,serviceVersion);
        }
        return bean;
    }
}
