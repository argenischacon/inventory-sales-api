package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for Category responses")
public class CategoryResponseDTO {

    @Schema(description = "Unique identifier for the category", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;

    @Schema(description = "Detailed description of the category", example = "Devices and gadgets")
    private String description;

    @Schema(description = "Timestamp when the category was created", example = "2024-08-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the category was last updated", example = "2024-08-01T11:00:00")
    private LocalDateTime updatedAt;
}
