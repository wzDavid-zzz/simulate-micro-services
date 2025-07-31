package com.ksyun.client.controller;

import com.ksyun.client.common.convention.result.Result;
import com.ksyun.client.common.convention.result.Results;
import com.ksyun.client.dto.ClientInfoRespDTO;
import com.ksyun.client.service.ClientService;
import lombok.RequiredArgsConstructor;
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
