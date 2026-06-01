package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Request body for {@code POST /api/misiones/{id}/operativos}. */
public record AssignOperativeRequest(

        @NotBlank(message = "operativeCodeName must not be blank")
        String operativeCodeName) {
}
