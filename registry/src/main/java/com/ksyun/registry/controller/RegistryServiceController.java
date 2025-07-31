package com.ksyun.registry.controller;

import com.ksyun.registry.common.convention.result.Result;
import com.ksyun.registry.common.convention.result.Results;
import com.ksyun.registry.dto.req.HeartBeatServiceReqDTO;
import com.ksyun.registry.dto.req.RegisterServiceReqDTO;
import com.ksyun.registry.dto.req.UnregisterServiceReqDTO;
import com.ksyun.registry.dto.resp.DiscoverServiceRespDTO;
import com.ksyun.registry.service.RegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 服务注册控制层
 */

@RestController
@RequiredArgsConstructor
public class RegistryServiceController {

    private final RegistryService registryService;

    @PostMapping("/api/register")
    public Result<Void> register(@RequestBody RegisterServiceReqDTO requestParam) {
        registryService.registerService(requestParam);
        return Results.success();
    }

    @PostMapping("/api/unregister")
    public Result<Void> unregister(@RequestBody UnregisterServiceReqDTO requestParam) {
        registryService.unregisterService(requestParam);
        return Results.success();
    }

    @PostMapping("/api/heartbeat")
    public Result<Void> sendHeartbeat(@RequestBody HeartBeatServiceReqDTO requestParam) {
        registryService.sendHearBeatService(requestParam);
        return Results.success();
    }

    @GetMapping("/api/discovery")
    public Result<List<DiscoverServiceRespDTO>> discovery(@RequestParam(value = "name", required = false) String serviceName) {
        List<DiscoverServiceRespDTO> respDTOList;
        if (serviceName != null && !serviceName.isEmpty()) {
            respDTOList = registryService.discoveryService(serviceName); // 调用服务 A
        } else {
            respDTOList = registryService.discoveryServiceList(); // 调用服务 B
        }
        return Results.success(respDTOList);
    }
}
