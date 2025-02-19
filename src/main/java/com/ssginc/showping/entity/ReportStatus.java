package com.ssginc.showping.entity;

import lombok.Getter;

@Getter
public enum ReportStatus {

    PROCEEDING("미처리"),
    COMPLETED("처리");

    private final String reportStatus;

    ReportStatus(String reportStatus) {
        this.reportStatus = reportStatus;
    }

}
