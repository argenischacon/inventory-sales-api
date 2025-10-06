package com.argenischacon.inventory_sales_api.controller.api;

import com.argenischacon.inventory_sales_api.config.OpenApiExamples;
import com.argenischacon.inventory_sales_api.dto.AuthRequestDTO;
import com.argenischacon.inventory_sales_api.dto.AuthResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Authentication", description = "Endpoint for user authentication")
@RequestMapping("/api/v1/auth")
public interface AuthAPI {

    @Operation(
            summary = "User Login",
            description = "Authenticates a user with username and password, returning a JWT token upon success."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK - Authentication successful", content = @Content(
                    schema = @Schema(implementation = AuthResponseDTO.class),
                    mediaType = "application/json"
            )),
            @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Auth.ERROR_400_VALIDATION_FAILED)
            )),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = OpenApiExamples.Auth.ERROR_401_INVALID_CREDENTIALS)
            ))
    })
    @PostMapping("/login")
    ResponseEntity<AuthResponseDTO> login(@Parameter(description = "User credentials for login", required = true) @Valid @RequestBody AuthRequestDTO authRequest);
}