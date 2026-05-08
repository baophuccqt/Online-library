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
import org.pio.backend.repository.BookRepository;
import org.pio.backend.repository.CategoryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Transactional(readOnly = true)
public class CategoryService {
    CategoryRepository categoryRepository;
    CategoryMapper categoryMapper;
    BookRepository bookRepository;
    private final BookMapper bookMapper;

    public Page<CategoryResponse> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(category -> categoryMapper.toCategoryResponse(category));
    }

    public Page<BookResponse> getBooksByCategory(Long id, Pageable pageable) {
        Category category = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)
        );

        return bookRepository.findAllByCategoriesContaining(category, pageable).map(book -> bookMapper.toBookResponse(book));
    }

    @Transactional
    public CategoryResponse addCategory(CategoryAddRequest request) {
        Category newCategory = categoryMapper.toCategory(request);
        categoryRepository.save(newCategory);
        return categoryMapper.toCategoryResponse(newCategory);
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request) {
        Category currentCategory = categoryRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_FOUND)
        );

        categoryMapper.updateCategory(currentCategory, request);
        return categoryMapper.toCategoryResponse(categoryRepository.save(currentCategory));
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new AppException(ErrorCode.CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
    }
}
