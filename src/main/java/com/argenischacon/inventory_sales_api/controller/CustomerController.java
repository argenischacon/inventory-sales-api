package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(@Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(customerService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody CustomerRequestDTO dto) {
        return ResponseEntity.ok(customerService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping ("/{id}")
    public ResponseEntity<CustomerResponseDTO> findById(@PathVariable Long id){
        return ResponseEntity.ok(customerService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> findAll(){
        return ResponseEntity.ok(customerService.findAll());
    }
}
