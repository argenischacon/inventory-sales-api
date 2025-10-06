package com.argenischacon.inventory_sales_api.controller.api;

import com.argenischacon.inventory_sales_api.config.OpenApiExamples;
import com.argenischacon.inventory_sales_api.dto.audit.ProductRevisionDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Product Audit", description = "Endpoints for auditing product history")
@RequestMapping("/api/v1/products/audit")
@SecurityRequirement(name = "bearerAuth")
public interface ProductAuditAPI {

    @Operation(
            summary = "Get all revisions for a product",
            description = "Retrieves the complete audit history for a specific product by its ID. Requires ADMIN privileges."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Successfully retrieved product revisions", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = ProductRevisionDTO.class)),
                    mediaType = "application/json",
                    examples = @ExampleObject(value = OpenApiExamples.ProductAudit.SUCCESS_200_OK)
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
            ))
    })
    @GetMapping("/{productId}/revisions")
    @PreAuthorize("hasRole('ADMIN')")
    ResponseEntity<List<ProductRevisionDTO>> getProductRevisions(@Parameter(description = "ID of the product to retrieve audit history for", required = true, example = "1") @PathVariable Long productId);
}