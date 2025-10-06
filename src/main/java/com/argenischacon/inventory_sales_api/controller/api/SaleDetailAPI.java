package com.argenischacon.inventory_sales_api.controller.api;

import com.argenischacon.inventory_sales_api.config.OpenApiExamples;
import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Sale Details", description = "Endpoints for retrieving sale details")
@RequestMapping("/api/v1/sale-details")
@SecurityRequirement(name = "bearerAuth")
public interface SaleDetailAPI {

    @Operation(
            summary = "Find all details for a specific sale",
            description = "Retrieves a list of all line items (details) associated with a given sale ID. Accessible by any authenticated user."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Successfully retrieved list of sale details", content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = SaleDetailResponseDTO.class)),
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
    @GetMapping("/{saleId}")
    ResponseEntity<List<SaleDetailResponseDTO>> findBySaleId(@Parameter(description = "ID of the sale to retrieve details for", required = true, example = "1") @PathVariable Long saleId);
}