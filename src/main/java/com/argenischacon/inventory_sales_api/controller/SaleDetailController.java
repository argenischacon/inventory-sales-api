package com.argenischacon.inventory_sales_api.controller;

import com.argenischacon.inventory_sales_api.controller.api.SaleDetailAPI;
import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;
import com.argenischacon.inventory_sales_api.service.SaleDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SaleDetailController implements SaleDetailAPI {

    private final SaleDetailService saleDetailService;

    @Override
    public ResponseEntity<List<SaleDetailResponseDTO>> findBySaleId(Long saleId) {
        return ResponseEntity.ok(saleDetailService.findBySaleId(saleId));
    }
}
