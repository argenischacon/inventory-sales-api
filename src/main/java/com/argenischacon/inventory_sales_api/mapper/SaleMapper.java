package com.argenischacon.inventory_sales_api.mapper;

import com.argenischacon.inventory_sales_api.dto.SaleRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleResponseDTO;
import com.argenischacon.inventory_sales_api.model.Sale;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, SaleDetailMapper.class})
public interface SaleMapper {
    // Entity -> ResponseDTO
    @Mapping(target = "total", expression = "java(sale.getTotal())")
    SaleResponseDTO toResponse(Sale sale);

    // List<Entity> -> List<ResponseDTO>
    List<SaleResponseDTO> toResponseList(List<Sale> sales);

    // RequestDTO -> Entity
    Sale toEntity(SaleRequestDTO saleRequestDTO);

    // Update Entity from RequestDTO
    void updateEntityFromDto(SaleRequestDTO dto, @MappingTarget Sale sale);
}
