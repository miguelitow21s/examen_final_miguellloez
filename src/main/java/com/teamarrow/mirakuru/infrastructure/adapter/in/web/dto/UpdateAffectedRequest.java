package com.teamarrow.mirakuru.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for {@code PUT /api/afectados/{id}}.
 *
 * <p>Mirrors {@link RegisterAffectedRequest} but additionally requires the
 * lifecycle {@code status}, since an update is the moment the team records that
 * a subject has been monitored, neutralised or cured.</p>
 */
public record UpdateAffectedRequest(

        @NotBlank(message = "codeName must not be blank")
        String codeName,

        @NotNull(message = "mirakuruSaturation is required")
        @Min(value = 0, message = "mirakuruSaturation must be at least 0")
        @Max(value = 100, message = "mirakuruSaturation must be at most 100")
        Integer mirakuruSaturation,

        @NotNull(message = "aggressionIndex is required")
        @Min(value = 0, message = "aggressionIndex must be at least 0")
        @Max(value = 10, message = "aggressionIndex must be at most 10")
        Integer aggressionIndex,

        @NotBlank(message = "locationSector must not be blank")
        String locationSector,

        @DecimalMin(value = "-90.0", message = "latitude must be between -90 and 90")
        @DecimalMax(value = "90.0", message = "latitude must be between -90 and 90")
        double latitude,

        @DecimalMin(value = "-180.0", message = "longitude must be between -180 and 180")
        @DecimalMax(value = "180.0", message = "longitude must be between -180 and 180")
        double longitude,

        @NotBlank(message = "status must not be blank")
        String status) {
}
