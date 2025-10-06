package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Data transfer object for creating or updating a SaleDetail")
public class SaleDetailRequestDTO {

    @Schema(description = "Unique identifier for the sale detail", example = "1", nullable = true)
    private Long id; //(create -> null | update -> present)

    @Schema(description = "Quantity of the product being sold", example = "2")
    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive number")
    private Integer quantity;

    @Schema(description = "Unit price of the product at the time of the sale", example = "999.99")
    @NotNull(message = "Unit price is required")
    @PositiveOrZero(message = "Unit price must not be negative")
    private BigDecimal unitPrice;

    @Schema(description = "ID of the product being sold", example = "1")
    @NotNull(message = "Product ID is required")
    private Long productId;
}
