package com.rpc.provider.config;

import com.rpc.provider.enums.RegistryTypeEnum;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author yanglin
 * @date 2023/1/11 14:25
 */
@Data
@ConfigurationProperties("rpc")
public class RpcProperties {

    private int servicePort;

    private String registryAddr;

    private RegistryTypeEnum registryType;
}
