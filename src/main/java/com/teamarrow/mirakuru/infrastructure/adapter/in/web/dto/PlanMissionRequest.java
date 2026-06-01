package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for {@code POST /api/misiones}. */
public record PlanMissionRequest(

        @NotBlank(message = "name must not be blank")
        String name,

        @NotBlank(message = "targetAffectedId must not be blank")
        String targetAffectedId) {
}
