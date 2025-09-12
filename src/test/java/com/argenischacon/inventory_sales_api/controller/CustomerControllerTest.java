package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.service.CustomerService;
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

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@TestPropertySource(properties = "app.jpa.auditing-enabled=false")
@AutoConfigureMockMvc(addFilters = false)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    private CustomerRequestDTO baseCustomerRequestDTO;
    private CustomerResponseDTO baseCustomerResponseDTO;

    @BeforeEach
    void setUp() {
        baseCustomerRequestDTO = new CustomerRequestDTO();
        baseCustomerRequestDTO.setDni("12345678");
        baseCustomerRequestDTO.setName("John");
        baseCustomerRequestDTO.setLastName("Doe");

        baseCustomerResponseDTO = new CustomerResponseDTO();
        baseCustomerResponseDTO.setId(1L);
        baseCustomerResponseDTO.setDni("12345678");
        baseCustomerResponseDTO.setName("John");
        baseCustomerResponseDTO.setLastName("Doe");
    }

    // ==== POST ====
    @Test
    @DisplayName("POST /api/v1/customers -> 201 Created")
    void createCustomerSuccess() throws Exception {
        when(customerService.create(any(CustomerRequestDTO.class))).thenReturn(baseCustomerResponseDTO);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(baseCustomerRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dni").value("12345678"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(customerService, times(1)).create(any(CustomerRequestDTO.class));
    }

    @Test
    @DisplayName("POST /api/v1/customers -> 400 Bad Request (validation)")
    void createCustomerValidationError() throws Exception {
        CustomerRequestDTO invalidRequestDTO = new CustomerRequestDTO("12345678", "John", " ", null, null, null);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.lastName").value("Last name is required"));

        verifyNoInteractions(customerService);
    }

    // ==== PUT ====
    @Test
    @DisplayName("PUT /api/v1/customers/{id} -> 200 OK")
    void updateCustomerSuccess() throws Exception {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("87654321", "Jane", "Smith", null, null, null);
        CustomerResponseDTO responseDTO = new CustomerResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setDni("87654321");
        responseDTO.setName("Jane");
        responseDTO.setLastName("Smith");

        when(customerService.update(eq(1L), any(CustomerRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dni").value("87654321"))
                .andExpect(jsonPath("$.name").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));

        verify(customerService, times(1)).update(eq(1L), any(CustomerRequestDTO.class));
    }

    @Test
    @DisplayName("PUT /api/v1/customers/{id} -> 404 Not Found")
    void updateCustomerNotFound() throws Exception {
        CustomerRequestDTO requestDTO = new CustomerRequestDTO("87654321", "Jane", "Smith", null, null, null);

        when(customerService.update(eq(1L), any(CustomerRequestDTO.class))).thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(put("/api/v1/customers/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(customerService, times(1)).update(eq(1L), any(CustomerRequestDTO.class));
    }

    // ==== DELETE ====
    @Test
    @DisplayName("DELETE /api/v1/customers/{id} -> 204 No Content")
    void deleteCustomerSuccess() throws Exception {
        doNothing().when(customerService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/customers/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(customerService, times(1)).delete(eq(1L));
    }

    @Test
    @DisplayName("DELETE /api/v1/customers/{id} -> 404 Not found")
    void deleteCustomerNotFound() throws Exception {
        doThrow(new ResourceNotFoundException("Customer not found")).when(customerService).delete(eq(1L));

        mockMvc.perform(delete("/api/v1/customers/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(customerService, times(1)).delete(eq(1L));
    }

    // ==== GET BY ID ====
    @Test
    @DisplayName("GET /api/v1/customers/{id} -> 200 OK")
    void getCustomerByIdSuccess() throws Exception {
        when(customerService.findById(eq(1L))).thenReturn(baseCustomerResponseDTO);

        mockMvc.perform(get("/api/v1/customers/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.dni").value("12345678"))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));

        verify(customerService, times(1)).findById(eq(1L));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} -> 404 Not Found")
    void getCustomerByIdNotFound() throws Exception {
        when(customerService.findById(eq(1L))).thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(get("/api/v1/customers/{id}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found"));

        verify(customerService, times(1)).findById(eq(1L));
    }

    // ==== GET ALL ====
    @Test
    @DisplayName("GET /api/v1/customers -> 200 OK")
    void getCustomersSuccess() throws Exception {
        when(customerService.findAll()).thenReturn(List.of(baseCustomerResponseDTO));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].dni").value("12345678"))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].lastName").value("Doe"));

        verify(customerService, times(1)).findAll();
    }

    // ==== INTERNAL SERVER ERROR ====
    @Test
    @DisplayName("GET /api/v1/customers/{id} -> 500 Internal Server Error (unexpected exception)")
    void getCustomerByIdInternalServerError() throws Exception {
        when(customerService.findById(eq(1L)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/v1/customers/{id}", 1L))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
