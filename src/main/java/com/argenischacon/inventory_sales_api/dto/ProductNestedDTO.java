package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Nested data transfer object for Product")
public class ProductNestedDTO {

    @Schema(description = "Unique identifier for the product", example = "1")
    private Long id;

    @Schema(description = "Name of the product", example = "Smart TV")
    private String name;

    @Schema(description = "Unit price of the product", example = "999.99")
    private BigDecimal unitPrice;
}
