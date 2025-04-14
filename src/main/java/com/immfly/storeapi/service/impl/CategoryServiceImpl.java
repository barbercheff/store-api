package com.immfly.storeapi.service.impl;

import com.immfly.storeapi.dto.CategoryDTO;
import com.immfly.storeapi.exception.CategoryAlreadyExistsException;
import com.immfly.storeapi.exception.CategoryDeletionException;
import com.immfly.storeapi.exception.InvalidCategoryHierarchyException;
import com.immfly.storeapi.exception.ResourceNotFoundException;
import com.immfly.storeapi.mapper.CategoryMapper;
import com.immfly.storeapi.model.Category;
import com.immfly.storeapi.repository.CategoryRepository;
import com.immfly.storeapi.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(CategoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByName(categoryDTO.getName())) {
            throw new CategoryAlreadyExistsException("Category with name '" + categoryDTO.getName() + "' already exists");
        }

        Category category = CategoryMapper.toEntity(categoryDTO);

        if (categoryDTO.getParentCategoryId() != null) {
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryDTO.getParentCategoryId()));
            category.setParentCategory(parentCategory);
        } else {
            category.setParentCategory(null);
        }

        Category savedCategory = categoryRepository.save(category);
        return CategoryMapper.toDto(savedCategory);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (categoryDTO.getParentCategoryId() != null) {
            if (id.equals(categoryDTO.getParentCategoryId())) {
                throw new InvalidCategoryHierarchyException("A category cannot be its own parent.");
            }
            Category parentCategory = categoryRepository.findById(categoryDTO.getParentCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent category not found with id: " + categoryDTO.getParentCategoryId()));
            existingCategory.setParentCategory(parentCategory);
        } else {
            existingCategory.setParentCategory(null);
        }

        categoryRepository.findByName(categoryDTO.getName())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new CategoryAlreadyExistsException("Category with name '" + categoryDTO.getName() + "' already exists");
                });

        existingCategory.setName(categoryDTO.getName());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return CategoryMapper.toDto(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getSubCategories().isEmpty()) {
            throw new CategoryDeletionException("Cannot delete a category that has subcategories.");
        }

        if (!category.getProducts().isEmpty()) {
            throw new CategoryDeletionException("Cannot delete a category that has products.");
        }

        categoryRepository.delete(category);
    }
}
