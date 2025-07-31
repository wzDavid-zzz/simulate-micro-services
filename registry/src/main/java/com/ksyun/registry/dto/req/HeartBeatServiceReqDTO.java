package com.ksyun.registry.dto.req;

import lombok.Data;

/**
 * 服务心跳请求实体
 */

@Data
public class HeartBeatServiceReqDTO {

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
