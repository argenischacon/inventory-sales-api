package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.CategoryMapper;
import com.argenischacon.inventory_sales_api.model.Category;
import com.argenischacon.inventory_sales_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        categoryMapper.updateEntityFromDto(dto, entity);
        return categoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryMapper.toResponse(entity);
    }

    @Override
    public List<CategoryResponseDTO> findAll() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }
}
