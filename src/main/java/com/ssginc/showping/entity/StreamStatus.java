package com.ssginc.showping.entity;

import lombok.Getter;

@Getter
public enum StreamStatus {
    STANDBY("송출 대기"),
    ONAIR("송출 중"),
    ENDED("송출 완료");

    private final String streamStatus;

    StreamStatus(String streamStatus) {
        this.streamStatus = streamStatus;
    }
}
