package com.immfly.storeapi.service;

import com.immfly.storeapi.dto.ProductDTO;

import java.util.List;

public interface ProductService {
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO product);
    List<ProductDTO> getAllProducts();
    ProductDTO updateProduct(Long id, ProductDTO product);
    void deleteProduct(Long id);
}
