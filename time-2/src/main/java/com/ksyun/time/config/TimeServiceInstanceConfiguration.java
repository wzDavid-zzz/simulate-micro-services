package com.ksyun.time.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "time-service-instance")
public class TimeServiceInstanceConfiguration {

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
