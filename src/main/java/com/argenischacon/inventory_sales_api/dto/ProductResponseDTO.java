package com.argenischacon.inventory_sales_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal unitPrice;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CategoryNestedDTO category;
}
