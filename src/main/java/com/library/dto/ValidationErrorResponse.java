package com.library.dto;

import java.time.LocalDateTime;
import java.util.Map;

public record ValidationErrorResponse(
    String error, String message, Map<String, String> fieldErrors, LocalDateTime timestamp) {}
