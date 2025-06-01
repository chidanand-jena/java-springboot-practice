package com.cj.productsvc.service;

import com.cj.productsvc.exception.ProductNotFoundException;
import com.cj.productsvc.exception.WarrantyNotFoundException;
import com.cj.productsvc.model.Product;
import com.cj.productsvc.model.WarrantyInfo;
import com.cj.productsvc.repo.ProductRepository;
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
    private final ProductRepository productRepo;
    //private final WarrantyRepository warrantyRepo;

    private final WarrantyCacheService warrantyCacheService;

    @Override
    public List<Product> findAll() {
        return productRepo.findAll();
    }

    @Override
    public Product findById(Long id) {
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
    public Product save(Product product) {
        if (product.getWarrantyId() != null && !warrantyCacheService.isWarrantyExists(product.getWarrantyId())) {
            throw new  WarrantyNotFoundException("Warranty not found with id: " + product.getWarrantyId());
        }
Long productId = productRepo.save(product);
        return productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Failed to save product"));
    }

    //if we want to return created product


    @Override
    public Product update(Product product) {
        // kafkaProducer.sendProductUpdateEvent(existing.get(), product); // handle update(move old record to archive table) asynchronously via Send Kafka message
        getProductOrThrow(product.getId());


        // Optional: Validate warrantyId before updating, if needed
        if (product.getWarrantyId() != null && !warrantyCacheService.isWarrantyExists(product.getWarrantyId())) {
            throw new WarrantyNotFoundException("Warranty not found with id: " + product.getWarrantyId());
        }

        // Proceed with update
        return productRepo.update(product)
                .orElseThrow(() -> new RuntimeException("Failed to update product"));

    }

    @Override
    public void delete(Long id) {
        // kafkaProducer.sendProductDeleteEvent(existing.get(), product); // handle soft delete asynchronously via Send Kafka message
        getProductOrThrow(id);
        int result= productRepo.deleteById(id);
        if (result != 1) {
            throw new RuntimeException("Failed to delete product with id: " + id);
        }

    }
    @Transactional
    @Override
    public int saveAll(List<Product> products) {
        log.info("In service-saveall");

        // Step 1: Pre-check warranty IDs via Redis
        List<Product> invalidWarrantyProducts = products.stream()
                .filter(p -> p.getWarrantyId() != null &&
                        !warrantyCacheService.isWarrantyExists(p.getWarrantyId()))
                .toList();
        // Step 2: If any invalid, log and evict them from cache
        if (!invalidWarrantyProducts.isEmpty()) {
            invalidWarrantyProducts.forEach(p -> {
                log.warn("Evicting and skipping Product with invalid warranty ID {}: Product ID = {}",
                        p.getWarrantyId(), p.getId());
                warrantyCacheService.evictWarranty(p.getWarrantyId());
            });
            throw new WarrantyNotFoundException("Some products have invalid warranty IDs: " +
                    invalidWarrantyProducts.stream()
                            .map(p -> "ProductName=" + p.getName() + ", WarrantyID=" + p.getWarrantyId())
                            .toList());
        }
        try {
            int[][] batchResults = productRepo.saveAll(products);
            return Arrays.stream(batchResults).flatMapToInt(Arrays::stream).sum();

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
