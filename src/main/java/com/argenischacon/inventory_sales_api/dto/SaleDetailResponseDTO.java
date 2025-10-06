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
@Schema(description = "Data transfer object for SaleDetail responses")
public class SaleDetailResponseDTO {

    @Schema(description = "Unique identifier for the sale detail", example = "1")
    private Long id;

    @Schema(description = "Quantity of the product sold", example = "2")
    private Integer quantity;

    @Schema(description = "Unit price of the product at the time of the sale", example = "999.99")
    private BigDecimal unitPrice;

    @Schema(description = "Timestamp when the sale detail was created", example = "2024-08-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the sale detail was last updated", example = "2024-08-01T11:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Nested data for the product sold")
    private ProductNestedDTO product;

    @Schema(description = "Sub-total of the sale detail", example = "1999.98")
    private BigDecimal subTotal;
}
