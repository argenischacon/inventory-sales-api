package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;

import java.util.List;

public interface SaleDetailService {
    List<SaleDetailResponseDTO> findBySaleId(Long saleId);
}
