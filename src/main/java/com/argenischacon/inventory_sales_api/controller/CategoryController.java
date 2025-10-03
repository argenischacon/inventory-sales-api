package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.controller.api.CategoryAPI;
import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;
import com.argenischacon.inventory_sales_api.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CategoryController implements CategoryAPI {

    private final CategoryService categoryService;

    @Override
    public ResponseEntity<CategoryResponseDTO> create(CategoryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(dto));
    }

    @Override
    public ResponseEntity<CategoryResponseDTO> update(Long id, CategoryRequestDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }

    @Override
    public ResponseEntity<Void> delete(Long id){
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CategoryResponseDTO> findById(Long id){
        return ResponseEntity.ok(categoryService.findById(id));
    }

    @Override
    public ResponseEntity<List<CategoryResponseDTO>> findAll(){
        return ResponseEntity.ok(categoryService.findAll());
    }
}
