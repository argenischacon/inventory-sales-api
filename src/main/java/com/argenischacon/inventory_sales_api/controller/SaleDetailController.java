package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;
import com.argenischacon.inventory_sales_api.service.SaleDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sale-details")
@RequiredArgsConstructor
public class SaleDetailController {

    private final SaleDetailService saleDetailService;

    @GetMapping("/{saleId}")
    public ResponseEntity<List<SaleDetailResponseDTO>> findBySaleId(@PathVariable Long saleId){
        return ResponseEntity.ok(saleDetailService.findBySaleId(saleId));
    }
}
