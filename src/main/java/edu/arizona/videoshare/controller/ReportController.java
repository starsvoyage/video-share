package edu.arizona.videoshare.controller;

import edu.arizona.videoshare.dto.report.CreateReportRequest;
import edu.arizona.videoshare.dto.report.ReportResponse;
import edu.arizona.videoshare.exception.ForbiddenException;
import edu.arizona.videoshare.service.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    private void requireLogin(HttpServletRequest request) {
        Object userId = request.getSession().getAttribute("loggedInUserId");

        if (userId == null) {
            throw new ForbiddenException("Authentication required");
        }
    }

    private void requireAdmin(HttpServletRequest request) {
        Object roleObj = request.getSession().getAttribute("loggedInRole");

        if (roleObj == null) {
            throw new ForbiddenException("Authentication required");
        }

        if (!roleObj.toString().equals("ADMIN")) {
            throw new ForbiddenException("Admin access required");
        }
    }

    @PostMapping
    public ReportResponse createReport(
            @Valid @RequestBody CreateReportRequest req,
            HttpServletRequest request
    ) {
        requireLogin(request);

        Long reporterUserId = (Long) request.getSession().getAttribute("loggedInUserId");

        return ReportResponse.of(reportService.createReport(reporterUserId, req));
    }

    @GetMapping
    public List<ReportResponse> getAllReports(HttpServletRequest request) {
        requireAdmin(request);

        return reportService.getAllReports()
                .stream()
                .map(ReportResponse::of)
                .toList();
    }

    @GetMapping("/open")
    public List<ReportResponse> getOpenReports(HttpServletRequest request) {
        requireAdmin(request);

        return reportService.getOpenReports()
                .stream()
                .map(ReportResponse::of)
                .toList();
    }

    @PutMapping("/{id}/resolve")
    public ReportResponse resolveReport(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        requireAdmin(request);

        return ReportResponse.of(reportService.resolveReport(id));
    }
}