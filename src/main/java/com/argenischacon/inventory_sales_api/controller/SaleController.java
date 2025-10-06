package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.controller.api.SaleAPI;
import com.argenischacon.inventory_sales_api.dto.SaleRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleResponseDTO;
import com.argenischacon.inventory_sales_api.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SaleController implements SaleAPI {

    private final SaleService saleService;

    @Override
    public ResponseEntity<SaleResponseDTO> create(SaleRequestDTO dto){
        return ResponseEntity.status(HttpStatus.CREATED).
                body(saleService.create(dto));
    }

    @Override
    public ResponseEntity<SaleResponseDTO> update(Long id, SaleRequestDTO dto){
        return ResponseEntity.ok(saleService.update(id, dto));
    }

    @Override
    public ResponseEntity<Void> delete(Long id){
        saleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SaleResponseDTO> findById(Long id){
        return ResponseEntity.ok(saleService.findById(id));
    }

    @Override
    public ResponseEntity<List<SaleResponseDTO>> findAll(){
        return ResponseEntity.ok(saleService.findAll());
    }
}
