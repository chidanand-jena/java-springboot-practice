package com.cj.productsvc.service;

import com.cj.productsvc.exception.ProductNotFoundException;
import com.cj.productsvc.exception.WarrantyNotFoundException;
import com.cj.productsvc.model.Product;
import com.cj.productsvc.repo.ProductRepositoryImpl;
import com.cj.productsvc.repo.WarrantyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService{
    private final ProductRepositoryImpl productRepo;
    private final WarrantyRepository warrantyRepo;

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
   /*     try {
            return productRepo.findById(id);
        } catch (EmptyResultDataAccessException ex) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        } catch (Exception ex) {
            // Log error if needed
            throw new ServiceException("Error while retrieving product with id: " + id, ex);
        }*/

    }


    @Override
    public Product createProduct(Product product) {
        if (product.getWarrantyId() != null && warrantyRepo.findById(product.getWarrantyId()).isEmpty()) {
            throw new  WarrantyNotFoundException("Warranty not found with id: " + product.getWarrantyId());
        }
Long productId = productRepo.save(product);
        return productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Failed to save product"));
    }

    //if we want to return created product


    @Override
    public Product updateProduct( Product product) {
        // kafkaProducer.sendProductUpdateEvent(existing.get(), product); // handle update(move old record to archive table) asynchronously via Send Kafka message
        getProductOrThrow(product.getId());


        // Optional: Validate warrantyId before updating, if needed
        if (product.getWarrantyId() != null && warrantyRepo.findById(product.getWarrantyId()).isEmpty()) {
            throw new WarrantyNotFoundException("Warranty not found with id: " + product.getWarrantyId());
        }

        // Proceed with update
        return productRepo.update(product)
                .orElseThrow(() -> new RuntimeException("Failed to update product"));

    }

    @Override
    public boolean deleteProduct(Long id) {
        // kafkaProducer.sendProductDeleteEvent(existing.get(), product); // handle soft delete asynchronously via Send Kafka message
        getProductOrThrow(id);
        int result= productRepo.deleteById(id);
         return result==1;
    }
    @Transactional
    @Override
    public int saveAll(List<Product> products) {
        log.info("In service-saveall");
        try {
            int[][] batchResults = productRepo.saveAll(products);
            return Arrays.stream(batchResults).flatMapToInt(Arrays::stream).sum();
        } catch (DataIntegrityViolationException ex) {
        Throwable rootCause = ex.getRootCause();
        String dbMessage = rootCause != null ? rootCause.getMessage() : ex.getMessage();

        if (dbMessage != null && dbMessage.contains("products_ibfk_1")) {
            // Try to identify the product causing the error if possible
            // Note: batch insert makes this tricky; you may fallback to inserting one-by-one
            throw new DataIntegrityViolationException("Foreign key constraint violation: "+dbMessage);
        }
            throw ex;
        }catch (Exception ex) {
            // handle other unexpected exceptions if needed, or rethrow
            throw new RuntimeException("Unexpected error during batch insert: " + ex.getMessage(), ex);
        }
    }



    private Product getProductOrThrow(Long id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
    }

}
