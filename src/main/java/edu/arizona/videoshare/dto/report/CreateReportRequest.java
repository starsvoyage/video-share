package edu.arizona.videoshare.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateReportRequest {

    @NotBlank
    public String contentType;

    @NotNull
    public Long contentId;

    @NotBlank
    public String reason;
}