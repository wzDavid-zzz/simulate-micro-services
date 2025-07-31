package com.ksyun.registry.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 心跳间隔枚举类
 */

@Getter
@RequiredArgsConstructor
public enum HeartBeatEnum {

    HEARTBEAT(60);

    private final int diff;
}
