package com.cj.productsvc.repo;

import com.cj.productsvc.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> findAll();

    Optional<Product> findById(Long id);

    //if you want to return created product
    Long save(Product product);

    Optional<Product> update(Product product);



    int deleteById(Long id);

}
