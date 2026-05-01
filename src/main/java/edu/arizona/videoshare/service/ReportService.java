package edu.arizona.videoshare.service;

import edu.arizona.videoshare.dto.report.CreateReportRequest;
import edu.arizona.videoshare.exception.NotFoundException;
import edu.arizona.videoshare.model.entity.Report;
import edu.arizona.videoshare.model.entity.User;
import edu.arizona.videoshare.model.entity.Video;
import edu.arizona.videoshare.model.enums.ReportStatus;
import edu.arizona.videoshare.repository.ReportRepository;
import edu.arizona.videoshare.repository.UserRepository;
import edu.arizona.videoshare.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    @Transactional
    public Report createReport(Long reporterUserId, CreateReportRequest req) {
        User reporter = userRepository.findById(reporterUserId)
                .orElseThrow(() -> new NotFoundException("User not found: " + reporterUserId));

        if (!"VIDEO".equalsIgnoreCase(req.contentType)) {
            throw new IllegalArgumentException("Only VIDEO reports are supported right now.");
        }

        Video video = videoRepository.findById(req.contentId)
                .orElseThrow(() -> new NotFoundException("Video not found: " + req.contentId));

        Report report = new Report();
        report.setContentType("VIDEO");
        report.setContentId(video.getId());
        report.setReason(req.reason.trim());
        report.setReportedBy(reporter);
        report.setStatus(ReportStatus.OPEN);

        return reportRepository.save(report);
    }

    @Transactional(readOnly = true)
    public List<Report> getAllReports() {
        return reportRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Report> getOpenReports() {
        return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.OPEN);
    }

    @Transactional
    public Report resolveReport(Long reportId) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new NotFoundException("Report not found: " + reportId));

        report.setStatus(ReportStatus.RESOLVED);
        return reportRepository.save(report);
    }
}