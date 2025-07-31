package com.ksyun.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户端信息返回实体
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientInfoRespDTO {

    private String error;

    private String result;
}
