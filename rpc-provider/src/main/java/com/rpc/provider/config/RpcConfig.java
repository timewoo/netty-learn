package com.rpc.provider.config;

import com.rpc.provider.start.RpcProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author yanglin
 * @date 2023/1/12 11:43
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
@Slf4j
public class RpcConfig {

    @Resource
    private RpcProperties properties;

    @Bean
    public RpcProvider init() {
        log.info("servicePort:{},registryAddr:{},registryType:{}", properties.getServicePort(),
                properties.getRegistryAddr(), properties.getRegistryType());
        return new RpcProvider(properties.getServicePort(), properties.getRegistryAddr(), properties.getRegistryType());
    }
}
