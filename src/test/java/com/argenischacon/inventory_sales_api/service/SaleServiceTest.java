package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.*;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.SaleMapper;
import com.argenischacon.inventory_sales_api.model.Customer;
import com.argenischacon.inventory_sales_api.model.Product;
import com.argenischacon.inventory_sales_api.model.Sale;
import com.argenischacon.inventory_sales_api.model.SaleDetail;
import com.argenischacon.inventory_sales_api.repository.CustomerRepository;
import com.argenischacon.inventory_sales_api.repository.ProductRepository;
import com.argenischacon.inventory_sales_api.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class SaleServiceTest{
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private SaleMapper saleMapper;

    @Mock
    private SaleRepository saleRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SaleServiceImpl saleService;

    private Sale baseSale;
    private SaleRequestDTO baseSaleRequestDTO;
    private SaleResponseDTO baseSaleResponseDTO;
    private SaleDetail baseSaleDetail;
    private SaleDetailRequestDTO baseSaleDetailRequestDTO;
    private SaleDetailResponseDTO baseSaleDetailResponseDTO;
    private Customer baseCustomer;
    private CustomerNestedDTO baseCustomerNestedDTO;
    private Product baseProduct;
    private ProductNestedDTO baseProductNestedDTO;

    @BeforeEach
    void setUp() {
        // Product (entity)
        baseProduct = new Product();
        baseProduct.setId(10L);
        baseProduct.setName("Smart TV");

        // ProductNestedDTO
        baseProductNestedDTO = new ProductNestedDTO();
        baseProductNestedDTO.setId(10L);
        baseProductNestedDTO.setName("Smart TV");

        // Customer (entity)
        baseCustomer = new Customer();
        baseCustomer.setId(1L);
        baseCustomer.setName("John Doe");

        // CustomerNestedDTO
        baseCustomerNestedDTO = new CustomerNestedDTO();
        baseCustomerNestedDTO.setId(1L);
        baseCustomerNestedDTO.setName("John Doe");

        // SaleDetail
        baseSaleDetail = new SaleDetail();
        baseSaleDetail.setId(100L);
        baseSaleDetail.setQuantity(2);
        baseSaleDetail.setUnitPrice(BigDecimal.valueOf(500));
        baseSaleDetail.setProduct(baseProduct);

        // SaleDetailRequestDTO
        baseSaleDetailRequestDTO = new SaleDetailRequestDTO();
        baseSaleDetailRequestDTO.setQuantity(2);
        baseSaleDetailRequestDTO.setUnitPrice(BigDecimal.valueOf(500));
        baseSaleDetailRequestDTO.setProductId(10L);

        // SaleDetailResponseDTO
        baseSaleDetailResponseDTO = new SaleDetailResponseDTO();
        baseSaleDetailResponseDTO.setId(100L);
        baseSaleDetailResponseDTO.setQuantity(2);
        baseSaleDetailResponseDTO.setUnitPrice(BigDecimal.valueOf(500));
        baseSaleDetailResponseDTO.setProduct(baseProductNestedDTO);
        baseSaleDetailResponseDTO.setSubTotal(BigDecimal.valueOf(1000));

        // Sale (entity)
        baseSale = new Sale();
        baseSale.setId(200L);
        baseSale.setCustomer(baseCustomer);
        baseSale.setSaleDetails(new ArrayList<>(List.of(baseSaleDetail)));

        // SaleRequestDTO
        baseSaleRequestDTO = new SaleRequestDTO();
        baseSaleRequestDTO.setCustomerId(1L);
        baseSaleRequestDTO.setSaleDetails(new ArrayList<>(List.of(baseSaleDetailRequestDTO)));

        // SaleResponseDTO
        baseSaleResponseDTO = new SaleResponseDTO();
        baseSaleResponseDTO.setId(200L);
        baseSaleResponseDTO.setCustomer(baseCustomerNestedDTO);
        baseSaleResponseDTO.setSaleDetails(new ArrayList<>(List.of(baseSaleDetailResponseDTO)));
        baseSaleResponseDTO.setTotal(BigDecimal.valueOf(1000));
    }

    // ==== CREATE ====
    @Test
    void shouldCreateSaleWhenDataIsValid() {
        Long customerId = baseCustomer.getId();
        Long productId = baseProduct.getId();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(productId))).thenReturn(List.of(baseProduct));
        when(saleRepository.save(any(Sale.class))).thenReturn(baseSale);
        when(saleMapper.toResponse(baseSale)).thenReturn(baseSaleResponseDTO);

        SaleResponseDTO result = saleService.create(baseSaleRequestDTO);

        assertEquals("John Doe", result.getCustomer().getName());
        assertEquals(2, result.getSaleDetails().getFirst().getQuantity());
        assertEquals(BigDecimal.valueOf(500), result.getSaleDetails().getFirst().getUnitPrice());
        assertEquals("Smart TV", result.getSaleDetails().getFirst().getProduct().getName());
        assertEquals(BigDecimal.valueOf(1000), result.getSaleDetails().getFirst().getSubTotal());
        assertEquals(BigDecimal.valueOf(1000), result.getTotal());

        verify(customerRepository).findById(customerId);
        verify(productRepository).findAllById(List.of(productId));
        verify(saleRepository).save(any(Sale.class));
        verify(saleMapper).toResponse(baseSale);
    }

    @Test
    void shouldThrowExceptionWhenCreatingSaleNonExistingCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.create(baseSaleRequestDTO));

        assertEquals("Customer not found", ex.getMessage());
        verify(customerRepository).findById(1L);
        verifyNoInteractions(productRepository, saleRepository, saleMapper);
    }

    @Test
    void shouldThrowExceptionWhenCreatingSaleNonExistingProductInSaleDetail() {
        Long customerId = baseCustomer.getId();
        Long productId = baseProduct.getId();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(productId))).thenReturn(List.of());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                saleService.create(baseSaleRequestDTO));

        assertEquals("Product not found with id: " + productId, ex.getMessage());
        verify(customerRepository).findById(customerId);
        verify(productRepository).findAllById(List.of(productId));
        verifyNoInteractions(saleRepository, saleMapper);
    }

    // ==== UPDATE ====
    @Test
    void shouldUpdateSaleWhenDataIsValid() {
        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(baseProduct));
        when(saleRepository.save(baseSale)).thenReturn(baseSale);
        when(saleMapper.toResponse(baseSale)).thenReturn(baseSaleResponseDTO);

        SaleResponseDTO result = saleService.update(200L, baseSaleRequestDTO);

        assertEquals(200L, result.getId());
        verify(saleRepository).findById(200L);
        verify(customerRepository).findById(1L);
        verify(productRepository).findAllById(List.of(10L));
        verify(saleRepository).save(baseSale);
        verify(saleMapper).toResponse(baseSale);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingSale() {
        when(saleRepository.findById(200L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.update(200L, baseSaleRequestDTO));

        assertEquals("Sale not found", ex.getMessage());
        verify(saleRepository).findById(200L);
        verifyNoInteractions(customerRepository, productRepository, saleMapper);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSaleWithNonExistingCustomer() {
        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.update(200L, baseSaleRequestDTO));

        assertEquals("Customer not found", ex.getMessage());
        verify(saleRepository).findById(200L);
        verify(customerRepository).findById(1L);
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSaleWithNonExistingProduct() {
        Long saleId = baseSale.getId();
        Long customerId = baseCustomer.getId();
        Long productId = baseProduct.getId();

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(productId))).thenReturn(List.of());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.update(saleId, baseSaleRequestDTO));

        assertEquals("Product not found with id: " + productId, ex.getMessage());
        verify(saleRepository).findById(saleId);
        verify(customerRepository).findById(customerId);
        verify(productRepository).findAllById(List.of(productId));
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSaleWithNonExistingSaleDetailId() {
        Long saleId = baseSale.getId();
        Long customerId = baseCustomer.getId();
        Long productId = baseProduct.getId();
        baseSaleDetailRequestDTO.setId(999L); // Non-existent detail ID

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(productId))).thenReturn(List.of(baseProduct));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.update(saleId, baseSaleRequestDTO));

        assertEquals("In this sale, there is no sale detail with id: 999", ex.getMessage());
        verify(saleRepository).findById(saleId);
        verify(customerRepository).findById(customerId);
        verify(productRepository).findAllById(List.of(productId));
        verify(saleRepository, never()).save(any(Sale.class));
    }

    // ==== DELETE ====
    @Test
    void shouldDeleteSaleWhenIdExists() {
        when(saleRepository.existsById(200L)).thenReturn(true);

        saleService.delete(200L);

        verify(saleRepository).existsById(200L);
        verify(saleRepository).deleteById(200L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingSale() {
        when(saleRepository.existsById(200L)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.delete(200L));

        assertEquals("Sale not found", ex.getMessage());
        verify(saleRepository).existsById(200L);
        verify(saleRepository, never()).deleteById(anyLong());
    }

    // ==== FIND BY ID ====
    @Test
    void shouldFindSaleByIdWhenIdExists() {
        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(saleMapper.toResponse(baseSale)).thenReturn(baseSaleResponseDTO);

        SaleResponseDTO result = saleService.findById(200L);

        assertEquals(200L, result.getId());
        assertNotNull(result.getCustomer());
        verify(saleRepository).findById(200L);
        verify(saleMapper).toResponse(baseSale);
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistingSale() {
        when(saleRepository.findById(200L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.findById(200L));

        assertEquals("Sale not found", ex.getMessage());
        verify(saleRepository).findById(200L);
        verifyNoInteractions(saleMapper);
    }

    // ==== FIND ALL ====
    @Test
    void shouldFindAllSales() {
        when(saleRepository.findAll()).thenReturn(List.of(baseSale));
        when(saleMapper.toResponseList(List.of(baseSale))).thenReturn(List.of(baseSaleResponseDTO));

        List<SaleResponseDTO> result = saleService.findAll();

        assertEquals(1, result.size());
        assertEquals(200L, result.getFirst().getId());
        verify(saleRepository).findAll();
        verify(saleMapper).toResponseList(List.of(baseSale));
    }
}
