package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.controller.api.ProductAPI;
import com.argenischacon.inventory_sales_api.dto.ProductRequestDTO;
import com.argenischacon.inventory_sales_api.dto.ProductResponseDTO;
import com.argenischacon.inventory_sales_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductAPI {

    private final ProductService productService;

    @Override
    public ResponseEntity<ProductResponseDTO> create(ProductRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(dto));
    }

    @Override
    public ResponseEntity<ProductResponseDTO> update(Long id, ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<ProductResponseDTO> findById(Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @Override
    public ResponseEntity<List<ProductResponseDTO>> findAll() {
        return ResponseEntity.ok(productService.findAll());
    }
}
