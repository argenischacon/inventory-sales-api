package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data transfer object for authentication response")
public record AuthResponseDTO(
        @Schema(description = "JWT token for authenticating subsequent requests",
                example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGVzIjoiUk9MRV9BRE1JTiIsImlhdCI6MTcyMjUyMjAwMCwiZXhwIjoxNzIyNTI1NjAwfQ.abcdef123456")
        String token
) {
}
