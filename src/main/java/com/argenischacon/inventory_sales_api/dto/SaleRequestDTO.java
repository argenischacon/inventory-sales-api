package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating or updating a Sale")
public class SaleRequestDTO {

    @Schema(description = "ID of the customer associated with the sale", example = "1")
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @Schema(description = "List of sale details associated with the sale")
    @NotEmpty(message = "Must have at least one sale detail")
    @Valid
    private List<SaleDetailRequestDTO> saleDetails;
}
