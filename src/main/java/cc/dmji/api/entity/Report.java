package cc.dmji.api.entity;


import cc.dmji.api.enums.ReportHandleStatus;
import cc.dmji.api.enums.ReportReason;
import cc.dmji.api.enums.ReportTargetType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 举报实体
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "dm_report", schema = "dmji")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "report_reason")
    private Integer reportReason;
    @Column(name = "report_target_type")
    private Integer reportTargetType;
    @Column(name = "report_handle_status")
    private Integer reportHandleStatus;
    @Column(name = "report_target_id")
    private Long reportTargetId;
    @Column(name = "publisher_user_id")
    private Long publisherUserId;
    @Column(name = "target_user_id")
    private Long targetUserId;
    @Column(name = "create_time")
    private Timestamp createTime;
    @Column(name = "handled_time")
    private Timestamp handledTime;
    @Column(name = "report_content", columnDefinition = "text")
    private String reportContent;
    @Column(name = "target_url", columnDefinition = "text")
    private String targetUrl;
    @Column(name = "manager_id")
    private Long managerId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReportReason getReportReason() {
        return ReportReason.byCode(this.reportReason);
    }

    public void setReportReason(Integer reportReason) {
        this.reportReason = reportReason;
    }

    public ReportTargetType getReportTargetType() {
        return ReportTargetType.byCode(this.reportTargetType);
    }

    public void setReportTargetType(Integer reportTargetType) {
        this.reportTargetType = reportTargetType;
    }

    public ReportHandleStatus getReportHandleStatus() {
        return ReportHandleStatus.byCode(this.reportHandleStatus);
    }

    public void setReportHandleStatus(Integer reportHandleStatus) {
        this.reportHandleStatus = reportHandleStatus;
    }

    public Long getReportTargetId() {
        return reportTargetId;
    }

    public void setReportTargetId(Long reportTargetId) {
        this.reportTargetId = reportTargetId;
    }

    public Long getPublisherUserId() {
        return publisherUserId;
    }

    public void setPublisherUserId(Long publisherUserId) {
        this.publisherUserId = publisherUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getHandledTime() {
        return handledTime;
    }

    public void setHandledTime(Timestamp handledTime) {
        this.handledTime = handledTime;
    }

    public String getReportContent() {
        return reportContent;
    }

    public void setReportContent(String reportContent) {
        this.reportContent = reportContent;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public Long getManagerId() {
        return managerId;
    }

    public void setManagerId(Long managerId) {
        this.managerId = managerId;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", reportType=" + reportReason +
                ", reportTargetType=" + reportTargetType +
                ", reportHandleStatus=" + reportHandleStatus +
                ", reportTargetId=" + reportTargetId +
                ", publisherUserId=" + publisherUserId +
                ", targetUserId=" + targetUserId +
                ", createTime=" + createTime +
                ", handledTime=" + handledTime +
                '}';
    }
}
