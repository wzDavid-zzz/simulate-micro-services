package com.ksyun.client.remote.dto.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取时间响应实体
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeRespDTO {

    /**
     * 时间
     */
    private String result;

    /**
     * 服务唯一标识
     */
    private String serviceId;
}
