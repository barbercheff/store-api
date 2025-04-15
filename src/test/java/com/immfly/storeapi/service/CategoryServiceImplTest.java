
package com.immfly.storeapi.service;

import com.immfly.storeapi.dto.CategoryDTO;
import com.immfly.storeapi.exception.CategoryAlreadyExistsException;
import com.immfly.storeapi.exception.CategoryDeletionException;
import com.immfly.storeapi.exception.InvalidCategoryHierarchyException;
import com.immfly.storeapi.exception.ResourceNotFoundException;
import com.immfly.storeapi.model.Category;
import com.immfly.storeapi.model.Product;
import com.immfly.storeapi.repository.CategoryRepository;
import com.immfly.storeapi.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    private CategoryRepository categoryRepository;
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryService = new CategoryServiceImpl(categoryRepository);
    }

    @Test
    void getCategoryById_ExistingId_ReturnsCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        CategoryDTO result = categoryService.getCategoryById(1L);

        assertNotNull(result);
        assertEquals("Electronics", result.getName());
    }

    @Test
    void getCategoryById_NotFound_ShouldThrowException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(99L));
    }

    @Test
    void createCategory_UniqueName_SavesSuccessfully() {
        CategoryDTO dto = new CategoryDTO(null, "Books", null);

        when(categoryRepository.existsByName("Books")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> {
            Category saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        CategoryDTO result = categoryService.createCategory(dto);

        assertNotNull(result.getId());
        assertEquals("Books", result.getName());
    }

    @Test
    void createCategory_DuplicateName_ShouldThrowException() {
        CategoryDTO dto = new CategoryDTO(null, "Books", null);
        when(categoryRepository.existsByName("Books")).thenReturn(true);

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.createCategory(dto));
    }

    @Test
    void createCategory_WithValidParent_ShouldSucceed() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Laptops");
        categoryDTO.setParentCategoryId(1L);

        Category parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Electronics");

        Category savedCategory = new Category();
        savedCategory.setId(2L);
        savedCategory.setName("Laptops");
        savedCategory.setParentCategory(parentCategory);

        when(categoryRepository.existsByName("Laptops")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any())).thenReturn(savedCategory);

        CategoryDTO result = categoryService.createCategory(categoryDTO);

        assertNotNull(result);
        assertEquals("Laptops", result.getName());
        assertEquals(1L, result.getParentCategoryId());
    }

    @Test
    void createCategory_WithNonExistentParent_ShouldThrowException() {
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Laptops");
        categoryDTO.setParentCategoryId(99L);

        when(categoryRepository.existsByName("Laptops")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.createCategory(categoryDTO);
        });

        verify(categoryRepository, never()).save(any());
    }



    @Test
    void getAllCategories_ShouldReturnListOfCategories() {
        Category cat1 = new Category();
        cat1.setId(1L);
        cat1.setName("Electronics");

        Category cat2 = new Category();
        cat2.setId(2L);
        cat2.setName("Books");

        when(categoryRepository.findAll()).thenReturn(List.of(cat1, cat2));

        List<CategoryDTO> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Electronics", result.get(0).getName());
        assertEquals("Books", result.get(1).getName());
    }

    @Test
    void deleteCategory_ValidId_ShouldDeleteSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");
        category.setSubCategories(Collections.emptyList());
        category.setProducts(Collections.emptyList());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        categoryService.deleteCategory(1L);

        verify(categoryRepository).delete(category);
    }

    @Test
    void deleteCategory_NotFound_ShouldThrowException() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            categoryService.deleteCategory(99L);
        });

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_WithSubcategories_ShouldThrowException() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Category subCategory = new Category();
        subCategory.setId(2L);
        subCategory.setName("Laptops");

        category.setSubCategories(List.of(subCategory));
        category.setProducts(Collections.emptyList());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(CategoryDeletionException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void deleteCategory_WithProducts_ShouldThrowException() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        Product product = new Product();
        product.setId(1L);
        product.setName("Smartphone");

        category.setProducts(List.of(product));
        category.setSubCategories(Collections.emptyList());

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

        assertThrows(CategoryDeletionException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        verify(categoryRepository, never()).delete(any());
    }

    @Test
    void updateCategory_RemovesParentCategorySuccessfully() {
        Long categoryId = 1L;
        Category existing = new Category();
        existing.setId(categoryId);
        existing.setName("Old");
        existing.setParentCategory(new Category());

        CategoryDTO dto = new CategoryDTO(categoryId, "Updated", null);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        when(categoryRepository.save(any(Category.class))).thenReturn(existing);

        CategoryDTO result = categoryService.updateCategory(categoryId, dto);

        assertEquals("Updated", result.getName());
        assertNull(existing.getParentCategory());
    }

    @Test
    void updateCategory_WithValidParent_SetsParentCorrectly() {
        Long categoryId = 1L;
        Long parentId = 2L;

        Category existing = new Category();
        existing.setId(categoryId);
        existing.setName("Old");

        Category parent = new Category();
        parent.setId(parentId);

        CategoryDTO dto = new CategoryDTO(categoryId, "New Name", parentId);

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(parentId)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(any(Category.class))).thenReturn(existing);

        CategoryDTO result = categoryService.updateCategory(categoryId, dto);

        assertEquals("New Name", result.getName());
        assertEquals(parent, existing.getParentCategory());
    }

    @Test
    void updateCategory_CategoryNotFound_ShouldThrowException() {
        Long id = 1L;
        CategoryDTO dto = new CategoryDTO(id, "New", null);

        when(categoryRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, dto));
    }

    @Test
    void updateCategory_ParentCategoryNotFound_ShouldThrowException() {
        Long id = 1L;
        Long invalidParentId = 99L;

        Category existing = new Category();
        existing.setId(id);
        existing.setName("Old");

        CategoryDTO dto = new CategoryDTO(id, "New", invalidParentId);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findById(invalidParentId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(id, dto));
    }

    @Test
    void updateCategory_selfAsParent_throwsInvalidHierarchyException() {
        Long id = 1L;
        Category existing = new Category();
        existing.setId(id);
        existing.setName("Phones");

        CategoryDTO dto = new CategoryDTO(id, "Phones Updated", id);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));

        assertThrows(InvalidCategoryHierarchyException.class, () -> categoryService.updateCategory(id, dto));
    }

    @Test
    void updateCategory_nameAlreadyUsedByAnother_throwsCategoryAlreadyExistsException() {
        Long id = 1L;

        Category existing = new Category();
        existing.setId(id);
        existing.setName("OldName");

        Category another = new Category();
        another.setId(2L);
        another.setName("NewName");

        CategoryDTO dto = new CategoryDTO(id, "NewName", null);

        when(categoryRepository.findById(id)).thenReturn(Optional.of(existing));
        when(categoryRepository.findByName("NewName")).thenReturn(Optional.of(another));

        assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.updateCategory(id, dto));
    }
}
