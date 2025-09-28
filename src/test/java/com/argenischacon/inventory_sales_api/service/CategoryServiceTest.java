package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceInUseException;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.CategoryMapper;
import com.argenischacon.inventory_sales_api.model.Category;
import com.argenischacon.inventory_sales_api.model.Product;
import com.argenischacon.inventory_sales_api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    CategoryServiceImpl categoryService;

    private CategoryRequestDTO baseRequestDTO;
    private CategoryResponseDTO baseResponseDTO;
    private Category baseCategory;

    @BeforeEach
    void setUp() {
        baseRequestDTO = new CategoryRequestDTO();
        baseRequestDTO.setName("Electronics");
        baseRequestDTO.setDescription("Electronic devices");

        baseResponseDTO = new CategoryResponseDTO();
        baseResponseDTO.setId(1L);
        baseResponseDTO.setName("Electronics");
        baseResponseDTO.setDescription("Electronic devices");

        baseCategory = new Category();
        baseCategory.setId(1L);
        baseCategory.setName("Electronics");
        baseCategory.setDescription("Electronic devices");
    }

    // ===== CREATE =====
    @Test
    void shouldCreateCategoryWhenDataisValid() {
        when(categoryMapper.toEntity(baseRequestDTO)).thenReturn(baseCategory);
        when(categoryRepository.save(baseCategory)).thenReturn(baseCategory);
        when(categoryMapper.toResponse(baseCategory)).thenReturn(baseResponseDTO);

        CategoryResponseDTO result = categoryService.create(baseRequestDTO);

        assertEquals("Electronics", result.getName());
        assertEquals("Electronic devices", result.getDescription());

        verify(categoryRepository, times(1)).save(baseCategory);
    }


    // ==== UPDATE ====
    @Test
    void shouldUpdateCategoryWhenIdExists() {
        CategoryRequestDTO updatingRequestDTO = new CategoryRequestDTO();
        updatingRequestDTO.setName("Sports");
        updatingRequestDTO.setDescription("Sports equipment");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(baseCategory));

        doAnswer(invocation -> {
            CategoryRequestDTO dto = invocation.getArgument(0);
            Category entity = invocation.getArgument(1);
            entity.setName(dto.getName());
            entity.setDescription(dto.getDescription());
            baseResponseDTO.setName(dto.getName());
            baseResponseDTO.setDescription(dto.getDescription());
            return null;
        }).when(categoryMapper).updateEntityFromDto(updatingRequestDTO, baseCategory);

        when(categoryRepository.save(baseCategory)).thenReturn(baseCategory);
        when(categoryMapper.toResponse(baseCategory)).thenReturn(baseResponseDTO);

        CategoryResponseDTO result = categoryService.update(1L, updatingRequestDTO);

        assertEquals("Sports", result.getName());
        assertEquals("Sports equipment", result.getDescription());

        verify(categoryRepository, times(1)).save(baseCategory);
        verify(categoryMapper, times(1)).updateEntityFromDto(updatingRequestDTO, baseCategory);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.update(1L, new CategoryRequestDTO()));

        assertEquals("Category not found", ex.getMessage());
    }

    // ==== DELETE ====
    @Test
    void shouldDeleteCategoryWhenIdExists() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(baseCategory));
        doNothing().when(categoryRepository).delete(baseCategory);

        // Act & Assert
        categoryService.delete(1L);

        verify(categoryRepository, times(1)).delete(baseCategory);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingCategory() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.delete(1L));

        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenDeletingCategoryWithAssociatedProducts() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(baseCategory));
        Product product = new Product();
        product.setId(5L);
        product.setName("Smart TV");
        baseCategory.setProducts(Set.of(product));

        // Act & Assert
        ResourceInUseException ex = assertThrows(ResourceInUseException.class,
                () -> categoryService.delete(1L));

        assertEquals("Cannot delete category with associated products", ex.getMessage());

    }

    // ==== FIND BY ID ====
    @Test
    void shouldFindCategoryByIdWhenIdExists() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(baseCategory));
        when(categoryMapper.toResponse(baseCategory)).thenReturn(baseResponseDTO);

        CategoryResponseDTO result = categoryService.findById(1L);

        assertEquals("Electronics", result.getName());
        assertEquals("Electronic devices", result.getDescription());
    }

    @Test
    void shouldThrowExceptionWhenFindingNonExistingCategory() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> categoryService.findById(1L));

        assertEquals("Category not found", ex.getMessage());
    }

    // ==== FIND ALL ====
    @Test
    void shouldFindAllCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(baseCategory));
        when(categoryMapper.toResponseList(List.of(baseCategory))).thenReturn(List.of(baseResponseDTO));

        List<CategoryResponseDTO> resultList = categoryService.findAll();

        assertEquals(1, resultList.size());
        assertEquals("Electronics", resultList.getFirst().getName());
        assertEquals("Electronic devices", resultList.getFirst().getDescription());
    }
}
