package com.argenischacon.inventory_sales_api.controller.api;

import com.argenischacon.inventory_sales_api.config.OpenApiExamples;
import com.argenischacon.inventory_sales_api.dto.SaleRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleResponseDTO;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Sales", description = "Endpoints for managing sales")
@RequestMapping("/api/v1/sales")
@SecurityRequirement(name = "bearerAuth")
public interface SaleAPI {

    @Operation(
            summary = "Create a new sale",
            description = "Creates a new sale, including its details. This operation updates product stock. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created - Sale created successfully", content = @Content(
                    schema = @Schema(implementation = SaleResponseDTO.class),
                    mediaType = "application/json"
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Validation Failed", value = OpenApiExamples.Sale.ERROR_400_VALIDATION_FAILED),
                            @ExampleObject(name = "Malformed JSON", value = OpenApiExamples.CommonErrors.ERROR_400_MALFORMED_JSON)
                    }
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Customer or Product not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Customer Not Found", value = OpenApiExamples.Customer.ERROR_404_NOT_FOUND),
                            @ExampleObject(name = "Product Not Found", value = OpenApiExamples.Product.ERROR_404_NOT_FOUND)
                    }
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - Insufficient stock for a product", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Sale.ERROR_409_INSUFFICIENT_STOCK)
            ))
    })
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<SaleResponseDTO> create(@Parameter(description = "Sale object to be created", required = true) @Valid @RequestBody SaleRequestDTO dto);

    @Operation(
            summary = "Update an existing sale",
            description = "Updates an existing sale. This can involve changing the customer, or adding, updating, or removing sale details. Stock levels are adjusted accordingly. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Sale updated successfully", content = @Content(
                    schema = @Schema(implementation = SaleResponseDTO.class),
                    mediaType = "application/json"
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or ID format", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Invalid ID Format", value = OpenApiExamples.CommonErrors.ERROR_400_TYPE_MISMATCH),
                            @ExampleObject(name = "Validation Failed", value = OpenApiExamples.Sale.ERROR_400_VALIDATION_FAILED),
                            @ExampleObject(name = "Malformed JSON", value = OpenApiExamples.CommonErrors.ERROR_400_MALFORMED_JSON)
                    }
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            )),
            @ApiResponse(responseCode = "404", description = "Not Found - Sale, Customer, Product, or SaleDetail not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Sale Not Found", value = OpenApiExamples.Sale.ERROR_404_NOT_FOUND),
                            @ExampleObject(name = "Customer Not Found", value = OpenApiExamples.Customer.ERROR_404_NOT_FOUND),
                            @ExampleObject(name = "Product Not Found", value = OpenApiExamples.Product.ERROR_404_NOT_FOUND),
                            @ExampleObject(name = "Detail Not In Sale", value = OpenApiExamples.Sale.ERROR_404_DETAIL_NOT_IN_SALE)
                    }
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - Insufficient stock for a product", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Sale.ERROR_409_INSUFFICIENT_STOCK)
            ))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<SaleResponseDTO> update(
            @Parameter(description = "ID of the sale to be updated", required = true) @PathVariable Long id,
            @Parameter(description = "Updated sale object", required = true) @Valid @RequestBody SaleRequestDTO dto);

    @Operation(
            summary = "Delete a sale",
            description = "Deletes a sale by its ID. This operation restores the stock for all products included in the sale. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content - Sale deleted successfully"),
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
            @ApiResponse(responseCode = "404", description = "Not Found - Sale not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Sale.ERROR_404_NOT_FOUND)
            ))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<Void> delete(@Parameter(description = "ID of the sale to be deleted", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find a sale by ID",
            description = "Retrieves the details of a specific sale by its ID. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Sale found", content = @Content(
                    schema = @Schema(implementation = SaleResponseDTO.class),
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
            @ApiResponse(responseCode = "404", description = "Not Found - Sale not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Sale.ERROR_404_NOT_FOUND)
            ))
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<SaleResponseDTO> findById(@Parameter(description = "ID of the sale to be retrieved", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find all sales",
            description = "Retrieves a list of all sales. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Successfully retrieved list of sales", content = {@Content(
                    array = @ArraySchema(schema = @Schema(implementation = SaleResponseDTO.class)),
                    mediaType = "application/json"
            )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            ))
    })
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    ResponseEntity<List<SaleResponseDTO>> findAll();
}