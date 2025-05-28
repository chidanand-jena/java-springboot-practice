package com.cj.productsvc.controller;

import com.cj.productsvc.model.Product;
import com.cj.productsvc.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productSvc;

    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product){
        System.out.println("Received product: "+product);
        Product created = productSvc.createProduct(product);
        System.out.println("Create product: "+created);
        return new ResponseEntity<>(created,HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        return new ResponseEntity<>(productSvc.getProductById(id),HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(productSvc.getAllProducts(),HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product){
        return new ResponseEntity<>(productSvc.updateProduct(product),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean isDeleted = productSvc.deleteProduct(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}
