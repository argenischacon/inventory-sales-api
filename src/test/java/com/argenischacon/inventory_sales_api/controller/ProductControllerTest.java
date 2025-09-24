package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.dto.CategoryNestedDTO;
import com.argenischacon.inventory_sales_api.dto.ProductRequestDTO;
import com.argenischacon.inventory_sales_api.dto.ProductResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.security.JwtUtils;
import com.argenischacon.inventory_sales_api.service.ProductService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@TestPropertySource(properties = "app.jpa.auditing-enabled=false")
@AutoConfigureMockMvc(addFilters = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private ProductRequestDTO baseProductRequestDTO;
    private ProductResponseDTO baseProductResponseDTO;

    @BeforeEach
    void setUp() {
        baseProductRequestDTO = new ProductRequestDTO();
        baseProductRequestDTO.setName("Laptop");
        baseProductRequestDTO.setDescription("Powerful laptop for gaming and work");
        baseProductRequestDTO.setUnitPrice(BigDecimal.valueOf(1200.00));
        baseProductRequestDTO.setStock(50);
        baseProductRequestDTO.setCategoryId(2L);

        baseProductResponseDTO = new ProductResponseDTO();
        baseProductResponseDTO.setId(1L);
        baseProductResponseDTO.setName("Laptop");
        baseProductResponseDTO.setDescription("Powerful laptop for gaming and work");
        baseProductResponseDTO.setUnitPrice(BigDecimal.valueOf(1200.00));
        baseProductResponseDTO.setStock(50);
        baseProductResponseDTO.setCategory(new CategoryNestedDTO(2L, "Electronics"));
    }

    // ==== POST ====
    @Test
    @DisplayName("POST /api/v1/products -> 201 Created")
    void createProductSuccess() throws Exception {
        when(productService.create(any(ProductRequestDTO.class))).thenReturn(baseProductResponseDTO);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseProductRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("Powerful laptop for gaming and work"))
                .andExpect(jsonPath("$.unitPrice").value(1200.00))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.category.id").value(2L));

        verify(productService, times(1)).create(any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/products -> 400 Bad Request")
    void createProductValidationError() throws Exception {
        ProductRequestDTO invalidRequestDTO = new ProductRequestDTO();
        invalidRequestDTO.setName(" "); // <-- Invalid name
        invalidRequestDTO.setDescription("Powerful laptop for gaming and work");
        invalidRequestDTO.setUnitPrice(BigDecimal.valueOf(1200.00));
        invalidRequestDTO.setStock(50);
        invalidRequestDTO.setCategoryId(2L);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").value("Name is required"));

        verifyNoInteractions(productService);
    }
    @Test
    @DisplayName("POST /api/v1/products -> 404 Not Found (Category not found)")
    void createProductCategoryNotFound() throws Exception {
        long nonExistentCategoryId = 999L;
        baseProductRequestDTO.setCategoryId(nonExistentCategoryId);

        when(productService.create(any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseProductRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));
        verify(productService, times(1)).create(any(ProductRequestDTO.class));
    }

    // ==== PUT ====
    @Test
    @DisplayName("PUT /api/v1/products/{id} -> 200 OK")
    void updateProductSuccess() throws Exception {
        baseProductRequestDTO.setName("Smart TV");
        baseProductRequestDTO.setDescription("Smart TV 4K");
        baseProductRequestDTO.setUnitPrice(BigDecimal.valueOf(900.00));
        baseProductRequestDTO.setStock(100);

        baseProductResponseDTO.setId(1L);
        baseProductResponseDTO.setName("Smart TV");
        baseProductResponseDTO.setDescription("Smart TV 4K");
        baseProductResponseDTO.setUnitPrice(BigDecimal.valueOf(900.00));
        baseProductResponseDTO.setStock(100);

        when(productService.update(eq(1L), any(ProductRequestDTO.class))).thenReturn(baseProductResponseDTO);

        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseProductRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Smart TV"))
                .andExpect(jsonPath("$.description").value("Smart TV 4K"))
                .andExpect(jsonPath("$.unitPrice").value(900.00))
                .andExpect(jsonPath("$.stock").value(100))
                .andExpect(jsonPath("$.category.id").value(2L));

        verify(productService, times(1)).update(eq(1L), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} -> 404 Not Found")
    void updateProductNotFound() throws Exception {
        baseProductRequestDTO.setName("Smart TV");
        baseProductRequestDTO.setDescription("Smart TV 4K");
        baseProductRequestDTO.setUnitPrice(BigDecimal.valueOf(900.00));
        baseProductRequestDTO.setStock(100);

        when(productService.update(eq(1L), any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(put("/api/v1/products/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseProductRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(productService, times(1)).update(eq(1L), any(ProductRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/products/{id} -> 404 Not Found (Category not found on update)")
    void updateProductCategoryNotFound() throws Exception {
        long productId = 1L;
        long nonExistentCategoryId = 999L;
        baseProductRequestDTO.setCategoryId(nonExistentCategoryId);

        when(productService.update(eq(productId), any(ProductRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Category not found"));

        mockMvc.perform(put("/api/v1/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseProductRequestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Category not found"));

        verify(productService, times(1)).update(eq(productId), any(ProductRequestDTO.class));
    }

    // ==== DELETE ====
    @Test
    @DisplayName("DELETE /api/v1/products/{id} -> 204 No Content")
    void deleteProductSuccess() throws Exception {
        doNothing().when(productService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(productService, times(1)).delete(eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/v1/products/{id} -> 404 Not Found")
    void deleteProductNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Product not found")).when(productService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/products/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(productService, times(1)).delete(eq(1L));
    }

    // ==== GET BY ID ====
    @Test
    @DisplayName("GET /api/v1/products/{id} -> 200 OK")
    void getProductByIdSuccess() throws Exception {
        when(productService.findById(eq(1L))).thenReturn(baseProductResponseDTO);

        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.description").value("Powerful laptop for gaming and work"))
                .andExpect(jsonPath("$.unitPrice").value(1200.00))
                .andExpect(jsonPath("$.stock").value(50))
                .andExpect(jsonPath("$.category.id").value(2L));

        verify(productService, times(1)).findById(eq(1L));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} -> 404 Not Found")
    void getProductByIdNotFound() throws Exception {
        when(productService.findById(eq(1L))).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));

        verify(productService, times(1)).findById(eq(1L));
    }

    // ==== GET ALL ====
    @Test
    @DisplayName("GET /api/v1/products -> 200 OK")
    void getProductsSuccess() throws Exception {
        when(productService.findAll()).thenReturn(List.of(baseProductResponseDTO));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].description").value("Powerful laptop for gaming and work"))
                .andExpect(jsonPath("$[0].unitPrice").value(1200.00))
                .andExpect(jsonPath("$[0].stock").value(50))
                .andExpect(jsonPath("$[0].category.id").value(2L));

        verify(productService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/products -> 200 OK (Empty List)")
    void getAllProductsEmpty() throws Exception {
        when(productService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(productService, times(1)).findAll();
    }

    // ==== INTERNAL SERVER ERROR ====
    @Test
    @DisplayName("GET /api/v1/products/{id} -> 500 Internal Server Error")
    void getProductByIdInternalServerError() throws Exception {
        when(productService.findById(eq(1L)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected internal server error occurred"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
