package com.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record RatingRequest(
    @NotNull(message = "La valoración es obligatoria")
        @Min(value = 1, message = "La valoración debe estar entre 1 y 5")
        @Max(value = 5, message = "La valoración debe estar entre 1 y 5")
        Integer rating) {}
