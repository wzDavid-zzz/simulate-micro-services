package com.ksyun.time.service.Impl;

import com.ksyun.time.config.TimeServiceInstanceConfiguration;
import com.ksyun.time.dto.TimeRespDTO;
import com.ksyun.time.remote.RegistryRemoteService;
import com.ksyun.time.remote.dto.req.HeartBeatServiceReqDTO;
import com.ksyun.time.remote.dto.req.RegisterServiceReqDTO;
import com.ksyun.time.remote.dto.req.UnregisterServiceReqDTO;
import com.ksyun.time.remote.dto.resp.DiscoverServiceRespDTO;
import com.ksyun.time.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 时间服务接口实现层
 */

@Service
@RequiredArgsConstructor
public class TimeServiceImpl implements TimeService {

    private final RegistryRemoteService registryRemoteService;
    private final TimeServiceInstanceConfiguration timeServiceInstanceConfiguration;

    private static String serviceId = "";

    @Override
    public TimeRespDTO getDateTime(String style) {
        String formattedDate;
        Date now = new Date();

        switch (style.toLowerCase()) {
            case "date":
                formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(now);
                break;
            case "time":
                formattedDate = new SimpleDateFormat("HH:mm:ss").format(now);
                break;
            case "unix":
                formattedDate = String.valueOf(now.getTime()); // Unix 时间戳（秒）
                break;
            case "full":
            default:
                formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now);
                break;
        }

        return TimeRespDTO.builder()
                .result(formattedDate)
                .serviceId(serviceId)
                .build();
    }

    public void remoteRegister() {
        serviceId = UUID.randomUUID().toString();
        RegisterServiceReqDTO reqDTO = RegisterServiceReqDTO.builder()
                .serviceId(serviceId)
                .ipAddress(timeServiceInstanceConfiguration.getIpAddress())
                .port(timeServiceInstanceConfiguration.getPort())
                .serviceName(timeServiceInstanceConfiguration.getServiceName())
                .build();
        registryRemoteService.register(reqDTO);
    }

    @Scheduled(fixedDelay = 20 * 1000)
    public void sendHeartBeat() {
        HeartBeatServiceReqDTO heartBeatServiceReqDTO = HeartBeatServiceReqDTO.builder()
                .serviceId("")
                .ipAddress(timeServiceInstanceConfiguration.getIpAddress())
                .port(timeServiceInstanceConfiguration.getPort())
                .build();
        registryRemoteService.sendHeartbeat(heartBeatServiceReqDTO);
    }

    public void remoteUnRegister() {
        UnregisterServiceReqDTO reqDTO = UnregisterServiceReqDTO.builder()
                .serviceId("")
                .ipAddress(timeServiceInstanceConfiguration.getIpAddress())
                .port(timeServiceInstanceConfiguration.getPort())
                .serviceName(timeServiceInstanceConfiguration.getServiceName())
                .build();
        registryRemoteService.unregister(reqDTO);
    }
}
