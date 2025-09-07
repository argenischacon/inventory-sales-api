package com.argenischacon.inventory_sales_api.mapper;

import com.argenischacon.inventory_sales_api.dto.CategoryNestedDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryRequestDTO;
import com.argenischacon.inventory_sales_api.dto.CategoryResponseDTO;
import com.argenischacon.inventory_sales_api.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    // Entity -> ResponseDTO
    CategoryResponseDTO toResponse(Category category);

    // List<Entity> -> List<ResponseDTO>
    List<CategoryResponseDTO> toResponseList(List<Category> categories);

    // RequestDTO -> Entity
    Category toEntity(CategoryRequestDTO categoryRequestDTO);

    // Update Entity from RequestDTO
    void updateEntityFromDto(CategoryRequestDTO dto, @MappingTarget Category category);

    //Entity -> NestedDTO
    CategoryNestedDTO toNested(Category category);

    // List<Entity> -> List<NestedDTO>
    List<CategoryNestedDTO> toNestedList(List<Category> categories);
}
