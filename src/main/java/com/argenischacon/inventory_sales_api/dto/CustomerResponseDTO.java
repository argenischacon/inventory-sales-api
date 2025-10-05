package com.argenischacon.inventory_sales_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Data transfer object for Customer responses")
public class CustomerResponseDTO {

    @Schema(description = "Unique identifier for the customer", example = "1")
    private Long id;

    @Schema(description = "DNI of the customer", example = "12345678")
    private String dni;

    @Schema(description = "Name of the customer", example = "John")
    private String name;

    @Schema(description = "Last name of the customer", example = "Doe")
    private String lastName;

    @Schema(description = "Email of the customer", example = "John@gmail.com")
    private String email;

    @Schema(description = "Phone number of the customer", example = "123456789")
    private String phone;

    @Schema(description = "Address of the customer", example = "123 Main St")
    private String address;

    @Schema(description = "Timestamp when the customer was created", example = "2024-08-01T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the customer was last updated", example = "2024-08-01T11:00:00")
    private LocalDateTime updatedAt;
}
