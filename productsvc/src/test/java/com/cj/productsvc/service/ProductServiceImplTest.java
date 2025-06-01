package com.cj.productsvc.service;

import com.cj.productsvc.exception.ProductNotFoundException;
import com.cj.productsvc.exception.WarrantyNotFoundException;
import com.cj.productsvc.model.Product;
import com.cj.productsvc.model.WarrantyInfo;
import com.cj.productsvc.repo.ProductRepository;
import com.cj.productsvc.repo.WarrantyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
    @Mock
    ProductRepository productRepo;

    @Mock
    WarrantyRepository warrantyRepo;

    @InjectMocks // Injects the mocks into ProductServiceImpl
    private ProductServiceImpl productService;
    private Product p1;
    private Product p2;
    private WarrantyInfo w1;
     List<Product> productList;
    @BeforeEach
    void setUp(){
        // Initialize common test data before each test
          w1 = WarrantyInfo.builder().id(3L)
                         .warrantyType("Manufacturer")
                 .description("1-year warranty provided by the manufacturer, covering all defects in material and workmanship.").build();
         p1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .brand("Brand A")
                .price(BigDecimal.valueOf(1000))
                .createdBy("admin")
                .build();
         p2 = Product.builder()
                .id(1L)
                .name("Product 2")
                .brand("Brand B")
                .price(BigDecimal.valueOf(1200))
                .createdBy("admin")
                .build();
        productList = List.of(p1, p2);
    }

    @Test
    @DisplayName("Should return all products when findAll is called")
    void shouldReturnAllProductsWhenFindAllIsCalled() {
        when(productRepo.findAll()).thenReturn(productList);
List<Product> result = productService.findAll();
        assertNotNull(result);
        assertEquals(2,result.size());
        assertEquals(p1.getName(),result.get(0).getName());
        assertEquals(p2.getName(),result.get(1).getName());
        assertEquals(p2.getBrand(),result.get(1).getBrand());
        verify(productRepo, times(1)).findAll();
    }
    @Test
    @DisplayName("Should return an empty list when no products are found")
    void shouldReturnEmptyListWhenNoProductsAreFound() {
        when(productRepo.findAll()).thenReturn(Collections.emptyList());

        List<Product> result = productService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return product when findById is called with existing ID")
    void shouldReturnProductWhenFindByIdIsCalledWithExistingId() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(p1));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Product 1", result.getName());
        verify(productRepo, times(1)).findById(1L);
    }
    @Test
    @DisplayName("Should throw ProductNotFoundException when findById is called with non-existing ID")
    void shouldThrowProductNotFoundExceptionWhenFindByIdWithNonExistingId() {
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.findById(99L));

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepo, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Should save product successfully with existing warranty")
    void shouldSaveProductSuccessfullyWithExistingWarranty() {
        Product p3 = Product.builder()
                //.id(5L)
                .name("Product 5")
                .brand("Brand E")
                .price(BigDecimal.valueOf(999))
                .warrantyId(3L)
                .stockQuantity(100)
                .createdBy("admin")
                .build();

        // Simulate that save returns a new product ID, and findById then retrieves the full product
        when(warrantyRepo.findById(3L)).thenReturn(Optional.of(w1));
        when(productRepo.save(any(Product.class))).thenReturn(5L); // Simulate ID assigned
        when(productRepo.findById(5L)).thenReturn(Optional.of(
                Product.builder()
                        .id(5L)
                        .name("Product 5")
                        .description("Brand E")
                        .price(BigDecimal.valueOf(999))
                        .warrantyId(3L)
                        .stockQuantity(100)
                        .build()
        ));

        Product savedProduct = productService.save(p3);
        assertNotNull(savedProduct);
        assertEquals(5L, savedProduct.getId());
        assertEquals("Product 5", savedProduct.getName());
        verify(warrantyRepo, times(1)).findById(3L);
        verify(productRepo, times(1)).save(any(Product.class));
        verify(productRepo, times(1)).findById(5L);

    }

    @Test
    @DisplayName("Should save product successfully without warranty")
    void shouldSaveProductSuccessfullyWithoutWarranty() {
        Product newProduct = Product.builder()
                .name("Monitor")
                .description("4K Monitor")
                .price(BigDecimal.valueOf(12000))
                .warrantyId(null) // No warranty
                .build();

        when(productRepo.save(any(Product.class))).thenReturn(4L);
        when(productRepo.findById(4L)).thenReturn(Optional.of(
                Product.builder()
                        .id(4L)
                        .name("Monitor")
                        .description("4K Monitor")
                        .price(BigDecimal.valueOf(12000))
                        .warrantyId(null)
                        .build()
        ));

        Product savedProduct = productService.save(newProduct);

        assertNotNull(savedProduct);
        assertEquals(4L, savedProduct.getId());
        assertEquals("Monitor", savedProduct.getName());
        verify(warrantyRepo, never()).findById(anyLong()); // Should not check for warranty
        verify(productRepo, times(1)).save(any(Product.class));
        verify(productRepo, times(1)).findById(4L);
    }
    @Test
    @DisplayName("Should throw WarrantyNotFoundException when saving product with non-existing warranty ID")
    void shouldThrowWarrantyNotFoundExceptionWhenSavingWithNonExistingWarranty() {
        Product newProduct = Product.builder()
                .name("Webcam")
                .description("HD Webcam")
                .price(BigDecimal.valueOf(50.00))
                .warrantyId(999L) // Non-existing warranty
                .build();

        when(warrantyRepo.findById(999L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(WarrantyNotFoundException.class, () -> productService.save(newProduct));

        assertEquals("Warranty not found with id: 999", exception.getMessage());
        verify(warrantyRepo, times(1)).findById(999L);
        verify(productRepo, never()).save(any(Product.class)); // Should not proceed to save
        verify(productRepo, never()).findById(anyLong());
    }
    @Test
    @DisplayName("Should throw RuntimeException if product save succeeds but findById fails")
    void shouldThrowRuntimeExceptionIfSaveSucceedsButFindByIdFails() {
        Product newProduct = Product.builder()
                .name("Headphones")
                .description("Noise cancelling")
                .price(BigDecimal.valueOf(150.00))
                .warrantyId(null)
                .build();

        when(productRepo.save(any(Product.class))).thenReturn(6L);
        when(productRepo.findById(6L)).thenReturn(Optional.empty()); // Simulate findById failing

        Exception exception = assertThrows(RuntimeException.class, () -> productService.save(newProduct));

        assertEquals("Failed to save product", exception.getMessage());
        verify(productRepo, times(1)).save(any(Product.class));
        verify(productRepo, times(1)).findById(6L);
    }
    @Test
    @DisplayName("Should update product successfully with existing warranty")
    void shouldUpdateProductSuccessfullyWithExistingWarranty() {
        Product updatedProduct = Product.builder()
                .id(1L)
                .name("Product 1")
                .brand("Brand A")
                .price(BigDecimal.valueOf(2000))
                .warrantyId(3L)
                .createdBy("admin")
                .build();

        when(productRepo.findById(1L)).thenReturn(Optional.of(p1)); // Product exists
        when(warrantyRepo.findById(3L)).thenReturn(Optional.of(w1)); // Warranty exists
        when(productRepo.update(any(Product.class))).thenReturn(Optional.of(updatedProduct));

        Product result = productService.update(updatedProduct);

        assertNotNull(result);
        assertEquals("Product 1", result.getName());
        assertEquals(BigDecimal.valueOf(2000), result.getPrice());
        verify(productRepo, times(1)).findById(1L);
        verify(warrantyRepo, times(1)).findById(3L);
        verify(productRepo, times(1)).update(any(Product.class));
    }
    @Test
    @DisplayName("Should delete product successfully and not throw exception")
    void shouldDeleteProductSuccessfully() {
        // Given
        when(productRepo.findById(1L)).thenReturn(Optional.of(p1)); // Product exists
        when(productRepo.deleteById(1L)).thenReturn(1); // 1 row affected = success

        // When / Then
        assertDoesNotThrow(() -> productService.delete(1L));

        verify(productRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existing product")
    void shouldThrowProductNotFoundExceptionWhenDeletingNonExistingProduct() {
        when(productRepo.findById(99L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ProductNotFoundException.class, () -> productService.delete(99L));

        assertEquals("Product not found with id: 99", exception.getMessage());
        verify(productRepo, times(1)).findById(99L);
        verify(productRepo, never()).deleteById(anyLong()); // Delete should not be called
    }

    @Test
    @DisplayName("Should return false if deletion affects no rows")
    void shouldReturnFalseIfDeletionAffectsNoRows() {
        when(productRepo.findById(1L)).thenReturn(Optional.of(p1));
        when(productRepo.deleteById(1L)).thenReturn(0); // 0 rows affected

        //boolean result = productService.delete(1L);

        RuntimeException exception = assertThrows(RuntimeException.class,()->productService.delete(1L));
        assertEquals("Failed to delete product with id: 1", exception.getMessage());
        //assertFalse(result);
        verify(productRepo, times(1)).findById(1L);
        verify(productRepo, times(1)).deleteById(1L);
    }
    @Test
    @DisplayName("Should save all products and return total rows inserted")
    void shouldSaveAllProductsSuccessfully() {
        //List<Product> products = List.of(p1, p2); // Assume p1 and p2 are defined

        int[][] batchResult = new int[][]{{1}, {1}}; // Each row inserted successfully
        when(productRepo.saveAll(productList)).thenReturn(batchResult);

        int result = productService.saveAll(productList);

        assertEquals(2, result);
        verify(productRepo, times(1)).saveAll(productList);
    }

    @Test
    @DisplayName("Should throw DataIntegrityViolationException with FK violation message")
    void shouldThrowForeignKeyConstraintViolation() {
        //List<Product> products = List.of(p1, p2);

        SQLException sqlEx = new SQLException("Cannot add or update a child row: a foreign key constraint fails (`products_ibfk_1`)");
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Foreign key issue", sqlEx);

        when(productRepo.saveAll(productList)).thenThrow(exception);

        DataIntegrityViolationException ex = assertThrows(DataIntegrityViolationException.class,
                () -> productService.saveAll(productList));

        assertTrue(ex.getMessage().contains("Foreign key constraint violation"));
        verify(productRepo, times(1)).saveAll(productList);
    }

    @Test
    @DisplayName("Should throw RuntimeException for unknown error")
    void shouldThrowRuntimeExceptionOnUnexpectedError() {
       // List<Product> products = List.of(p1, p2);

        when(productRepo.saveAll(productList)).thenThrow(new RuntimeException("DB Timeout"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> productService.saveAll(productList));

        assertTrue(ex.getMessage().contains("Unexpected error during batch insert"));
        verify(productRepo, times(1)).saveAll(productList);
    }

}
