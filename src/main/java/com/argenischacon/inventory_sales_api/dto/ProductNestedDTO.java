package com.argenischacon.inventory_sales_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductNestedDTO {
    private Long id;
    private String name;
    private BigDecimal unitPrice;
}
