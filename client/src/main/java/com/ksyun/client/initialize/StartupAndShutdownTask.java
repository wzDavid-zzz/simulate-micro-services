package com.ksyun.client.initialize;

import com.ksyun.client.service.Impl.ClientServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupAndShutdownTask implements CommandLineRunner, DisposableBean {

    private final ClientServiceImpl clientService;

    @Override
    public void run(String... args) throws Exception {
        // 在应用启动时调用的方法
        System.out.println("客户服务启动，执行注册.");
        clientService.remoteRegister();
    }

    @Override
    public void destroy() throws Exception {
        // 在应用退出时调用的方法
        System.out.println("客户服务退出，执行注销.");
        clientService.remoteUnRegister();
    }
}
