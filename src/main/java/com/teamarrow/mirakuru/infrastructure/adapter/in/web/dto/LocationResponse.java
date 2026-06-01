package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

/**
 * Nested representation of an individual's last known location in API responses.
 */
public record LocationResponse(String sector, double latitude, double longitude) {
}
