package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.*;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.SaleMapper;
import com.argenischacon.inventory_sales_api.exception.InsufficientStockException;
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
import java.util.Collections;

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
        baseProduct.setStock(20);

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
        // Arrange
        int initialStock = baseProduct.getStock();
        int quantitySold = baseSaleDetailRequestDTO.getQuantity();
        Long customerId = baseCustomer.getId();
        Long productId = baseProduct.getId();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(productId))).thenReturn(List.of(baseProduct));
        when(saleRepository.save(any(Sale.class))).thenReturn(baseSale);
        when(saleMapper.toResponse(baseSale)).thenReturn(baseSaleResponseDTO);

        // Act
        SaleResponseDTO result = saleService.create(baseSaleRequestDTO);

        // Assert
        // Verify stock was reduced
        assertEquals(initialStock - quantitySold, baseProduct.getStock());

        // Verify the response DTO is correct
        assertEquals("John Doe", result.getCustomer().getName());
        assertEquals(2, result.getSaleDetails().getFirst().getQuantity());
        assertEquals(BigDecimal.valueOf(500), result.getSaleDetails().getFirst().getUnitPrice());
        assertEquals("Smart TV", result.getSaleDetails().getFirst().getProduct().getName());
        assertEquals(BigDecimal.valueOf(1000), result.getTotal());

        // Verify mock interactions
        verify(customerRepository).findById(customerId);
        verify(productRepository).findAllById(List.of(productId));
        verify(saleRepository).save(any(Sale.class));
        verify(saleMapper).toResponse(baseSale);
    }

    @Test
    void shouldThrowExceptionWhenCreatingSaleNonExistingCustomer() {
        long customerId = baseSaleRequestDTO.getCustomerId();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.create(baseSaleRequestDTO));

        assertEquals("Customer with id " + customerId + " not found.", ex.getMessage());
        verify(customerRepository).findById(customerId);
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

        assertEquals("Product with id " + productId + " not found.", ex.getMessage());
        verify(customerRepository).findById(customerId);
        verify(productRepository).findAllById(List.of(productId));
        verifyNoInteractions(saleRepository, saleMapper);
    }

    @Test
    void shouldThrowInsufficientStockExceptionOnCreate() {
        // Arrange: Try to create a sale with quantity 30, but stock is only 20
        baseSaleDetailRequestDTO.setQuantity(30);
        int requestedQuantity = 30;

        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(baseProduct));

        // Act & Assert
        InsufficientStockException ex = assertThrows(InsufficientStockException.class,
                () -> saleService.create(baseSaleRequestDTO));

        assertEquals(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d.",
                baseProduct.getName(), requestedQuantity, baseProduct.getStock()), ex.getMessage());
    }

    // ==== UPDATE ====
    @Test
    void shouldUpdateSaleWhenDataIsValid() {
        // Arrange: Decrease quantity from 2 to 1
        int initialStock = baseProduct.getStock();
        baseSaleDetailRequestDTO.setId(100L);
        baseSaleDetailRequestDTO.setQuantity(1);

        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(baseProduct));
        when(saleRepository.save(baseSale)).thenReturn(baseSale);
        when(saleMapper.toResponse(baseSale)).thenReturn(baseSaleResponseDTO);

        // Act
        SaleResponseDTO result = saleService.update(200L, baseSaleRequestDTO);

        // Assert: Verify response and stock change
        assertEquals(200L, result.getId());
        // Stock should increase by 1 because quantity was reduced from 2 to 1
        assertEquals(initialStock + 1, baseProduct.getStock());

        // Verify mock interactions
        verify(saleRepository).findById(200L);
        verify(customerRepository).findById(1L);
        verify(productRepository).findAllById(List.of(10L));
        verify(saleRepository).save(baseSale);
        verify(saleMapper).toResponse(baseSale);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingSale() {
        long saleId = 200L;
        when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.update(saleId, baseSaleRequestDTO));

        assertEquals("Sale with id " + saleId + " not found.", ex.getMessage());
        verify(saleRepository).findById(saleId);
        verifyNoInteractions(customerRepository, productRepository, saleMapper);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingSaleWithNonExistingCustomer() {
        long saleId = 200L;
        long customerId = baseSaleRequestDTO.getCustomerId();
        when(saleRepository.findById(saleId)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.update(saleId, baseSaleRequestDTO));

        assertEquals("Customer with id " + customerId + " not found.", ex.getMessage());
        verify(saleRepository).findById(saleId);
        verify(customerRepository).findById(customerId);
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

        assertEquals("Product with id " + productId + " not found.", ex.getMessage());
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
        long nonExistentDetailId = 999L;
        baseSaleDetailRequestDTO.setId(nonExistentDetailId); // Non-existent detail ID

        when(saleRepository.findById(saleId)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(productId))).thenReturn(List.of(baseProduct));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.update(saleId, baseSaleRequestDTO));

        assertEquals("In this sale with id " + saleId + ", there is no sale detail with id " + nonExistentDetailId + ".", ex.getMessage());
        verify(saleRepository).findById(saleId);
        verify(customerRepository).findById(customerId);
        verify(productRepository).findAllById(List.of(productId));
        verify(saleRepository, never()).save(any(Sale.class));
    }

    @Test
    void shouldDecreaseStockWhenIncreasingQuantityOnUpdate() {
        // Arrange: Increase quantity from 2 to 5
        int initialStock = baseProduct.getStock();
        baseSaleDetailRequestDTO.setId(100L);
        baseSaleDetailRequestDTO.setQuantity(5);

        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(baseProduct));
        when(saleRepository.save(any(Sale.class))).thenReturn(baseSale);
        when(saleMapper.toResponse(any(Sale.class))).thenReturn(baseSaleResponseDTO);

        // Act
        saleService.update(200L, baseSaleRequestDTO);

        // Assert: Stock should decrease by 3 (because quantity increased from 2 to 5)
        assertEquals(initialStock - 3, baseProduct.getStock());
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void shouldDecreaseStockWhenAddingNewDetailOnUpdate() {
        // Arrange: Add a new detail without an ID
        int initialStock = baseProduct.getStock();
        // Start with a sale that has no details, so we only test the addition logic
        baseSale.setSaleDetails(new ArrayList<>());
        baseSaleDetailRequestDTO.setId(null);

        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(baseProduct));

        // Act
        saleService.update(200L, baseSaleRequestDTO);

        // Assert: Stock should decrease by the quantity of the new detail (2)
        assertEquals(initialStock - 2, baseProduct.getStock());
        verify(saleRepository).save(any(Sale.class));
    }

    @Test
    void shouldRestoreStockWhenRemovingDetailOnUpdate() {
        // Arrange: The original sale has one detail. The update request has zero.
        int initialStock = baseProduct.getStock();
        int quantityToRestore = baseSaleDetail.getQuantity();
        baseSaleRequestDTO.setSaleDetails(Collections.emptyList());

        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        saleService.update(200L, baseSaleRequestDTO);

        // Assert: Stock should be restored by the quantity of the removed detail
        assertEquals(initialStock + quantityToRestore, baseProduct.getStock());
    }

    @Test
    void shouldThrowInsufficientStockExceptionOnUpdate() {
        // Arrange: Try to increase quantity from 2 to 30, but stock is only 20
        baseSaleDetailRequestDTO.setId(100L);
        baseSaleDetailRequestDTO.setQuantity(30);
        int quantityChange = 30 - baseSaleDetail.getQuantity();

        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(baseCustomer));
        when(productRepository.findAllById(List.of(10L))).thenReturn(List.of(baseProduct));

        // Act & Assert
        InsufficientStockException ex = assertThrows(InsufficientStockException.class,
                () -> saleService.update(200L, baseSaleRequestDTO));

        assertEquals(String.format("Insufficient stock for product '%s'. Requested: %d, Available: %d.",
                baseProduct.getName(), quantityChange, baseProduct.getStock()), ex.getMessage());

        verify(saleRepository, never()).save(any(Sale.class));
    }

    // ==== DELETE ====
    @Test
    void shouldDeleteSaleWhenIdExists() {
        // Arrange
        int initialStock = baseProduct.getStock();
        int quantitySold = baseSaleDetail.getQuantity();
        when(saleRepository.findById(200L)).thenReturn(Optional.of(baseSale));

        // Act
        saleService.delete(200L);

        // Assert: Stock should be restored
        assertEquals(initialStock + quantitySold, baseProduct.getStock());
        verify(saleRepository).findById(200L);
        verify(saleRepository).deleteById(200L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingSale() {
        // Arrange
        long saleId = 200L;
        when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.delete(saleId));

        assertEquals("Sale with id " + saleId + " not found.", ex.getMessage());
        verify(saleRepository).findById(saleId);
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
        long saleId = 200L;
        when(saleRepository.findById(saleId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> saleService.findById(saleId));

        assertEquals("Sale with id " + saleId + " not found.", ex.getMessage());
        verify(saleRepository).findById(saleId);
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
