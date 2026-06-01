package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

import java.time.Instant;

/** Response body describing one external intelligence report. */
public record IntelReportResponse(String sourceName, String headline, int confidence, Instant reportedAt) {
}
