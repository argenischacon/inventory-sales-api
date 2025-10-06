package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for Sale responses")
public class SaleResponseDTO {

    @Schema(description = "Unique identifier for the sale", example = "1")
    private Long id;

    @Schema(description = "Date of the sale", example = "2024-08-01")
    private LocalDate date;

    @Schema(description = "Timestamp when the sale detail was created", example = "2024-08-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the sale detail was last updated", example = "2024-08-01T11:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Nested data for the customer associated with the sale")
    private CustomerNestedDTO customer;

    @Schema(description = "List of sale details associated with the sale")
    private List<SaleDetailResponseDTO> saleDetails;

    @Schema(description = "Total amount of the sale", example = "1999.98")
    private BigDecimal total;
}
