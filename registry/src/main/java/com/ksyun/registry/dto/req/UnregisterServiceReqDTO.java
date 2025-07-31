package com.ksyun.registry.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务注销请求实体
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnregisterServiceReqDTO {

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
