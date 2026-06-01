package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

/** Response body describing a member of Team Arrow. */
public record TeamMemberResponse(String id, String codeName, String role) {
}
