package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Nested data transfer object for Customer")
public class CustomerNestedDTO {

    @Schema(description = "Unique identifier for the customer", example = "1")
    private Long id;

    @Schema(description = "Name of the customer", example = "John")
    private String name;

    @Schema(description = "Last name of the customer", example = "Doe")
    private String lastName;
}
