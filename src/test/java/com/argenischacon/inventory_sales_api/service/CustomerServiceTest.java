package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.exception.DuplicateResourceException;
import com.argenischacon.inventory_sales_api.exception.ResourceInUseException;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.CustomerMapper;
import com.argenischacon.inventory_sales_api.model.Customer;
import com.argenischacon.inventory_sales_api.model.Sale;
import com.argenischacon.inventory_sales_api.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    CustomerServiceImpl customerService;

    private CustomerRequestDTO baseRequestDTO;
    private CustomerResponseDTO baseResponseDTO;
    private Customer baseCustomer;

    @BeforeEach
    void setup() {
        baseRequestDTO = new CustomerRequestDTO();
        baseRequestDTO.setDni("12345678");
        baseRequestDTO.setName("John");

        baseResponseDTO = new CustomerResponseDTO();
        baseResponseDTO.setId(1L);
        baseResponseDTO.setDni("12345678");
        baseResponseDTO.setName("John");

        baseCustomer = new Customer();
        baseCustomer.setId(1L);
        baseCustomer.setDni("12345678");
        baseCustomer.setName("John");
    }

    // ==== CREATE =====
    @Test
    void shouldCreateCustomerWhenDataIsValid() {
        when(customerMapper.toEntity(baseRequestDTO)).thenReturn(baseCustomer);
        when(customerRepository.save(baseCustomer)).thenReturn(baseCustomer);
        when(customerMapper.toResponse(baseCustomer)).thenReturn(baseResponseDTO);

        CustomerResponseDTO result = customerService.create(baseRequestDTO);

        assertEquals("12345678", result.getDni());
        assertEquals("John", result.getName());
        verify(customerRepository, times(1)).save(baseCustomer);
    }

    @Test
    void shouldThrowExceptionWhenCreatingCustomerWithDuplicateDni() {
        // Arrange
        when(customerRepository.existsByDni("12345678")).thenReturn(true);

        // Act & Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> customerService.create(baseRequestDTO));

        assertEquals("A customer with the same DNI already exists.", ex.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // ==== UPDATE =====
    @Test
    void shouldUpdateCustomerWhenIdExists() {
        CustomerRequestDTO updatingCustomer = new CustomerRequestDTO();
        updatingCustomer.setDni("87654321");
        updatingCustomer.setName("Jane");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));

        doAnswer(invocation -> {
            CustomerRequestDTO dto = invocation.getArgument(0);
            Customer entity = invocation.getArgument(1);
            entity.setDni(dto.getDni());
            entity.setName(dto.getName());
            baseResponseDTO.setDni(dto.getDni());
            baseResponseDTO.setName(dto.getName());
            return null;
        }).when(customerMapper).updateEntityFromDto(updatingCustomer, baseCustomer);

        when(customerRepository.save(baseCustomer)).thenReturn(baseCustomer);
        when(customerMapper.toResponse(baseCustomer)).thenReturn(baseResponseDTO);

        CustomerResponseDTO result = customerService.update(1L, updatingCustomer);

        assertEquals("87654321", result.getDni());
        assertEquals("Jane", result.getName());
        verify(customerRepository, times(1)).save(baseCustomer);
        verify(customerMapper, times(1)).updateEntityFromDto(updatingCustomer, baseCustomer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> customerService.update(1L, baseRequestDTO)
        );

        assertEquals("Customer with id 1 not found.", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingCustomerWithDuplicateDni() {
        // Arrange: Update customer with an existing DNI
        CustomerRequestDTO updatingRequestDTO = new CustomerRequestDTO();
        updatingRequestDTO.setDni("87654321B");

        Customer existingCustomerWithSameDni = new Customer();
        existingCustomerWithSameDni.setId(2L); // A different customer ID
        existingCustomerWithSameDni.setDni("87654321B");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(customerRepository.findByDni("87654321B")).thenReturn(Optional.of(existingCustomerWithSameDni));

        // Act & Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> customerService.update(1L, updatingRequestDTO));

        assertEquals("A customer with the same DNI already exists.", ex.getMessage());
        verify(customerRepository, never()).save(any(Customer.class));
    }

    // ==== DELETE =====
    @Test
    void shouldDeleteCustomerWhenIdExists() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        doNothing().when(customerRepository).delete(baseCustomer);

        // Act & Assert
        customerService.delete(1L);

        verify(customerRepository, times(1)).delete(baseCustomer);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCustomer() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> customerService.delete(1L));

        assertEquals("Customer with id 1 not found.", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingCustomerWithAssociatedSales() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        Sale sale = new Sale();
        sale.setId(5L);
        sale.setCustomer(baseCustomer);
        baseCustomer.setSales(Set.of(sale));

        // Act & Assert
        ResourceInUseException ex = assertThrows(ResourceInUseException.class,
                () -> customerService.delete(1L));

        assertEquals("Cannot delete customer: it is associated with existing sales.", ex.getMessage());
    }

    // ==== FIND BY ID =====
    @Test
    void shouldFindCustomerByIdWhenIdExists() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(customerMapper.toResponse(baseCustomer)).thenReturn(baseResponseDTO);

        CustomerResponseDTO result = customerService.findById(1L);

        assertEquals("12345678", result.getDni());
        assertEquals("John", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistingCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> customerService.findById(1L));

        assertEquals("Customer with id 1 not found.", ex.getMessage());
    }

    // ==== FIND ALL =====
    @Test
    void shouldFindAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(baseCustomer));
        when(customerMapper.toResponseList(List.of(baseCustomer))).thenReturn(List.of(baseResponseDTO));

        List<CustomerResponseDTO> result = customerService.findAll();

        assertEquals(1, result.size());
        assertEquals("12345678", result.getFirst().getDni());
        assertEquals("John", result.getFirst().getName());
    }
}
