package com.ksyun.registry.service.Impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ksyun.registry.dao.entity.RegistryDO;
import com.ksyun.registry.dao.mapper.RegistryMapper;
import com.ksyun.registry.dto.req.HeartBeatServiceReqDTO;
import com.ksyun.registry.dto.req.RegisterServiceReqDTO;
import com.ksyun.registry.dto.req.UnregisterServiceReqDTO;
import com.ksyun.registry.dto.resp.DiscoverServiceRespDTO;
import com.ksyun.registry.service.RegistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.ksyun.registry.common.enums.HeartBeatEnum.HEARTBEAT;

/**
 * 服务注册信息接口实现层
 */

@Service
@RequiredArgsConstructor
public class RegistryServiceImpl extends ServiceImpl<RegistryMapper, RegistryDO> implements RegistryService {

    private final RegistryMapper registryMapper;

    private final AtomicInteger currentIndex = new AtomicInteger(0);

    @Override
    public void registerService(RegisterServiceReqDTO requestParam) {
        String serviceId = requestParam.getServiceId();
        if(StrUtil.isBlank(serviceId)) {
            serviceId = "";  // 将 serviceId 置为 null 以便统一处理
        }

        LambdaQueryWrapper<RegistryDO> queryWrapper = Wrappers.lambdaQuery(RegistryDO.class)
                //.eq(!serviceId.isEmpty(), RegistryDO::getServiceId, serviceId)
                //.eq(RegistryDO::getServiceName, requestParam.getServiceName())
                .eq(RegistryDO::getIpAddress, requestParam.getIpAddress())
                .eq(RegistryDO::getPort, requestParam.getPort())
                .eq(RegistryDO::getDelFlag, 0)
                .eq(RegistryDO::getDelTime, 0L);
        RegistryDO registryDO = registryMapper.selectOne(queryWrapper);

        if (registryDO == null) {
            RegistryDO newRegistry = RegistryDO.builder()
                    .ipAddress(requestParam.getIpAddress())
                    .port(requestParam.getPort())
                    .serviceName(requestParam.getServiceName())
                    .delFlag(0)
                    .delTime(0L)
                    .serviceId(serviceId)
                    .build();

            registryMapper.insert(newRegistry);
        }
    }

    @Override
    public void unregisterService(UnregisterServiceReqDTO requestParam) {
        String serviceId = requestParam.getServiceId();
        if(StrUtil.isBlank(serviceId)) {
            serviceId = "";  // 将 serviceId 置为 null 以便统一处理
        }

        LambdaQueryWrapper<RegistryDO> queryWrapper = Wrappers.lambdaQuery(RegistryDO.class)
                .eq(!serviceId.isEmpty(), RegistryDO::getServiceId, serviceId)
                .eq(RegistryDO::getServiceName, requestParam.getServiceName())
                .eq(RegistryDO::getIpAddress, requestParam.getIpAddress())
                .eq(RegistryDO::getPort, requestParam.getPort())
                .eq(RegistryDO::getDelFlag, 0)
                .eq(RegistryDO::getDelTime, 0L);
        RegistryDO registryDO = registryMapper.selectOne(queryWrapper);

        if (registryDO != null) {
            LambdaUpdateWrapper<RegistryDO> updateWrapper = Wrappers.lambdaUpdate(RegistryDO.class)
                    .eq(!serviceId.isEmpty(), RegistryDO::getServiceId, serviceId)
                    .eq(RegistryDO::getServiceName, requestParam.getServiceName())
                    .eq(RegistryDO::getIpAddress, requestParam.getIpAddress())
                    .eq(RegistryDO::getPort, requestParam.getPort())
                    .eq(RegistryDO::getDelFlag, 0)
                    .eq(RegistryDO::getDelTime, 0L);

            RegistryDO newRegistry = RegistryDO.builder()
                    .serviceId(registryDO.getServiceId())
                    .serviceName(registryDO.getServiceName())
                    .ipAddress(registryDO.getIpAddress())
                    .port(registryDO.getPort())
                    .delFlag(1)
                    .delTime(System.currentTimeMillis())
                    .build();

            registryMapper.update(newRegistry, updateWrapper);
        }
    }

    @Override
    public void sendHearBeatService(HeartBeatServiceReqDTO requestParam) {
        String serviceId = requestParam.getServiceId();
        if(StrUtil.isBlank(serviceId)) {
            serviceId = "";  // 将 serviceId 置为 null 以便统一处理
        }

        LambdaQueryWrapper<RegistryDO> queryWrapper = Wrappers.lambdaQuery(RegistryDO.class)
                .eq(!serviceId.isEmpty(), RegistryDO::getServiceId, serviceId)
                .eq(RegistryDO::getIpAddress, requestParam.getIpAddress())
                .eq(RegistryDO::getPort, requestParam.getPort())
                .eq(RegistryDO::getDelFlag, 0)
                .eq(RegistryDO::getDelTime, 0L);
        RegistryDO registryDO = registryMapper.selectOne(queryWrapper);

        if (registryDO != null) {
            if(checkHeartBeat(registryDO)) return;
            LambdaUpdateWrapper<RegistryDO> updateWrapper = Wrappers.lambdaUpdate(RegistryDO.class)
                    .eq(!serviceId.isEmpty(), RegistryDO::getServiceId, serviceId)
                    .eq(RegistryDO::getIpAddress, requestParam.getIpAddress())
                    .eq(RegistryDO::getPort, requestParam.getPort())
                    .eq(RegistryDO::getDelFlag, 0)
                    .eq(RegistryDO::getDelTime, 0L);

            RegistryDO newRegistry = RegistryDO.builder()
                    .serviceId(registryDO.getServiceId())
                    .serviceName(registryDO.getServiceName())
                    .ipAddress(registryDO.getIpAddress())
                    .port(registryDO.getPort())
                    .delFlag(registryDO.getDelFlag())
                    .delTime(registryDO.getDelTime())
                    .build();

            registryMapper.update(newRegistry, updateWrapper);
        }
    }

    public boolean checkHeartBeat(RegistryDO registryDO) {
        // 惰性删除策略，若当前注册信息存在，查看上次更新时间与当前时间的秒差
        Date currentDate = new Date();
        long secondsDiff = DateUtil.between(registryDO.getUpdateTime(), currentDate, DateUnit.SECOND);
        if(secondsDiff > HEARTBEAT.getDiff()) {
            // 执行注销操作
            UnregisterServiceReqDTO unregisterServiceReqDTO = UnregisterServiceReqDTO.builder()
                    .ipAddress(registryDO.getIpAddress())
                    .port(registryDO.getPort())
                    .serviceName(registryDO.getServiceName())
                    .serviceId(registryDO.getServiceId())
                    .build();
            unregisterService(unregisterServiceReqDTO);
            return true;
        }
        return false;
    }

    @Override
    public List<DiscoverServiceRespDTO> discoveryService(String serviceName) {
        LambdaQueryWrapper<RegistryDO> queryWrapper = Wrappers.lambdaQuery(RegistryDO.class)
                .eq(RegistryDO::getServiceName, serviceName)
                .eq(RegistryDO::getDelFlag, 0)
                .eq(RegistryDO::getDelTime, 0L);
        List<RegistryDO> serviceList =  registryMapper.selectList(queryWrapper);
        System.out.println(serviceName + "==" +serviceList.size());
        List<RegistryDO> aliveServiceList = new ArrayList<>();

        for(RegistryDO registryDO : serviceList) {
            if(!checkHeartBeat(registryDO)) aliveServiceList.add(registryDO);
        }

        if (aliveServiceList.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取当前索引，并递增
        int index = currentIndex.updateAndGet(i -> (i + 1) % aliveServiceList.size());
        RegistryDO registryDO = aliveServiceList.get(index);
        DiscoverServiceRespDTO discoverServiceRespDTO = DiscoverServiceRespDTO
                .builder()
                .ipAddress(registryDO.getIpAddress())
                .port(registryDO.getPort())
                .serviceName(registryDO.getServiceName())
                .serviceId(registryDO.getServiceId())
                .build();

        // 返回列表中的服务
        return CollectionUtil.toList(discoverServiceRespDTO);
    }

    @Override
    public List<DiscoverServiceRespDTO> discoveryServiceList() {
        LambdaQueryWrapper<RegistryDO> queryWrapper = Wrappers.lambdaQuery(RegistryDO.class)
                .eq(RegistryDO::getDelFlag, 0)
                .eq(RegistryDO::getDelTime, 0L);
        List<RegistryDO> serviceList =  registryMapper.selectList(queryWrapper);
        List<RegistryDO> aliveServiceList = new ArrayList<>();

        for(RegistryDO registryDO : serviceList) {
            if(!checkHeartBeat(registryDO)) aliveServiceList.add(registryDO);
        }

        if (aliveServiceList.isEmpty()) {
            return new ArrayList<>();
        }

        List<DiscoverServiceRespDTO> discoverServiceRespDTOS = aliveServiceList.stream()
                .map(registryDO -> DiscoverServiceRespDTO
                        .builder()
                        .ipAddress(registryDO.getIpAddress())
                        .port(registryDO.getPort())
                        .serviceName(registryDO.getServiceName())
                        .serviceId(registryDO.getServiceId())
                        .build()).collect(Collectors.toList());
        return discoverServiceRespDTOS;
    }
}
