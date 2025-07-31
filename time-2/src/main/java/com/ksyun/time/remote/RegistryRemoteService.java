package com.ksyun.time.remote;

import com.ksyun.time.common.convention.result.Result;
import com.ksyun.time.config.OpenFeignConfiguration;
import com.ksyun.time.remote.dto.req.HeartBeatServiceReqDTO;
import com.ksyun.time.remote.dto.req.RegisterServiceReqDTO;
import com.ksyun.time.remote.dto.req.UnregisterServiceReqDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 注册中心服务远程调用
 */

@FeignClient(
        value = "registry-service",
        url = "${registry-service.remote-url:}",
        configuration = OpenFeignConfiguration.class
)

public interface RegistryRemoteService {

    @PostMapping("/api/register")
    Result<Void> register(@RequestBody RegisterServiceReqDTO requestParam);

    @PostMapping("/api/unregister")
    Result<Void> unregister(@RequestBody UnregisterServiceReqDTO requestParam);

    @PostMapping("/api/heartbeat")
    Result<Void> sendHeartbeat(@RequestBody HeartBeatServiceReqDTO requestParam);
}
