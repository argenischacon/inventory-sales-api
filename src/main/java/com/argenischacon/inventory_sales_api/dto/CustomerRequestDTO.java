package com.argenischacon.inventory_sales_api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDTO {
    @NotBlank(message = "DNI is required")
    private String dni;

    @NotBlank (message = "Name is required")
    private String name;

    @NotBlank (message = "Last name is required")
    private String lastName;

    @Email(message = "Must be a valid email format")
    private String email;

    private String phone;
    private String address;
}
