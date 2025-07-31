package com.ksyun.client.remote;

import com.ksyun.client.common.convention.result.Result;
import com.ksyun.client.config.OpenFeignConfiguration;
import com.ksyun.client.remote.dto.resp.TimeRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 时间服务远程调用
 */

@FeignClient(
        value = "time-service",
        url = "http://localhost:8080",
        configuration = OpenFeignConfiguration.class
)
public interface TimeRemoteService {

    @RequestMapping(value = "/api/getDateTime", method = RequestMethod.GET)
    Result<TimeRespDTO> getCurrentDateTime(@RequestParam("style") String style);
}
