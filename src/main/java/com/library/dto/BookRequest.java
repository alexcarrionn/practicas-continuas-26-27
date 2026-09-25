package com.library.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BookRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 255, message = "El título no puede superar los 255 caracteres")
        String title,

        @NotBlank(message = "El autor es obligatorio")
        @Size(max = 255, message = "El autor no puede superar los 255 caracteres")
        String author,

        @NotBlank(message = "El género es obligatorio")
        @Size(max = 100, message = "El género no puede superar los 100 caracteres")
        String genre,

        @Pattern(regexp = "^(?:\\d{10}|\\d{13})$", message = "El ISBN debe tener 10 o 13 dígitos")
        @Size(max = 20, message = "El ISBN no puede superar los 20 caracteres")
        String isbn,

        @Min(value = 1450, message = "El año de publicación debe ser 1450 o posterior")
        @Max(value = 2100, message = "El año de publicación no puede superar 2100")
        Integer publishedYear,

        @Min(value = 1, message = "El número de páginas debe ser mayor que 0")
        @Max(value = 10000, message = "El número de páginas no puede superar 10000")
        Integer pages
) {}
