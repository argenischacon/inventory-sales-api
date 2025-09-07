package com.argenischacon.inventory_sales_api.mapper;

import com.argenischacon.inventory_sales_api.dto.SaleDetailRequestDTO;
import com.argenischacon.inventory_sales_api.dto.SaleDetailResponseDTO;
import com.argenischacon.inventory_sales_api.model.SaleDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface SaleDetailMapper {
    // Entity -> ResponseDTO
    @Mapping(target = "subTotal", expression = "java(saleDetail.getSubTotal())")
    SaleDetailResponseDTO toResponse(SaleDetail saleDetail);

    // List<Entity> -> List<ResponseDTO>
    List<SaleDetailResponseDTO> toResponseList(List<SaleDetail> saleDetails);

    // RequestDTO -> Entity
    SaleDetail toEntity(SaleDetailRequestDTO saleDetailRequestDTO);

    // Update Entity from RequestDTO
    void updateEntityFromDto(SaleDetailRequestDTO dto, @MappingTarget SaleDetail saleDetail);
}
