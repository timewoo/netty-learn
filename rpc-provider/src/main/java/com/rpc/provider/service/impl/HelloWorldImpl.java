package com.rpc.provider.service.impl;

import com.rpc.provider.annotation.RpcService;
import com.rpc.provider.service.HelloWorld;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yanglin
 * @date 2023/1/12 12:12
 */
@Slf4j
@RpcService(serviceInterface = HelloWorld.class,serviceVersion = "1.0.1")
public class HelloWorldImpl implements HelloWorld {
    @Override
    public String hello(String name) {
        log.info("receive request,name:{}",name);
        return "hello "+name;
    }
}
