package com.ksyun.registry.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务发现响应实体
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscoverServiceRespDTO {

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
