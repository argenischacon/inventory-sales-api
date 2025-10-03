package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Nested data transfer object for Category")
public class CategoryNestedDTO {

    @Schema(description = "Unique identifier for the category", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Electronics")
    private String name;
}
