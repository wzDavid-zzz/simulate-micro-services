package com.ksyun.registry.dto.req;

import lombok.Data;

/**
 * 服务注册请求实体
 */

@Data
public class RegisterServiceReqDTO {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务唯一标识
     */
    private String serviceId;

    /**
     * ip地址
     */
    private String ipAddress;

    /**
     * 端口
     */
    private Integer port;
}
