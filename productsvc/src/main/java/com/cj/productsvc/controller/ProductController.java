package com.cj.productsvc.controller;

import com.cj.productsvc.model.Product;
import com.cj.productsvc.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productSvc;

    @PostMapping("/save")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product){
       log.info("Received product: {}",product);
        Product created = productSvc.save(product);
        log.info("Create product:{} ",created);
        return new ResponseEntity<>(created,HttpStatus.CREATED);

    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        return new ResponseEntity<>(productSvc.findById(id),HttpStatus.OK);
    }
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return new ResponseEntity<>(productSvc.findAll(),HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product){
        return new ResponseEntity<>(productSvc.update(product),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productSvc.delete(id);
        return  new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/saveall")
    public ResponseEntity<?> saveMultipleProducts(@RequestBody List<@Valid Product> products, BindingResult result){
        if (result.hasErrors()) {
            List<Map<String, String>> errors = result.getFieldErrors().stream()
                    .map(fieldError -> Map.of(
                            "field", fieldError.getField(),
                            "message", Optional.of(fieldError.toString())
                                    .orElse("Invalid value")
                    ))    .toList();

            Map<String, Object> errorResponse = Map.of(
                    "message", "Validation failed",
                    "errors", errors
            );
            return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);
        }

        int insertedCount = productSvc.saveAll(products);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Batch insert completed");
        response.put("recordsInserted", insertedCount);
        return new ResponseEntity<>(response,HttpStatus.OK);

    }


}
