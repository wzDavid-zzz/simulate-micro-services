package com.ksyun.time.service;

import com.ksyun.time.dto.TimeRespDTO;

/**
 * 时间服务接口层
 */
public interface TimeService {
    TimeRespDTO getDateTime(String style);
}
