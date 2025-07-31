package com.ksyun.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ClientServiceMain2 {
    public static void main(String[] args) {
        SpringApplication.run(ClientServiceMain2.class, args);
    }
}