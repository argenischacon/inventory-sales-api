package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for creating or updating a Customer")
public class CustomerRequestDTO {

    @Schema(description = "DNI of the customer", example = "12345678")
    @NotBlank(message = "DNI is required")
    private String dni;

    @Schema(description = "Name of the customer", example = "John")
    @NotBlank (message = "Name is required")
    private String name;

    @Schema(description = "Last name of the customer", example = "Doe")
    @NotBlank (message = "Last name is required")
    private String lastName;

    @Schema(description = "Email of the customer", example = "John@gmail.com")
    @Email(message = "Must be a valid email format")
    private String email;

    @Schema(description = "Phone number of the customer", example = "123456789")
    private String phone;

    @Schema(description = "Address of the customer", example = "123 Main St")
    private String address;
}
