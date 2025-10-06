package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating or updating a Product")
public class ProductRequestDTO {

    @Schema(description = "Name of the product", example = "Smart TV")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Detailed description of the product", example = "Smart TV 4K")
    private String description;

    @Schema(description = "Unit price of the product", example = "999.99")
    @PositiveOrZero(message = "Unit price must not be negative")
    @NotNull(message = "Unit price is required")
    private BigDecimal unitPrice;

    @Schema(description = "Stock quantity of the product", example = "10")
    @PositiveOrZero (message = "Stock must be a positive number or zero")
    @NotNull(message = "Stock is required")
    private Integer stock;

    @Schema(description = "ID of the category associated with the product", example = "1")
    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
