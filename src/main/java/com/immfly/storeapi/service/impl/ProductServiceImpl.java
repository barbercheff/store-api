package com.immfly.storeapi.service.impl;

import com.immfly.storeapi.dto.ProductDTO;
import com.immfly.storeapi.exception.ResourceNotFoundException;
import com.immfly.storeapi.mapper.ProductMapper;
import com.immfly.storeapi.model.Category;
import com.immfly.storeapi.model.Product;
import com.immfly.storeapi.repository.CategoryRepository;
import com.immfly.storeapi.repository.ProductRepository;
import com.immfly.storeapi.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }


    @Override
    public ProductDTO getProductById(Long id) {
        return productRepository.findById(id)
                .map(ProductMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = ProductMapper.toEntity(productDTO);

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDTO.getCategoryId()));
            product.setCategory(category);
        } else {
            // TIENE SENTIDO UN PRODUCTO SIN CATEGORÍA???
            product.setCategory(null);
        }

        Product savedProduct = productRepository.save(product);
        return ProductMapper.toDto(savedProduct);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(productDTO.getName());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setImageUrl(productDTO.getImageUrl());

        if (productDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + productDTO.getCategoryId()));
            existingProduct.setCategory(category);
        } else {
            existingProduct.setCategory(null);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return ProductMapper.toDto(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
