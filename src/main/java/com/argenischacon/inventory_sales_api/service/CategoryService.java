package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;

import java.util.List;

public interface CategoryService {
    CategoryResponseDTO create(CategoryRequestDTO dto);
    CategoryResponseDTO update(Long id, CategoryRequestDTO dto);
    void delete(Long id);
    CategoryResponseDTO findById(Long id);
    List<CategoryResponseDTO> findAll();
}
