package com.immfly.storeapi.service;

import com.immfly.storeapi.dto.ProductDTO;
import com.immfly.storeapi.exception.ProductAlreadyExistsException;
import com.immfly.storeapi.exception.ProductDeletionException;
import com.immfly.storeapi.exception.ResourceNotFoundException;
import com.immfly.storeapi.mapper.ProductMapper;
import com.immfly.storeapi.model.Category;
import com.immfly.storeapi.model.Product;
import com.immfly.storeapi.model.ProductOrder;
import com.immfly.storeapi.repository.CategoryRepository;
import com.immfly.storeapi.repository.ProductRepository;
import com.immfly.storeapi.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    private ProductRepository productRepository;
    private CategoryRepository categoryRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        productService = new ProductServiceImpl(productRepository, categoryRepository);
    }

    @Test
    void getProductById_ExistingProduct_ReturnsProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Laptop");
        product.setPrice(BigDecimal.valueOf(1200));
        product.setStock(10);
        Category category = new Category();
        category.setId(1L);
        product.setCategory(category);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Laptop", result.getName());
        assertEquals(BigDecimal.valueOf(1200), result.getPrice());
    }

    @Test
    void getProductById_NotFound_ShouldThrowException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.getProductById(99L));
    }

    @Test
    void getAllProducts_ReturnsListOfProducts() {
        Product p1 = new Product();
        p1.setId(1L);
        p1.setName("Product 1");
        p1.setPrice(BigDecimal.valueOf(10));
        p1.setStock(5);
        p1.setCategory(new Category());

        Product p2 = new Product();
        p2.setId(2L);
        p2.setName("Product 2");
        p2.setPrice(BigDecimal.valueOf(15));
        p2.setStock(2);
        p2.setCategory(new Category());

        when(productRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<ProductDTO> result = productService.getAllProducts();

        assertEquals(2, result.size());
        assertEquals("Product 1", result.get(0).getName());
        assertEquals("Product 2", result.get(1).getName());
    }

    @Test
    void createProduct_SuccessfullySavesAndReturnsProduct() {
        Product product = new Product();
        product.setName("iPhone");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setImageUrl("image_url");
        product.setStock(10);

        Category category = new Category();
        category.setId(1L);
        category.setName("Electronics");

        product.setCategory(category);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("iPhone");
        savedProduct.setPrice(product.getPrice());
        savedProduct.setCategory(category);
        savedProduct.setStock(10);
        savedProduct.setImageUrl("image_url");

        when(productRepository.existsByName("iPhone")).thenReturn(false);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductDTO result = productService.createProduct(ProductMapper.toDto(product));

        assertNotNull(result);
        assertEquals("iPhone", result.getName());
        assertEquals(1L, result.getId());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_NameExists_ShouldThrowException() {
        when(productRepository.existsByName("MacBook")).thenReturn(true);

        ProductDTO dto = new ProductDTO();
        dto.setName("MacBook");
        dto.setPrice(BigDecimal.valueOf(1500));
        dto.setStock(10);
        dto.setImageUrl("img.jpg");
        dto.setCategoryId(1L);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(dto));
        verify(productRepository, never()).save(any());
    }


    @Test
    void createProduct_CategoryNotFound_ShouldThrowException() {
        when(productRepository.existsByName("Tablet")).thenReturn(false);
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        ProductDTO dto = new ProductDTO();
        dto.setName("Tablet");
        dto.setPrice(BigDecimal.valueOf(299));
        dto.setStock(5);
        dto.setImageUrl("tablet.png");
        dto.setCategoryId(99L);

        assertThrows(ResourceNotFoundException.class, () -> productService.createProduct(dto));
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProduct_ValidId_UpdatesProductSuccessfully() {
        Long productId = 1L;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Old Name");
        existingProduct.setPrice(BigDecimal.valueOf(100));
        existingProduct.setStock(5);
        existingProduct.setImageUrl("old.jpg");

        Category category = new Category();
        category.setId(2L);
        category.setName("Tech");

        ProductDTO dto = new ProductDTO();
        dto.setName("New Name");
        dto.setPrice(BigDecimal.valueOf(150));
        dto.setStock(5);
        dto.setImageUrl("new.jpg");
        dto.setCategoryId(2L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByName("New Name")).thenReturn(Optional.empty());
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(productRepository.save(any())).thenReturn(existingProduct);

        ProductDTO result = productService.updateProduct(productId, dto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals(BigDecimal.valueOf(150), result.getPrice());
        assertEquals(5, result.getStock());
        assertEquals("new.jpg", result.getImageUrl());
        assertEquals(2L, result.getCategoryId());
        verify(productRepository).save(existingProduct);
    }

    @Test
    void updateProduct_ProductNotFound_ShouldThrowException() {
        Long productId = 99L;
        ProductDTO dto = new ProductDTO();
        dto.setName("Update");
        dto.setCategoryId(1L);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, dto));
    }

    @Test
    void updateProduct_NameAlreadyExistsForDifferentProduct_ShouldThrowException() {
        Long productId = 1L;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Original");

        Product conflictingProduct = new Product();
        conflictingProduct.setId(2L);

        ProductDTO dto = new ProductDTO();
        dto.setName("Duplicate");

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByName("Duplicate")).thenReturn(Optional.of(conflictingProduct));

        assertThrows(ProductAlreadyExistsException.class, () -> productService.updateProduct(productId, dto));
    }

    @Test
    void updateProduct_CategoryNotFound_ShouldThrowException() {
        Long productId = 1L;

        Product existingProduct = new Product();
        existingProduct.setId(productId);
        existingProduct.setName("Product");

        ProductDTO dto = new ProductDTO();
        dto.setName("Updated Product");
        dto.setPrice(BigDecimal.valueOf(200));
        dto.setImageUrl("updated.jpg");
        dto.setCategoryId(999L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findByName("Updated Product")).thenReturn(Optional.empty());
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.updateProduct(productId, dto));
    }

    @Test
    void deleteProduct_ValidId_DeletesSuccessfully() {
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Auriculares");
        product.setProductOrders(List.of());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.deleteProduct(productId);

        verify(productRepository).findById(productId);
        verify(productRepository).delete(product);
    }

    @Test
    void deleteProduct_ProductNotFound_ShouldThrowException() {
        Long productId = 99L;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> productService.deleteProduct(productId));

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void deleteProduct_WithAssociatedOrders_ShouldThrowException() {
        Long productId = 1L;

        Product product = new Product();
        product.setId(productId);
        product.setName("Tablet");

        ProductOrder po = new ProductOrder();
        product.setProductOrders(List.of(po));

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ProductDeletionException.class, () -> productService.deleteProduct(productId));

        verify(productRepository).findById(productId);
        verify(productRepository, never()).delete(any());
    }
}
