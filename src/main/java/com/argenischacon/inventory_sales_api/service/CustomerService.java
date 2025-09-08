package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;

import java.util.List;

public interface CustomerService {
    CustomerResponseDTO create(CustomerRequestDTO dto);
    CustomerResponseDTO update(Long id, CustomerRequestDTO dto);
    void delete(Long id);
    CustomerResponseDTO findById(Long id);
    List<CustomerResponseDTO> findAll();
}
