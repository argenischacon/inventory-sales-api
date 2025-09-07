package com.argenischacon.inventory_sales_api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleDetailRequestDTO {
    private Long id; //(create -> null | update -> present)

    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @PositiveOrZero(message = "Unit price must not be negative")
    private BigDecimal unitPrice;

    @NotNull(message = "Product ID is required")
    private Long productId;
}
