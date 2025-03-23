package com.ssginc.showping.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report")
public class Report {

    @Id
    @Column(name = "report_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportNo;

    // 회원
    // 신고 : 회원은 N : 1의 관계를 가진다.
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", referencedColumnName = "member_no")
    private Member member;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "report_status")
    private ReportStatus reportStatus;

    @NotNull
    @Column(name = "report_reason", length = 500)
    private String reportReason;

    @NotNull
    @Column(name = "report_content", length = 500)
    private String reportContent;

    @NotNull
    @Column(name = "report_created_at")
    private LocalDateTime reportCreatedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "report_type")
    private ReportType reportType;

}
