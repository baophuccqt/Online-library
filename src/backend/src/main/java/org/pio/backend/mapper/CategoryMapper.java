package org.pio.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.pio.backend.dto.request.CategoryAddRequest;
import org.pio.backend.dto.request.CategoryUpdateRequest;
import org.pio.backend.dto.response.CategoryResponse;
import org.pio.backend.entity.Category;
import org.pio.backend.repository.CategoryRepository;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toCategory(CategoryAddRequest request);

    CategoryResponse toCategoryResponse(Category category);

    void updateCategory(@MappingTarget Category category,  CategoryUpdateRequest request);
}
