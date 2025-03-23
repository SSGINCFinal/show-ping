package com.ssginc.showping.entity;

import lombok.Getter;

@Getter
public enum ReportType {

    CHAT("채팅"),
    REVIEW("리뷰");

    private final String reportType;

    ReportType(String reportType) {
        this.reportType = reportType;
    }

}
