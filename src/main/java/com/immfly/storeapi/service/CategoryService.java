package com.immfly.storeapi.service;

import com.immfly.storeapi.dto.CategoryDTO;

import java.util.List;

public interface CategoryService {
    CategoryDTO getCategoryById(Long id);
    CategoryDTO createCategory(CategoryDTO category);
    List<CategoryDTO> getAllCategories();
    CategoryDTO updateCategory(Long id, CategoryDTO category);
    void deleteCategory(Long id);
}
