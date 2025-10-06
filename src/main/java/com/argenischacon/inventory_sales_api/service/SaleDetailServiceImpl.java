package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.SaleDetailMapper;
import com.argenischacon.inventory_sales_api.repository.SaleDetailRepository;
import com.argenischacon.inventory_sales_api.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleDetailServiceImpl implements SaleDetailService{
    private final SaleDetailRepository saleDetailRepository;
    private final SaleDetailMapper saleDetailMapper;
    private final SaleRepository saleRepository;

    @Override
    public List<SaleDetailResponseDTO> findBySaleId(Long saleId) {
        if(!saleRepository.existsById(saleId)){
            throw new ResourceNotFoundException("Sale with id " + saleId + " not found.");
        }
        return saleDetailMapper.toResponseList(saleDetailRepository.findBySaleId(saleId));
    }
}
