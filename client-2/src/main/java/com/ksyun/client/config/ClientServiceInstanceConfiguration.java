package com.ksyun.client.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "client-service-instance")
public class ClientServiceInstanceConfiguration {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 端口号
     */
    private Integer port;

}
