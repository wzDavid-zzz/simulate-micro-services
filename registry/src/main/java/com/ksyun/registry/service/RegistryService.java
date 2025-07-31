package com.ksyun.registry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ksyun.registry.dao.entity.RegistryDO;
import com.ksyun.registry.dto.req.HeartBeatServiceReqDTO;
import com.ksyun.registry.dto.req.RegisterServiceReqDTO;
import com.ksyun.registry.dto.req.UnregisterServiceReqDTO;
import com.ksyun.registry.dto.resp.DiscoverServiceRespDTO;

import java.util.List;

/**
 * 服务注册信息接口层
 */

public interface RegistryService extends IService<RegistryDO> {

    /**
     * 服务注册
     * @param requestParam 请求参数
     */
    void registerService(RegisterServiceReqDTO requestParam);

    /**
     * 服务注销
     * @param requestParam 请求参数
     */
    void unregisterService(UnregisterServiceReqDTO requestParam);

    /**
     * 服务心跳监测
     * @param requestParam 请求参数
     */
    void sendHearBeatService(HeartBeatServiceReqDTO requestParam);

    /**
     * 服务发现，带负载均衡
     * @param serviceName 服务名
     */
    List<DiscoverServiceRespDTO> discoveryService(String serviceName);

    /**
     * 服务发现，返回所有可用服务
     */
    List<DiscoverServiceRespDTO> discoveryServiceList();
}
