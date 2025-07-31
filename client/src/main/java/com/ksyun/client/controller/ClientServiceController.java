package com.ksyun.client.controller;

import com.ksyun.client.common.convention.result.Result;
import com.ksyun.client.common.convention.result.Results;
import com.ksyun.client.dto.ClientInfoRespDTO;
import com.ksyun.client.remote.TimeRemoteService;
import com.ksyun.client.service.ClientService;
import feign.Feign;
import feign.form.spring.SpringFormEncoder;
import feign.optionals.OptionalDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClientServiceController {

    private final ClientService clientService;

    @GetMapping("/api/getInfo")
    public Result<ClientInfoRespDTO> getInfo() {
        ClientInfoRespDTO resp = clientService.getInfo();
        if(resp.getResult() == null) {
            return Results.failure(resp);
        }
        return Results.success(resp);
    }

}
