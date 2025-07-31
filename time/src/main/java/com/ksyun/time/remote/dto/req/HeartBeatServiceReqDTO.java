package com.ksyun.time.remote.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务心跳请求实体
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
