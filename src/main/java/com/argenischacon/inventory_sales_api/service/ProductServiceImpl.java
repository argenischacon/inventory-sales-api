package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.ProductRequestDTO;
import com.argenischacon.inventory_sales_api.dto.ProductResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.ProductMapper;
import com.argenischacon.inventory_sales_api.model.Category;
import com.argenischacon.inventory_sales_api.model.Product;
import com.argenischacon.inventory_sales_api.repository.CategoryRepository;
import com.argenischacon.inventory_sales_api.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    @Transactional
    @Override
    public ProductResponseDTO create(ProductRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Product product = productMapper.toEntity(dto);
        product.setCategory(category);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Transactional
    @Override
    public ProductResponseDTO update(Long id, ProductRequestDTO dto) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        productMapper.updateEntityFromDto(dto, entity);

        if (!Objects.equals(dto.getCategoryId(), entity.getCategory().getId())) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
            entity.setCategory(category);
        }
        return productMapper.toResponse(productRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponseDTO findById(Long id) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        return productMapper.toResponse(entity);
    }

    @Override
    public List<ProductResponseDTO> findAll() {
        return productMapper.toResponseList(productRepository.findAll());
    }
}
