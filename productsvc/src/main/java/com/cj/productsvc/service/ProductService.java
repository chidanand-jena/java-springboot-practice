package com.cj.productsvc.service;

import com.cj.productsvc.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAll();
    Product findById(Long id);
    Product save(Product product);
    Product update(Product product);
    void delete(Long id);

    int saveAll(List<Product> products);

}
