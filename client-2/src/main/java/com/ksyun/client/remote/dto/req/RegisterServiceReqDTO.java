package com.ksyun.client.remote.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务注册请求实体
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
