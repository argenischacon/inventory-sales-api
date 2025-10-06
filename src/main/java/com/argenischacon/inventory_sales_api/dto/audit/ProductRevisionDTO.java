package com.argenischacon.inventory_sales_api.dto.audit;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.Instant;

@Schema(description = "Data transfer object representing a single revision of a Product entity")
public record ProductRevisionDTO(
        @Schema(description = "The revision number.", example = "1")
        Long revisionId,

        @Schema(description = "The username of the user who made the change", example = "admin")
        String username,

        @Schema(description = "The timestamp when the revision was created", example = "2024-08-01T11:00:00.123456Z")
        Instant revisionDate,

        @Schema(description = "The type of change (e.g., INSERT, UPDATE, DELETE)", example = "UPDATE")
        String revisionType,

        @Schema(description = "The ID of the product entity at this revision", example = "1")
        Long id,

        @Schema(description = "The name of the product at this revision", example = "Smart TV 4K")
        String name,

        @Schema(description = "The description of the product at this revision", example = "A 55-inch 4K Smart TV.")
        String description,

        @Schema(description = "The unit price of the product at this revision", example = "999.99")
        BigDecimal unitPrice,

        @Schema(description = "The stock quantity of the product at this revision", example = "100")
        Integer stock,

        @Schema(description = "The ID of the category associated with the product at this revision", example = "1")
        Long categoryId
) { }
