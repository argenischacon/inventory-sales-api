package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Data transfer object for authentication request")
public record AuthRequestDTO(
        @Schema(description = "User's username", example = "admin")
        @NotBlank(message = "Username is required")
        String username,

        @Schema(description = "User's password", example = "password")
        @NotBlank(message = "Password is required")
        String password
) {
}
