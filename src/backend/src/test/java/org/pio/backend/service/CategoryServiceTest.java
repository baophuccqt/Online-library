package org.pio.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pio.backend.dto.request.CategoryAddRequest;
import org.pio.backend.dto.request.CategoryUpdateRequest;
import org.pio.backend.dto.response.CategoryResponse;
import org.pio.backend.entity.Category;
import org.pio.backend.exception.AppException;
import org.pio.backend.exception.ErrorCode;
import org.pio.backend.mapper.BookMapper;
import org.pio.backend.mapper.CategoryMapper;
import org.pio.backend.repository.BookRepository;
import org.pio.backend.repository.CategoryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryRepository categoryRepository;
    @Mock
    CategoryMapper categoryMapper;
    @Mock
    BookRepository bookRepository;
    @Mock
    BookMapper bookMapper;
    @InjectMocks
    CategoryService categoryService;

    Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder().id(1L).name("Programming").build();
    }

    // ---------- getBooksByCategory----------

    @Test
    void getBooksByCategory_notFound_throws() {
        Pageable pageable = PageRequest.of(0, 10);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.getBooksByCategory(99L, pageable)).isInstanceOf(AppException.class).extracting(e -> ((AppException) e).getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

        verify(bookRepository, never()).findAllByCategoriesContaining(any(), any());
    }

    // ---------- addCategory ----------

    @Test
    void addCategory_success_mapsSavesAndReturnsResponse() {
        var req = new CategoryAddRequest();
        req.setName("Programming"); //
        // set theo field DTO của bạn

        when(categoryMapper.toCategory(req)).thenReturn(category);
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(new CategoryResponse());

        categoryService.addCategory(req);

        verify(categoryRepository).save(category);
        verify(categoryMapper).toCategoryResponse(category);
    }

    // ---------- updateCategory ----------

    @Test
    void updateCategory_notFound_throws() {
        var req = new CategoryUpdateRequest();
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoryService.updateCategory(99L, req)).isInstanceOf(AppException.class).extracting(e -> ((AppException) e).getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

        verify(categoryMapper, never()).updateCategory(any(), any());
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_success_callsMapperAndSaves() {
        var req = new CategoryUpdateRequest();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(categoryMapper.toCategoryResponse(category)).thenReturn(new CategoryResponse());

        categoryService.updateCategory(1L, req);

        verify(categoryMapper).updateCategory(category, req);

        verify(categoryRepository).save(category);
    }

    // ---------- deleteCategory ----------

    @Test
    void deleteCategory_notFound_throws() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory(99L)).isInstanceOf(AppException.class).extracting(e -> ((AppException) e).getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND);

        verify(categoryRepository, never()).deleteById(any());
    }

    @Test
    void deleteCategory_success_callsDeleteById() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.deleteCategory(1L);

        verify(categoryRepository).deleteById(1L);
    }
}