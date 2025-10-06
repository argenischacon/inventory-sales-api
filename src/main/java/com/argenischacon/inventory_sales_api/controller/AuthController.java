package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.controller.api.AuthAPI;
import com.argenischacon.inventory_sales_api.dto.AuthRequestDTO;
import com.argenischacon.inventory_sales_api.dto.AuthResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ErrorResponse;
import com.argenischacon.inventory_sales_api.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class AuthController implements AuthAPI {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Override
    public ResponseEntity<AuthResponseDTO> login(AuthRequestDTO authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));

            String token = jwtUtils.generateToken(authentication);
            return ResponseEntity.ok(new AuthResponseDTO(token));

        } catch (AuthenticationException e) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .message("Invalid credentials.")
                    .build();
            return new ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}
