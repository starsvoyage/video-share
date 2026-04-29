package edu.arizona.videoshare.dto.report;

import java.time.LocalDateTime;

import edu.arizona.videoshare.model.entity.Report;
import edu.arizona.videoshare.model.enums.ReportStatus;

public class ReportResponse {

    public Long id;
    public String contentType;
    public Long contentId;
    public String reason;
    public Long reportedByUserId;
    public ReportStatus status;
    public LocalDateTime createdAt;

    public static ReportResponse of(Report report) {
        ReportResponse r = new ReportResponse();
        r.id = report.getId();
        r.contentType = report.getContentType();
        r.contentId = report.getContentId();
        r.reason = report.getReason();
        r.reportedByUserId = report.getReportedBy().getId();
        r.status = report.getStatus();
        r.createdAt = report.getCreatedAt();
        return r;
    }
}