package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.controller.api.CustomerAPI;
import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController implements CustomerAPI {

    private final CustomerService customerService;

    @Override
    public ResponseEntity<CustomerResponseDTO> create(CustomerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.create(dto));
    }

    @Override
    public ResponseEntity<CustomerResponseDTO> update(Long id, CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @Override
    public ResponseEntity<Void> delete(Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CustomerResponseDTO> findById(Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @Override
    public ResponseEntity<List<CustomerResponseDTO>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }
}
