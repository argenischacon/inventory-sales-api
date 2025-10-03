package com.argenischacon.inventory_sales_api.controller.api;

import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
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

@Tag(name = "Categories", description = "Endpoints for managing categories")
@RequestMapping("/api/v1/categories")
@SecurityRequirement(name = "bearerAuth")
public interface CategoryAPI {
    @Operation(
            summary = "Create a new Category",
            description = "Creates a new category with a unique name. This operation requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Created - Category created successfully", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict - A category with the same name already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CategoryResponseDTO> create(@Parameter(description = "Category object to be created", required = true) @Valid @RequestBody CategoryRequestDTO dto);

    @Operation(
            summary = "Update an existing category",
            description = "Updates the details of an existing category by its ID. This operation requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Category updated successfully", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or invalid ID format", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found - Category not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict - A category with the same name already exists", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<CategoryResponseDTO> update(
            @Parameter(description = "ID of the category to be updated", required = true) @PathVariable Long id,
            @Parameter(description = "Updated category object", required = true) @Valid @RequestBody CategoryRequestDTO dto);

    @Operation(
            summary = "Delete a Category",
            description = "Deletes a category by its ID. This operation fails if the category is in use by any products and requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "No Content - Category deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid ID format", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found - Category not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "409", description = "Conflict - Category is in use and cannot be deleted", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<Void> delete(@Parameter(description = "ID of the category to be deleted", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find a category by ID",
            description = "Retrieves the details of a specific category by its ID. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Category found", content = @Content(schema = @Schema(implementation = CategoryResponseDTO.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid ID format", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Not Found - Category not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    ResponseEntity<CategoryResponseDTO> findById(@Parameter(description = "ID of the category to be retrieved", required = true) @PathVariable Long id);

    @Operation(
            summary = "Find all Categories",
            description = "Retrieves a list of all categories. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Successfully retrieved list of categories", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class)), mediaType = "application/json") }),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required or invalid token", content = @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = "application/json"))
    })
    @GetMapping
    ResponseEntity<List<CategoryResponseDTO>> findAll();
}
