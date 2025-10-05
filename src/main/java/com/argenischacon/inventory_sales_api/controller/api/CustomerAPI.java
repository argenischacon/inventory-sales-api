package com.argenischacon.inventory_sales_api.controller.api;

import com.argenischacon.inventory_sales_api.config.OpenApiExamples;
import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Customers", description = "Endpoints for managing customers")
@RequestMapping("/api/v1/customers")
@SecurityRequirement(name = "bearerAuth")
public interface CustomerAPI {

    @Operation(
            summary = "Create a new customer",
            description = "Creates a new customer with a unique DNI. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created - Customer created successfully", content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Validation Failed", summary = "Business rule validation failed", value = OpenApiExamples.Customer.ERROR_400_VALIDATION_FAILED),
                            @ExampleObject(name = "Malformed JSON", summary = "Invalid JSON format", value = OpenApiExamples.CommonErrors.ERROR_400_MALFORMED_JSON)
                    }
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_403_FORBIDDEN)
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - A customer with the same DNI already exists", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Customer.ERROR_409_DUPLICATE_DNI)
            ))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CustomerResponseDTO> create(@Parameter(description = "Customer object to be created", required = true) @Valid @RequestBody CustomerRequestDTO dto);

    @Operation(
            summary = "Update an existing customer",
            description = "Updates the details of an existing customer by its ID. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Customer updated successfully", content = @Content(schema = @Schema(implementation = CustomerResponseDTO.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or ID format", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Invalid ID Format", value = OpenApiExamples.CommonErrors.ERROR_400_TYPE_MISMATCH),
                            @ExampleObject(name = "Validation Failed", summary = "Business rule validation failed", value = OpenApiExamples.Customer.ERROR_400_VALIDATION_FAILED),
                            @ExampleObject(name = "Malformed JSON", summary = "Invalid JSON format", value = OpenApiExamples.CommonErrors.ERROR_400_MALFORMED_JSON)
                    }
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_403_FORBIDDEN)
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Customer not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Customer.ERROR_404_NOT_FOUND)
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - A customer with the same DNI already exists", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Customer.ERROR_409_DUPLICATE_DNI)
            ))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CustomerResponseDTO> update(
            @Parameter(description = "ID of the customer to be updated", required = true) @PathVariable Long id,
            @Parameter(description = "Updated customer object", required = true) @Valid @RequestBody CustomerRequestDTO dto);

    @Operation(
            summary = "Delete a customer",
            description = "Deletes a customer by its ID. Fails if the customer is associated with any sales. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content - Customer deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid ID format", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_400_TYPE_MISMATCH)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            )),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_403_FORBIDDEN)
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Customer not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Customer.ERROR_404_NOT_FOUND)
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - Customer is in use and cannot be deleted", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Customer.ERROR_409_IN_USE)
            ))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> delete(@Parameter(description = "ID of the customer to be deleted", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find a customer by ID",
            description = "Retrieves the details of a specific customer by its ID. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Customer found", content = @Content(
                    schema = @Schema(implementation = CustomerResponseDTO.class),
                    mediaType = "application/json"
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid ID format", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_400_TYPE_MISMATCH)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Customer not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Customer.ERROR_404_NOT_FOUND)
            ))
    })
    @GetMapping("/{id}")
    ResponseEntity<CustomerResponseDTO> findById(@Parameter(description = "ID of the customer to be retrieved", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find all customers",
            description = "Retrieves a list of all customers. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Successfully retrieved list of customers", content = {@Content(
                    array = @ArraySchema(schema = @Schema(implementation = CustomerResponseDTO.class)),
                    mediaType = "application/json"
            )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            ))
    })
    @GetMapping
    ResponseEntity<List<CustomerResponseDTO>> findAll();
}