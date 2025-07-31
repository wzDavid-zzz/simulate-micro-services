package com.ksyun.time.initialize;

import com.ksyun.time.service.Impl.TimeServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupAndShutdownTask implements CommandLineRunner, DisposableBean {

    private final TimeServiceImpl timeServiceImpl;

    @Override
    public void run(String... args) throws Exception {
        // 在应用启动时调用的方法
        System.out.println("时间服务启动，执行注册.");
        timeServiceImpl.remoteRegister();
    }

    @Override
    public void destroy() throws Exception {
        // 在应用退出时调用的方法
        System.out.println("时间服务退出，执行注销.");
        timeServiceImpl.remoteUnRegister();
    }
}
