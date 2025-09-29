package com.argenischacon.inventory_sales_api.dto;

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
public class SaleRequestDTO {
    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Must have at least one sale detail")
    @Valid
    private List<SaleDetailRequestDTO> saleDetails;
}
