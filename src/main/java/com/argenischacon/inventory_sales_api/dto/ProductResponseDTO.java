package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for Product responses")
public class ProductResponseDTO {

    @Schema(description = "Unique identifier for the product", example = "1")
    private Long id;

    @Schema(description = "Name of the product", example = "Smart TV")
    private String name;

    @Schema(description = "Detailed description of the product", example = "Smart TV 4K")
    private String description;

    @Schema(description = "Unit price of the product", example = "999.99")
    private BigDecimal unitPrice;

    @Schema(description = "Stock quantity of the product", example = "10")
    private Integer stock;

    @Schema(description = "Timestamp when the product was created", example = "2024-08-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the product was last updated", example = "2024-08-01T11:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Nested data for the category associated with the product")
    private CategoryNestedDTO category;
}
