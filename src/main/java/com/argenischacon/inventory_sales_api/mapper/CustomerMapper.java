package com.argenischacon.inventory_sales_api.mapper;

import com.argenischacon.inventory_sales_api.dto.CustomerNestedDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CustomerResponseDTO;
import com.argenischacon.inventory_sales_api.model.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    // Entity -> ResponseDTO
    CustomerResponseDTO toResponse(Customer customer);

    // List<Entity> -> List<ResponseDTO>
    List<CustomerResponseDTO> toResponseList(List<Customer> customers);

    // RequestDTO -> Entity
    Customer toEntity(CustomerRequestDTO customerRequestDTO);

    // Update Entity from RequestDTO
    void updateEntityFromDto(CustomerRequestDTO dto, @MappingTarget Customer customer);

    //Entity -> NestedDTO
    CustomerNestedDTO toNested(Customer customer);

    // List<Entity> -> List<NestedDTO>
    List<CustomerNestedDTO> toNestedList(List<Customer> customers);
}
