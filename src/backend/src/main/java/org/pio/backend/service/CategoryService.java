package org.pio.backend.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.pio.backend.dto.request.CategoryAddRequest;
import org.pio.backend.dto.request.CategoryUpdateRequest;
import org.pio.backend.dto.response.BookResponse;
import org.pio.backend.dto.response.CategoryResponse;
import org.pio.backend.entity.Category;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.BookMapper;
import org.pio.backend.mapper.CategoryMapper;
import org.pio.backend.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream().map(category -> categoryMapper.toCategoryResponse(category)).toList();
    }

    @Transactional(readOnly = true)
    public List<BookResponse> getBooksByCategory(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXIST)
        );

        return category.getBooks().stream().map(book -> bookMapper.toBookResponse(book)).toList();
    }

    public CategoryResponse addCategory(CategoryAddRequest request) {
        Category newCategory = categoryMapper.toCategory(request);
        categoryRepository.save(newCategory);
        return categoryMapper.toCategoryResponse(newCategory);
    }

    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category currentCategory = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXIST)
        );

        categoryMapper.updateCategory(currentCategory, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(currentCategory));
    }

    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXIST);
        }
        categoryRepository.deleteById(id);
    }
}
