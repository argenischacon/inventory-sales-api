package com.argenischacon.inventory_sales_api.dto;

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
public class ProductRequestDTO {
    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @PositiveOrZero(message = "Unit price must not be negative")
    private BigDecimal unitPrice;

    @PositiveOrZero (message = "Stock must be a positive number or zero")
    private Integer stock;

    @NotNull(message = "Category ID is required")
    private Long categoryId;
}
