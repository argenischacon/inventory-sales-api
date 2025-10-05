package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;
import com.argenischacon.inventory_sales_api.exception.DuplicateResourceException;
import com.argenischacon.inventory_sales_api.exception.ResourceInUseException;
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
        if(categoryRepository.existsByName(dto.getName())){
            throw new DuplicateResourceException("A category with the same name already exists.");
        }
        Category category = categoryMapper.toEntity(dto);
        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found."));

        categoryRepository.findByName(dto.getName()).ifPresent(existingCategory -> {
            if(!existingCategory.getId().equals(id)){
                throw new DuplicateResourceException("A category with the same name already exists.");
            }
        });
        categoryMapper.updateEntityFromDto(dto, entity);
        return categoryMapper.toResponse(categoryRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found."));

        if (!category.getProducts().isEmpty()) {
            throw new ResourceInUseException("Cannot delete category: it is associated with existing products.");
        }

        categoryRepository.delete(category);
    }

    @Override
    public CategoryResponseDTO findById(Long id) {
        Category entity = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id " + id + " not found."));
        return categoryMapper.toResponse(entity);
    }

    @Override
    public List<CategoryResponseDTO> findAll() {
        return categoryMapper.toResponseList(categoryRepository.findAll());
    }
}
