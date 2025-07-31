package com.ksyun.client.service.Impl;

import cn.hutool.core.util.StrUtil;
import com.ksyun.client.config.ClientServiceInstanceConfiguration;
import com.ksyun.client.dto.ClientInfoRespDTO;
import com.ksyun.client.remote.RegistryRemoteService;
import com.ksyun.client.remote.TimeRemoteService;
import com.ksyun.client.remote.dto.req.HeartBeatServiceReqDTO;
import com.ksyun.client.remote.dto.req.RegisterServiceReqDTO;
import com.ksyun.client.remote.dto.req.UnregisterServiceReqDTO;
import com.ksyun.client.remote.dto.resp.DiscoverServiceRespDTO;
import com.ksyun.client.remote.dto.resp.TimeRespDTO;
import com.ksyun.client.service.ClientService;
import feign.Feign;
import feign.form.spring.SpringFormEncoder;
import feign.optionals.OptionalDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final RegistryRemoteService registryRemoteService;
    private final ClientServiceInstanceConfiguration clientServiceInstanceConfiguration;

    private static String serviceId = "";

    private final ObjectFactory<HttpMessageConverters> messageConverters;

    private TimeRemoteService createFeignClient(String url) {
        /*1、在创建Feign客户端的时候最核心的对象是decoder、encoder、contract
        通过跟踪源码与SpringBoot自动创建的Feign对象比较，设置decoder、encoder、
        contract为SpringBoot中自动创建对象相同，然后定义Feign接口的时候，
        各种参数的注解和方法的注解就可以和不动态修改url的相同了
        decoder解码器，对返回的结果进行解码*/
        OptionalDecoder decoder = new OptionalDecoder(new ResponseEntityDecoder(new SpringDecoder(messageConverters)));
        //encoder编码器，对输入的数据进行编码
        SpringEncoder springEncoder = new SpringEncoder(messageConverters);
        SpringFormEncoder encoder = new SpringFormEncoder(springEncoder);
        //该对象是将接口进行解析，方便生成最后调用的网络对象HttpurlConnection
        SpringMvcContract contract = new SpringMvcContract();
        return Feign.builder()
                .decoder(decoder)
                .encoder(encoder)
                .contract(contract)
                //这个地方的Url可以根据每次调用的时候进行改变
                .target(TimeRemoteService.class, url);
    }

    @Override
    public ClientInfoRespDTO getInfo() {
        List<DiscoverServiceRespDTO> respDTOList = remoteDiscover();
        ClientInfoRespDTO clientInfoRespDTO;
        if(respDTOList.isEmpty()){
            clientInfoRespDTO = ClientInfoRespDTO.builder()
                    .error("时间服务不可用")
                    .result(null)
                    .build();
        } else {
            DiscoverServiceRespDTO discoverServiceRespDTO = respDTOList.get(0);
            String ipAddress = discoverServiceRespDTO.getIpAddress();
            Integer port = discoverServiceRespDTO.getPort();
            String dynamicUrl = StrUtil.format("http://{}:{}", ipAddress, port);

            System.out.println("本次调用的IP是：" + dynamicUrl);

            // 创建动态 URL 的 Feign 客户端
            TimeRemoteService timeRemoteService = createFeignClient(dynamicUrl);

            // 调用远程服务
            String style = "full";
            TimeRespDTO timeRespDTO = timeRemoteService.getCurrentDateTime(style).getData();
            clientInfoRespDTO = ClientInfoRespDTO.builder()
                    .error(null)
                    .result(StrUtil.format("Hello Kingsoft Cloud Star Camp - {} - {}", serviceId, timeRespDTO.getResult()))
                    .build();
        }
        return clientInfoRespDTO;
    }

    public void remoteRegister() {
        serviceId = UUID.randomUUID().toString();
        RegisterServiceReqDTO reqDTO = RegisterServiceReqDTO.builder()
                .serviceId(serviceId)
                .ipAddress(clientServiceInstanceConfiguration.getIpAddress())
                .port(clientServiceInstanceConfiguration.getPort())
                .serviceName(clientServiceInstanceConfiguration.getServiceName())
                .build();
        registryRemoteService.register(reqDTO);
    }

    @Scheduled(fixedDelay = 20 * 1000)
    public void sendHeartBeat() {
        HeartBeatServiceReqDTO heartBeatServiceReqDTO = HeartBeatServiceReqDTO.builder()
                .serviceId("")
                .ipAddress(clientServiceInstanceConfiguration.getIpAddress())
                .port(clientServiceInstanceConfiguration.getPort())
                .build();
        registryRemoteService.sendHeartbeat(heartBeatServiceReqDTO);
    }

    public void remoteUnRegister() {
        UnregisterServiceReqDTO reqDTO = UnregisterServiceReqDTO.builder()
                .serviceId("")
                .ipAddress(clientServiceInstanceConfiguration.getIpAddress())
                .port(clientServiceInstanceConfiguration.getPort())
                .serviceName(clientServiceInstanceConfiguration.getServiceName())
                .build();
        registryRemoteService.unregister(reqDTO);
    }

    public List<DiscoverServiceRespDTO> remoteDiscover() {
        String name = "time-service";
        return registryRemoteService.discovery(name).getData();
    }
}
