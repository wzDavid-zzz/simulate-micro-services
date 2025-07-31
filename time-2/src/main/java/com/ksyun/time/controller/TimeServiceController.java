package com.ksyun.time.controller;


import com.ksyun.time.common.convention.result.Result;
import com.ksyun.time.common.convention.result.Results;
import com.ksyun.time.dto.TimeRespDTO;
import com.ksyun.time.remote.RegistryRemoteService;
import com.ksyun.time.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 时间服务控制层
 */

@RestController
@RequiredArgsConstructor
public class TimeServiceController {

    private final TimeService timeService;
    private final RegistryRemoteService registryRemoteService;

    /**
     * 根据style参数获取当前时间
     * @param style 时间格式
     * @return 响应
     */
    @GetMapping("/api/getDateTime")
    public Result<TimeRespDTO> getCurrentDateTime(@RequestParam String style) {
        TimeRespDTO resp = timeService.getDateTime(style);
        return Results.success(resp);
    }
}
