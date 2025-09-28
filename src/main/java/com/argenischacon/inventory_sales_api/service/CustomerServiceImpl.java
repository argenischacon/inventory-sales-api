package com.argenischacon.inventory_sales_api.service;

import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.exception.ResourceInUseException;
import com.argenischacon.inventory_sales_api.exception.ResourceNotFoundException;
import com.argenischacon.inventory_sales_api.mapper.CustomerMapper;
import com.argenischacon.inventory_sales_api.model.Customer;
import com.argenischacon.inventory_sales_api.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
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
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customerMapper.updateEntityFromDto(dto, entity);
        return customerMapper.toResponse(customerRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!customer.getSales().isEmpty()) {
            throw new ResourceInUseException("Cannot delete customer with associated sales");
        }

        customerRepository.delete(customer);
    }

    @Override
    public CustomerResponseDTO findById(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return customerMapper.toResponse(entity);
    }

    @Override
    public List<CustomerResponseDTO> findAll() {
        return customerMapper.toResponseList(customerRepository.findAll());
    }
}
