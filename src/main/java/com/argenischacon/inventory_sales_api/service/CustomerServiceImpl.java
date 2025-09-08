package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.mapper.CustomerMapper;
import com.argenischacon.inventory_sales_api.model.Customer;
import com.argenischacon.inventory_sales_api.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService{
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponseDTO create(CustomerRequestDTO dto) {
        Customer entity = customerMapper.toEntity(dto);
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    @Override
    public CustomerResponseDTO update(Long id, CustomerRequestDTO dto) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        customerMapper.updateEntityFromDto(dto, entity);
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        if(!customerRepository.existsById(id)){
            throw new EntityNotFoundException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerResponseDTO findById(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        return customerMapper.toResponse(entity);
    }

    @Override
    public List<CustomerResponseDTO> findAll() {
        return customerMapper.toResponseList(customerRepository.findAll());
    }
}
