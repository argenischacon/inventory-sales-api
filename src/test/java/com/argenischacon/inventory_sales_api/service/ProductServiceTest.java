package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CategoryNestedDTO;
import com.argenischacon.inventory_sales_api.dto.ProductRequestDTO;
import com.argenischacon.inventory_sales_api.dto.ProductResponseDTO;
import com.argenischacon.inventory_sales_api.exception.DuplicateResourceException;
import com.argenischacon.inventory_sales_api.exception.ResourceInUseException;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.ProductMapper;
import com.argenischacon.inventory_sales_api.model.Category;
import com.argenischacon.inventory_sales_api.model.Product;
import com.argenischacon.inventory_sales_api.model.SaleDetail;
import com.argenischacon.inventory_sales_api.repository.CategoryRepository;
import com.argenischacon.inventory_sales_api.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category baseCategory;
    private CategoryNestedDTO baseCategoryNestedDTO;
    private Product baseProduct;
    private ProductRequestDTO baseProductRequestDTO;
    private ProductResponseDTO baseProductResponseDTO;

    @BeforeEach
    void setUp() {
        // Category entity
        baseCategory = new Category();
        baseCategory.setId(1L);
        baseCategory.setName("Electronics");
        baseCategory.setDescription("Electronic devices");

        // Category nested DTO
        baseCategoryNestedDTO = new CategoryNestedDTO();
        baseCategoryNestedDTO.setId(1L);
        baseCategoryNestedDTO.setName("Electronics");

        // Product entity
        baseProduct = new Product();
        baseProduct.setId(1L);
        baseProduct.setName("Smart TV");
        baseProduct.setDescription("Smart TV 4K");
        baseProduct.setCategory(baseCategory);

        // Product request DTO
        baseProductRequestDTO = new ProductRequestDTO();
        baseProductRequestDTO.setName("Smart TV");
        baseProductRequestDTO.setDescription("Smart TV 4K");
        baseProductRequestDTO.setCategoryId(1L);

        // Product response DTO
        baseProductResponseDTO = new ProductResponseDTO();
        baseProductResponseDTO.setId(1L);
        baseProductResponseDTO.setName("Smart TV");
        baseProductResponseDTO.setDescription("Smart TV 4K");
        baseProductResponseDTO.setCategory(baseCategoryNestedDTO);

    }

    // ===== CREATE =====
    @Test
    void shouldCreateProductWhenDataIsValid() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(baseCategory));
        when(productMapper.toEntity(baseProductRequestDTO)).thenReturn(baseProduct);
        when(productRepository.save(baseProduct)).thenReturn(baseProduct);
        when(productMapper.toResponse(baseProduct)).thenReturn(baseProductResponseDTO);

        ProductResponseDTO result = productService.create(baseProductRequestDTO);

        assertEquals("Smart TV", result.getName());
        assertEquals("Smart TV 4K", result.getDescription());
        assertEquals(1L, result.getCategory().getId());
        assertEquals("Electronics", result.getCategory().getName());

        verify(productRepository, times(1)).save(baseProduct);
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithDuplicateName() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(baseCategory));
        when(productRepository.existsByName("Smart TV")).thenReturn(true);

        // Act & Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> productService.create(baseProductRequestDTO));

        assertEquals("A product with the name 'Smart TV' already exists.", ex.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithNonExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.create(baseProductRequestDTO));

        assertEquals("Category not found", ex.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ==== UPDATE ====
    @Test
    void shouldUpdateProductWhenIdExistsAndCategoryUnchanged() {
        ProductRequestDTO updatingRequestDTO = new ProductRequestDTO();
        updatingRequestDTO.setName("TV Android 4K");
        updatingRequestDTO.setDescription("Smart TV with Android TV OS");
        updatingRequestDTO.setCategoryId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(baseProduct));
        mockMapperUpdate(updatingRequestDTO);
        when(productRepository.save(baseProduct)).thenReturn(baseProduct);
        baseProductResponseDTO.setName("TV Android 4K");
        baseProductResponseDTO.setDescription("Smart TV with Android TV OS");
        when(productMapper.toResponse(baseProduct)).thenReturn(baseProductResponseDTO);

        ProductResponseDTO result = productService.update(1L, updatingRequestDTO);

        assertEquals("TV Android 4K", result.getName());
        assertEquals("Smart TV with Android TV OS", result.getDescription());

        verify(categoryRepository, never()).findById(anyLong());
        verify(productRepository).save(baseProduct);
    }

    @Test
    void shouldUpdateProductAndChangeCategoryWhenCategoryIdIsDifferent() {
        Category newCategory = new Category();
        newCategory.setId(2L);
        newCategory.setName("Home Appliances");

        CategoryNestedDTO newCategoryNestedDTO = new CategoryNestedDTO();
        newCategoryNestedDTO.setId(2L);
        newCategoryNestedDTO.setName("Home Appliances");

        ProductRequestDTO updatingRequestDTO = new ProductRequestDTO();
        updatingRequestDTO.setName("Washing Machine");
        updatingRequestDTO.setDescription("Automatic washing machine");
        updatingRequestDTO.setCategoryId(2L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(baseProduct));
        mockMapperUpdate(updatingRequestDTO);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(baseProduct)).thenReturn(baseProduct);

        baseProductResponseDTO.setName("Washing Machine");
        baseProductResponseDTO.setDescription("Automatic washing machine");
        baseProductResponseDTO.setCategory(newCategoryNestedDTO);
        when(productMapper.toResponse(baseProduct)).thenReturn(baseProductResponseDTO);

        ProductResponseDTO result = productService.update(1L, updatingRequestDTO);

        assertEquals("Washing Machine", result.getName());
        assertEquals("Automatic washing machine", result.getDescription());
        assertEquals(2L, result.getCategory().getId());
        assertEquals("Home Appliances", result.getCategory().getName());

        verify(categoryRepository).findById(2L);
        verify(productRepository).save(baseProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.update(1L, baseProductRequestDTO));

        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProductWithNonExistingCategory() {
        ProductRequestDTO updatingRequestDTO = new ProductRequestDTO();
        updatingRequestDTO.setName("TV Android 4K");
        updatingRequestDTO.setDescription("Smart TV with Android TV OS");
        updatingRequestDTO.setCategoryId(5L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(baseProduct));
        when(categoryRepository.findById(5L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.update(1L, updatingRequestDTO));

        assertEquals("Category not found", ex.getMessage());
    }

    private void mockMapperUpdate(ProductRequestDTO updatingRequestDTO) {
        doAnswer(invocation -> {
            ProductRequestDTO dto = invocation.getArgument(0);
            Product entity = invocation.getArgument(1);
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            return null;
        }).when(productMapper).updateEntityFromDto(updatingRequestDTO, baseProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingProductWithDuplicateName() {
        // Arrange: Update product with an existing product name
        ProductRequestDTO updatingRequestDTO = new ProductRequestDTO();
        updatingRequestDTO.setName("Mouse");

        Product existingProductWithSameName = new Product();
        existingProductWithSameName.setId(2L);
        existingProductWithSameName.setName("Mouse");

        when(productRepository.findById(1L)).thenReturn(Optional.of(baseProduct));
        when(productRepository.findByName("Mouse")).thenReturn(Optional.of(existingProductWithSameName));

        // Act & Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> productService.update(1L, updatingRequestDTO));

        assertEquals("A product with the name 'Mouse' already exists.", ex.getMessage());
        verify(productRepository, never()).save(any(Product.class));
    }

    // ==== DELETE ====
    @Test
    void shouldDeleteProductWhenIdExists() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(baseProduct));

        // Act & Assert
        productService.delete(1L);

        verify(productRepository, times(1)).delete(baseProduct);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingProduct() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.delete(1L));

        assertEquals("Product not found", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingProductWithAssociatedSales() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(baseProduct));
        SaleDetail saleDetail = new SaleDetail();
        saleDetail.setId(5L);
        saleDetail.setProduct(baseProduct);
        baseProduct.setSaleDetails(List.of(saleDetail));

        // Act & Assert
        ResourceInUseException ex = assertThrows(ResourceInUseException.class, () ->
                productService.delete(1L));

        assertEquals("Cannot delete product with associated sales", ex.getMessage());
    }

    // ==== FIND BY ID ====
    @Test
    void shouldFindProductByIdWhenIdExists() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(baseProduct));
        when(productMapper.toResponse(baseProduct)).thenReturn(baseProductResponseDTO);

        ProductResponseDTO result = productService.findById(1L);

        assertEquals("Smart TV", result.getName());
        assertEquals("Smart TV 4K", result.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistingProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> productService.findById(1L));

        assertEquals("Product not found", ex.getMessage());
    }

    // ==== FIND ALL ====
    @Test
    void shouldFindAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(baseProduct));
        when(productMapper.toResponseList(List.of(baseProduct))).thenReturn(List.of(baseProductResponseDTO));

        List<ProductResponseDTO> result = productService.findAll();

        assertEquals(1, result.size());
        assertEquals("Smart TV", result.getFirst().getName());
        assertEquals("Smart TV 4K", result.getFirst().getDescription());
    }
}
