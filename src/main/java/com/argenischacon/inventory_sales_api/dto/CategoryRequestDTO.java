package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating or updating a Category")
public class CategoryRequestDTO {

    @Schema(description = "Name of the category", example = "Electronics")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Detailed description of the category", example = "Devices and gadgets")
    private String description;
}
