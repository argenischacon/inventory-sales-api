package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.dto.ProductNestedDTO;
import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.security.JwtUtils;
import com.argenischacon.inventory_sales_api.service.SaleDetailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleDetailController.class)
@TestPropertySource(properties = "app.jpa.auditing-enabled=false")
@AutoConfigureMockMvc(addFilters = false)
public class SaleDetailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SaleDetailService saleDetailService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private SaleDetailResponseDTO baseSaleDetailResponseDTO;

    @BeforeEach
    void setUp() {
        baseSaleDetailResponseDTO = new SaleDetailResponseDTO();
        baseSaleDetailResponseDTO.setId(1L);
        baseSaleDetailResponseDTO.setQuantity(2);
        baseSaleDetailResponseDTO.setUnitPrice(BigDecimal.valueOf(1000.00));
        baseSaleDetailResponseDTO.setProduct(new ProductNestedDTO(2L, "Laptop", BigDecimal.valueOf(1000.00)));
        baseSaleDetailResponseDTO.setSubTotal(BigDecimal.valueOf(2000.00));
    }

    // ==== GET BY SALE ID ====
    @Test
    @DisplayName("GET /api/v1/sale-details/{saleId} -> 200 OK")
    void getSaleDetailsBySaleIdSuccess() throws Exception {
        when(saleDetailService.findBySaleId(eq(5L))).thenReturn(List.of(baseSaleDetailResponseDTO));

        mockMvc.perform(get("/api/v1/sale-details/{saleId}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].quantity").value(2))
                .andExpect(jsonPath("$[0].unitPrice").value(1000.00))
                .andExpect(jsonPath("$[0].product.id").value(2L))
                .andExpect(jsonPath("$[0].subTotal").value(2000.00));

        verify(saleDetailService, times(1)).findBySaleId(eq(5L));
    }

    @Test
    @DisplayName("GET /api/v1/sale-details/{saleId} -> 404 Not Found")
    void getSaleDetailsBySaleIdNotFound() throws Exception {
        when(saleDetailService.findBySaleId(eq(5L))).thenThrow(new ResourceNotFoundException("Sale not found"));

        mockMvc.perform(get("/api/v1/sale-details/{saleId}", 5L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sale not found"));

        verify(saleDetailService, times(1)).findBySaleId(eq(5L));
    }

    @Test
    @DisplayName("GET /api/v1/sale-details/{saleId} -> 200 OK (Empty List)")
    void getSaleDetailsBySaleIdEmpty() throws Exception {
        Long saleId = 5L;
        when(saleDetailService.findBySaleId(eq(saleId))).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/sale-details/{saleId}", saleId))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(saleDetailService, times(1)).findBySaleId(eq(saleId));
    }

    // ==== INTERNAL SERVER ERROR ====
    @Test
    @DisplayName("GET /api/v1/sale-details/{saleId} -> 500 Internal Server Error")
    void getSaleDetailsBySaleIdInternalServerError() throws Exception {
        when(saleDetailService.findBySaleId(eq(5L))).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/sale-details/{saleId}", 5L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected internal server error occurred"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
