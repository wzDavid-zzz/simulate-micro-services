package com.ksyun.registry;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 注册服务启动类
 */

@SpringBootApplication
@MapperScan("com.ksyun.registry.dao.mapper")
public class RegistryServiceMain {
    public static void main(String[] args) {
        SpringApplication.run(RegistryServiceMain.class, args);
    }
}