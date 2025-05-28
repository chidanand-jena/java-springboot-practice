package com.cj.productsvc.service;

import com.cj.productsvc.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> getAllProducts();
    Product getProductById(Long id);
    Product createProduct(Product product);
    Product updateProduct( Product product);
    boolean deleteProduct(Long id);
}
