package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.service.CategoryService;
import com.argenischacon.inventory_sales_api.security.JwtUtils;
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


import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
@TestPropertySource(properties = "app.jpa.auditing-enabled=false")
@AutoConfigureMockMvc(addFilters = false)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private CategoryRequestDTO baseCategoryRequestDTO;
    private CategoryResponseDTO baseCategoryResponseDTO;

    @BeforeEach
    void setUp() {
        baseCategoryRequestDTO = new CategoryRequestDTO();
        baseCategoryRequestDTO.setName("Electronics");
        baseCategoryRequestDTO.setDescription("Electronic devices");

        baseCategoryResponseDTO = new CategoryResponseDTO();
        baseCategoryResponseDTO.setId(1L);
        baseCategoryResponseDTO.setName("Electronics");
        baseCategoryResponseDTO.setDescription("Electronic devices");
    }

    // ==== POST ====
    @Test
    @DisplayName("POST /api/v1/categories -> 201 Created")
    void createCategorySuccess() throws Exception {
        when(categoryService.create(any(CategoryRequestDTO.class))).thenReturn(baseCategoryResponseDTO);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseCategoryRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic devices"));

        verify(categoryService, times(1)).create(any(CategoryRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/categories -> 400 Bad Request (validation)")
    void createCategoryValidationError() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO(" ", "Missing name");

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.name").value("Name is required"));

        verifyNoInteractions(categoryService);
    }

    // ==== PUT ====
    @Test
    @DisplayName("PUT /api/v1/categories/{id} -> 200 OK")
    void updateCategorySuccess() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Sports", "Sports Equipment");
        CategoryResponseDTO responseDTO = new CategoryResponseDTO(1L, "Sports", "Sports Equipment", null, null);

        when(categoryService.update(eq(1L), any(CategoryRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Sports"))
                .andExpect(jsonPath("$.description").value("Sports Equipment"));

        verify(categoryService, times(1)).update(eq(1L), any(CategoryRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/categories/{id} -> 404 Not Found")
    void updateCategoryNotFound() throws Exception {
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Sports", "Sports Equipment");
        String expectedMessage = "Category with id 1 not found.";
        when(categoryService.update(eq(1L), any(CategoryRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException(expectedMessage));

        mockMvc.perform(put("/api/v1/categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(categoryService).update(eq(1L), any(CategoryRequestDTO.class));
    }

    // ==== DELETE ====
    @Test
    @DisplayName("DELETE /api/v1/categories/{id} -> 204 No Content")
    void deleteCategorySuccess() throws Exception {
        doNothing().when(categoryService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/categories/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(categoryService).delete(eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/v1/categories/{id} -> 404 Not Found")
    void deleteCategoryNotFound() throws Exception {
        String expectedMessage = "Category with id 1 not found.";
        doThrow(new ResourceNotFoundException(expectedMessage)).when(categoryService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/categories/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(categoryService).delete(eq(1L));
    }

    // ==== GET BY ID ====
    @Test
    @DisplayName("GET /api/v1/categories/{id} -> 200 OK")
    void getCategoryByIdSuccess() throws Exception {
        when(categoryService.findById(eq(1L))).thenReturn(baseCategoryResponseDTO);

        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.description").value("Electronic devices"));

        verify(categoryService).findById(eq(1L));
    }

    @Test
    @DisplayName("GET /api/v1/categories/{id} -> 404 Not Found")
    void getCategoryByIdNotFound() throws Exception {
        String expectedMessage = "Category with id 1 not found.";
        when(categoryService.findById(eq(1L))).thenThrow(new ResourceNotFoundException(expectedMessage));

        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(expectedMessage));

        verify(categoryService).findById(eq(1L));
    }

    // ==== GET ALL ====
    @Test
    @DisplayName("GET /api/v1/categories -> 200 OK (list)")
    void getCategoriesSuccess() throws Exception {
        when(categoryService.findAll()).thenReturn(List.of(baseCategoryResponseDTO));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[0].description").value("Electronic devices"));

        verify(categoryService).findAll();
    }

    @Test
    @DisplayName("GET /api/v1/categories -> 200 OK (Empty List)")
    void getAllCategoriesEmpty() throws Exception {
        when(categoryService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(categoryService).findAll();
    }

    // ==== INTERNAL SERVER ERROR ====
    @Test
    @DisplayName("GET /api/v1/categories/{id} -> 500 Internal Server Error (unexpected exception)")
    void getCategoryByIdInternalServerError() throws Exception {
        when(categoryService.findById(eq(1L)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/categories/{id}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected internal server error occurred"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

}
