package com.argenischacon.inventory_sales_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Standard structure for API error responses")
public class ErrorResponse {

    @Schema(description = "Timestamp when the error occurred", example = "2024-08-01T10:30:00.123456")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "404")
    private int status;

    @Schema(description = "HTTP status error reason phrase", example = "Not Found")
    private String error;

    @Schema(description = "A human-readable message providing details about the error", example = "Product not found")
    private String message;

    @Schema(description = "A map containing additional details about the error, such as validation failures",
            example = "{\"fieldName\": \"Error description\"}")
    private Map<String, Object> details;
}
