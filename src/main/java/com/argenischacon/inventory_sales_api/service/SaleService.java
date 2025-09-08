package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.SaleRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleResponseDTO;

import java.util.List;

public interface SaleService {
    SaleResponseDTO create(SaleRequestDTO dto);
    SaleResponseDTO update(Long id, SaleRequestDTO dto);
    void delete(Long id);
    SaleResponseDTO findById(Long id);
    List<SaleResponseDTO> findAll();
}
