package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.dto.*;
import com.argenischacon.inventory_sales_api.exception.InsufficientStockException;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.security.JwtUtils;
import com.argenischacon.inventory_sales_api.service.SaleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SaleController.class)
@TestPropertySource(properties = "app.jpa.auditing-enabled=false")
@AutoConfigureMockMvc(addFilters = false)
public class SaleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SaleService saleService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private SaleRequestDTO baseSaleRequestDTO;
    private SaleResponseDTO baseSaleResponseDTO;

    @BeforeEach
    void setUp() {
        SaleDetailRequestDTO baseSaleDetailRequestDTO = new SaleDetailRequestDTO();
        baseSaleDetailRequestDTO.setQuantity(2);
        baseSaleDetailRequestDTO.setUnitPrice(BigDecimal.valueOf(1000.00));
        baseSaleDetailRequestDTO.setProductId(3L);

        SaleDetailResponseDTO baseSaleDetailResponseDTO = new SaleDetailResponseDTO();
        baseSaleDetailResponseDTO.setId(4L);
        baseSaleDetailResponseDTO.setQuantity(2);
        baseSaleDetailResponseDTO.setUnitPrice(BigDecimal.valueOf(1000.00));
        baseSaleDetailResponseDTO.setProduct(new ProductNestedDTO(3L, "Laptop", BigDecimal.valueOf(1000.00)));
        baseSaleDetailResponseDTO.setSubTotal(BigDecimal.valueOf(2000.00));

        baseSaleRequestDTO = new SaleRequestDTO();
        baseSaleRequestDTO.setCustomerId(2L);
        baseSaleRequestDTO.setSaleDetails(List.of(baseSaleDetailRequestDTO));

        baseSaleResponseDTO = new SaleResponseDTO();
        baseSaleResponseDTO.setId(1L);
        baseSaleResponseDTO.setCustomer(new CustomerNestedDTO(2L, "John", "Doe"));
        baseSaleResponseDTO.setSaleDetails(List.of(baseSaleDetailResponseDTO));
        baseSaleResponseDTO.setTotal(BigDecimal.valueOf(2000.00));
    }

    // ==== POST ====
    @Test
    @DisplayName("POST /api/v1/sales -> 201 Created")
    void createSaleSuccess() throws Exception {
        when(saleService.create(any(SaleRequestDTO.class))).thenReturn(baseSaleResponseDTO);

        mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customer.id").value(2L))
                .andExpect(jsonPath("$.saleDetails[0].id").value(4L))
                .andExpect(jsonPath("$.saleDetails[0].quantity").value(2))
                .andExpect(jsonPath("$.saleDetails[0].unitPrice").value(1000.00))
                .andExpect(jsonPath("$.saleDetails[0].product.id").value(3L))
                .andExpect(jsonPath("$.saleDetails[0].subTotal").value(2000.00))
                .andExpect(jsonPath("$.total").value(2000.00));

        verify(saleService).create(any(SaleRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/sales -> 400 Bad Request")
    void createSaleValidationError() throws Exception {
        SaleRequestDTO invalidRequestDTO = new SaleRequestDTO();
        invalidRequestDTO.setCustomerId(2L);
        invalidRequestDTO.setSaleDetails(List.of()); //<- invalid

        mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.saleDetails").value("Must have at least one sale detail"));

        verifyNoInteractions(saleService);
    }

    @Test
    @DisplayName("POST /api/v1/sales -> 404 Not Found (Customer not found)")
    void createSaleCustomerNotFound() throws Exception {
        when(saleService.create(any(SaleRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(saleService).create(any(SaleRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/sales -> 404 Not Found (Product not found)")
    void createSaleProductNotFound() throws Exception {
        long nonExistentProductId = 999L;
        when(saleService.create(any(SaleRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found with id: " + nonExistentProductId));

        mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: " + nonExistentProductId));

        verify(saleService).create(any(SaleRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/sales -> 409 Conflict (Insufficient Stock)")
    void createSaleInsufficientStock() throws Exception {
        long productId = 3L;
        int requested = 10;
        int available = 5;
        String errorMessage = String.format("Insufficient stock for product 'Laptop'. Requested: %d, Available: %d", requested, available);

        when(saleService.create(any(SaleRequestDTO.class)))
                .thenThrow(new InsufficientStockException(errorMessage, productId, requested, available));

        mockMvc.perform(post("/api/v1/sales")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.details.productId").value(productId))
                .andExpect(jsonPath("$.details.requestQuantity").value(requested))
                .andExpect(jsonPath("$.details.availableStock").value(available));

        verify(saleService).create(any(SaleRequestDTO.class));
    }

    // ==== PUT ====
    @Test
    @DisplayName("PUT /api/v1/sales/{id} -> 200 OK")
    void updateSaleSuccess() throws Exception {
        baseSaleRequestDTO.setCustomerId(3L); // update
        baseSaleResponseDTO.setCustomer(new CustomerNestedDTO(3L, "Jane", "Smith"));

        when(saleService.update(eq(1L), any(SaleRequestDTO.class))).thenReturn(baseSaleResponseDTO);

        mockMvc.perform(put("/api/v1/sales/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customer.id").value(3L))
                .andExpect(jsonPath("$.customer.name").value("Jane"))
                .andExpect(jsonPath("$.customer.lastName").value("Smith"));

        verify(saleService).update(eq(1L), any(SaleRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/sales/{id} -> 404 Not Found")
    void updateSaleNotFound() throws Exception {
        when(saleService.update(eq(1L), any(SaleRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Sale not found"));

        mockMvc.perform(put("/api/v1/sales/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sale not found"));

        verify(saleService).update(eq(1L), any(SaleRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/sales/{id} -> 404 Not Found (Customer not found on update)")
    void updateSaleCustomerNotFound() throws Exception {
        long saleId = 1L;
        long nonExistentCustomerId = 999L;
        baseSaleRequestDTO.setCustomerId(nonExistentCustomerId);

        when(saleService.update(eq(saleId), any(SaleRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(put("/api/v1/sales/{id}", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(saleService).update(eq(saleId), any(SaleRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/sales/{id} -> 404 Not Found (Product not found on update)")
    void updateSaleProductNotFound() throws Exception {
        long saleId = 1L;
        long nonExistentProductId = 999L;
        String errorMessage = "Product not found with id: " + nonExistentProductId;

        when(saleService.update(eq(saleId), any(SaleRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        mockMvc.perform(put("/api/v1/sales/{id}", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));
    }

    @Test
    @DisplayName("PUT /api/v1/sales/{id} -> 404 Not Found (SaleDetail not found in Sale)")
    void updateSaleWithNonExistentSaleDetailId() throws Exception {
        long saleId = 1L;
        long nonExistentSaleDetailId = 999L;
        String errorMessage = "In this sale, there is no sale detail with id: " + nonExistentSaleDetailId;

        // Modify the request DTO to include an invalid detail ID
        baseSaleRequestDTO.getSaleDetails().getFirst().setId(nonExistentSaleDetailId);

        when(saleService.update(eq(saleId), any(SaleRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(errorMessage));

        mockMvc.perform(put("/api/v1/sales/{id}", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(saleService).update(eq(saleId), any(SaleRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/sales/{id} -> 409 Conflict (Insufficient Stock on update)")
    void updateSaleInsufficientStock() throws Exception {
        long saleId = 1L;
        long productId = 3L;
        int requested = 10;
        int available = 5;
        String errorMessage = String.format("Insufficient stock for product 'Laptop'. Requested: %d, Available: %d", requested, available);

        when(saleService.update(eq(saleId), any(SaleRequestDTO.class)))
                .thenThrow(new InsufficientStockException(errorMessage, productId, requested, available));

        mockMvc.perform(put("/api/v1/sales/{id}", saleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseSaleRequestDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.details.productId").value(productId))
                .andExpect(jsonPath("$.details.requestQuantity").value(requested))
                .andExpect(jsonPath("$.details.availableStock").value(available));

        verify(saleService).update(eq(saleId), any(SaleRequestDTO.class));
    }

    // ==== DELETE ====
    @Test
    @DisplayName("DELETE /api/v1/sales/{id} -> 204 No Content")
    void deleteSaleSuccess() throws Exception {
        doNothing().when(saleService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/sales/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(saleService).delete(eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/v1/sales/{id} -> 404 Not Found")
    void deleteSaleNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Sale not found")).when(saleService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/sales/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sale not found"));

        verify(saleService).delete(eq(1L));
    }

    // ==== GET BY ID ====
    @Test
    @DisplayName("GET /api/v1/sales/{id} -> 200 OK")
    void getSaleByIdSuccess() throws Exception {
        when(saleService.findById(eq(1L))).thenReturn(baseSaleResponseDTO);

        mockMvc.perform(get("/api/v1/sales/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.customer.id").value(2L))
                .andExpect(jsonPath("$.saleDetails[0].id").value(4L));

        verify(saleService).findById(eq(1L));
    }

    @Test
    @DisplayName("GET /api/v1/sales/{id} -> 404 Not Found")
    void getSaleByIdNotFound() throws Exception {
        when(saleService.findById(eq(1L)))
                .thenThrow(new ResourceNotFoundException("Sale not found"));

        mockMvc.perform(get("/api/v1/sales/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sale not found"));

        verify(saleService).findById(eq(1L));
    }

    // ==== GET ALL ====
    @Test
    @DisplayName("GET /api/v1/sales -> 200 OK")
    void getAllSalesSuccess() throws Exception {
        when(saleService.findAll()).thenReturn(List.of(baseSaleResponseDTO));

        mockMvc.perform(get("/api/v1/sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].customer.id").value(2L));

        verify(saleService).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/sales -> 200 OK (Empty List)")
    void getAllSalesEmpty() throws Exception {
        when(saleService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/sales"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(saleService).findAll();
    }

    // ==== INTERNAL SERVER ERROR ====
    @Test
    @DisplayName("GET /api/v1/sales/{id} -> 500 Internal Server Error")
    void getSaleByIdInternalServerError() throws Exception {
        when(saleService.findById(eq(1L)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/sales/{id}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected internal server error occurred."))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(saleService).findById(eq(1L));
    }
}
