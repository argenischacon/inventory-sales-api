package com.argenischacon.inventory_sales_api.mapper;

import com.argenischacon.inventory_sales_api.dto.ProductNestedDTO;
import com.argenischacon.inventory_sales_api.dto.ProductRequestDTO;
import com.argenischacon.inventory_sales_api.dto.ProductResponseDTO;
import com.argenischacon.inventory_sales_api.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class})
public interface ProductMapper {
    // Entity -> ResponseDTO
    ProductResponseDTO toResponse(Product product);

    // List<Entity> -> List<ResponseDTO>
    List<ProductResponseDTO> toResponseList(List<Product> products);

    // RequestDTO -> Entity
    Product toEntity(ProductRequestDTO productRequestDTO);

    // Update Entity from RequestDTO
    void updateEntityFromDto(ProductRequestDTO dto, @MappingTarget Product product);

    // Entity -> NestedDTO
    ProductNestedDTO toNested(Product product);

    // List<Entity> - List<NestedDTO>
    List<ProductNestedDTO> toNestedList(List<Product> products);
}
