package com.ksyun.client.remote;

import com.ksyun.client.common.convention.result.Result;
import com.ksyun.client.config.OpenFeignConfiguration;
import com.ksyun.client.remote.dto.req.HeartBeatServiceReqDTO;
import com.ksyun.client.remote.dto.req.RegisterServiceReqDTO;
import com.ksyun.client.remote.dto.req.UnregisterServiceReqDTO;
import com.ksyun.client.remote.dto.resp.DiscoverServiceRespDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

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

    @GetMapping("/api/discovery")
    Result<List<DiscoverServiceRespDTO>> discovery(@RequestParam(value = "name", required = false) String serviceName);

}
