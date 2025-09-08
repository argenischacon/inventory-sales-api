package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.ProductRequestDTO;
import com.argenischacon.inventory_sales_api.dto.ProductResponseDTO;

import java.util.List;

public interface ProductService {
    ProductResponseDTO create(ProductRequestDTO dto);
    ProductResponseDTO update(Long id, ProductRequestDTO dto);
    void delete(Long id);
    ProductResponseDTO findById(Long id);
    List<ProductResponseDTO> findAll();
}
