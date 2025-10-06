package com.argenischacon.inventory_sales_api.controller.api;

import com.argenischacon.inventory_sales_api.config.OpenApiExamples;
import com.argenischacon.inventory_sales_api.dto.ProductRequestDTO;
import com.argenischacon.inventory_sales_api.dto.ProductResponseDTO;
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

@Tag(name = "Products", description = "Endpoints for managing products")
@RequestMapping("/api/v1/products")
@SecurityRequirement(name = "bearerAuth")
public interface ProductAPI {

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product with a unique name. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created - Product created successfully", content = @Content(
                    schema = @Schema(implementation = ProductResponseDTO.class),
                    mediaType = "application/json"
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Validation Failed", summary = "Business rule validation failed", value = OpenApiExamples.Product.ERROR_400_VALIDATION_FAILED),
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
            @ApiResponse(responseCode = "404", description = "Not Found - Category not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Category.ERROR_404_NOT_FOUND)
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - A product with the same name already exists", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Product.ERROR_409_DUPLICATE_NAME)
            ))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ProductResponseDTO> create(@Parameter(description = "Product object to be created", required = true) @Valid @RequestBody ProductRequestDTO dto);

    @Operation(
            summary = "Update an existing product",
            description = "Updates the details of an existing product by its ID. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Product updated successfully", content = @Content(
                    schema = @Schema(implementation = ProductResponseDTO.class),
                    mediaType = "application/json"
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or ID format", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Invalid ID Format", value = OpenApiExamples.CommonErrors.ERROR_400_TYPE_MISMATCH),
                            @ExampleObject(name = "Validation Failed", summary = "Business rule validation failed", value = OpenApiExamples.Product.ERROR_400_VALIDATION_FAILED),
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
            @ApiResponse(responseCode = "404", description = "Not Found - Product or Category not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "Product Not Found", value = OpenApiExamples.Product.ERROR_404_NOT_FOUND),
                            @ExampleObject(name = "Category Not Found", value = OpenApiExamples.Category.ERROR_404_NOT_FOUND)
                    }
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - A product with the same name already exists", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Product.ERROR_409_DUPLICATE_NAME)
            ))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<ProductResponseDTO> update(
            @Parameter(description = "ID of the product to be updated", required = true) @PathVariable Long id,
            @Parameter(description = "Updated product object", required = true) @Valid @RequestBody ProductRequestDTO dto);

    @Operation(
            summary = "Delete a product",
            description = "Deletes a product by its ID. Fails if the product is associated with any sales. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content - Product deleted successfully"),
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
            @ApiResponse(responseCode = "404", description = "Not Found - Product not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Product.ERROR_404_NOT_FOUND)
            )),
            @ApiResponse(responseCode = "409", description = "Conflict - Product is in use and cannot be deleted", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Product.ERROR_409_IN_USE)
            ))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> delete(@Parameter(description = "ID of the product to be deleted", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find a product by ID",
            description = "Retrieves the details of a specific product by its ID. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Product found", content = @Content(
                    schema = @Schema(implementation = ProductResponseDTO.class),
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
            @ApiResponse(responseCode = "404", description = "Not Found - Product not found", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Product.ERROR_404_NOT_FOUND)
            ))
    })
    @GetMapping("/{id}")
    ResponseEntity<ProductResponseDTO> findById(@Parameter(description = "ID of the product to be retrieved", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find all products",
            description = "Retrieves a list of all products. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Successfully retrieved list of products", content = {@Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductResponseDTO.class)),
                    mediaType = "application/json"
            )}),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.CommonErrors.ERROR_401_UNAUTHORIZED)
            ))
    })
    @GetMapping
    ResponseEntity<List<ProductResponseDTO>> findAll();
}
