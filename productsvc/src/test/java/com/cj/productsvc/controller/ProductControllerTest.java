package com.cj.productsvc.controller;

import com.cj.productsvc.exception.ProductNotFoundException;
import com.cj.productsvc.model.Product;

import com.cj.productsvc.service.ProductService;
//import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;


import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
//@Import(ProductControllerTestConfig.class)
public class ProductControllerTest {
    @Autowired
    private MockMvc mockMvc;

    //@MockitoBean
    @MockitoBean
    ProductService productService;

/*    @MockitoBean // Mock the ProductRepository interface
    ProductRepository productRepository; // Use the interface type

    @MockitoBean // Mock the WarrantyRepository class directly
    WarrantyRepository warrantyRepository;*/

 /*   @Autowired
    private ObjectMapper objectMapper;*/
    // Common test data, reused across tests
    Product p1 = Product.builder()
            .id(1L)
            .name("Product 1")
            .brand("Brand A")
            .price(BigDecimal.valueOf(1000))
            .createdBy("admin")
            .build();
    Product p2 = Product.builder()
            .id(1L)
            .name("Product 2")
            .brand("Brand B")
            .price(BigDecimal.valueOf(1200))
            .createdBy("admin")
            .build();
    private final List<Product> productList = List.of(p1, p2);
    @Test
    void shouldReturnAllProducts() throws Exception{
        when(productService.findAll()).thenReturn(productList);

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Product 1"))
                .andExpect(jsonPath("$[1].brand").value("Brand B"));

        verify(productService, times(1)).findAll();

    }

    @Test
    void shouldReturnOneProduct() throws Exception{
when(productService.findById(1L)).thenReturn(p1);

mockMvc.perform(get("/api/products/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.name").value("Product 1"));
        verify(productService, times(1)).findById(1L);
    }

    @Test
    void shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        when(productService.findById(99L)).thenThrow(new ProductNotFoundException("product not found with id: 99L"));

        mockMvc.perform(get("/api/products/{id}",99L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("product not found with id: 99L"));
    }
    @Test
    void shouldCreateProductSuccessfully() throws Exception {
        // Arrange
        Product p3 = Product.builder()
                .id(null)
                .name("Product 3")
                .brand("Brand c")
                .price(BigDecimal.valueOf(1100))
                .stockQuantity(10)
                .createdBy("admin")
                .build();

        Product savedProduct = Product.builder()
                .id(3L)
                .name("Product 3")
                .brand("Brand c")
                .price(BigDecimal.valueOf(1100))
                .stockQuantity(10)
                .createdBy("admin")
                .build();
        when(productService.save(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products/save")
                .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(p3)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath(".name").value("Product 3"))
                .andExpect(jsonPath(".price").value(1100))
                .andExpect(jsonPath(".stockQuantity").value(10))
                .andExpect(jsonPath(".createdBy").value("admin"));
    }

    @Test
    void shouldUpdateProductSuccessfully() throws Exception{
        Product updatedP1 = Product.builder()
                .id(1L)
                .name("Product 1")
                .brand("Brand A")
                .stockQuantity(5)
                .price(BigDecimal.valueOf(1000))
                .createdBy("admin")
                .updatedBy("admin")
                .build();
        when(productService.update(any(Product.class))).thenReturn(updatedP1);

        mockMvc.perform(put("/api/products/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(updatedP1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Product 1"))
                .andExpect(jsonPath("$.stockQuantity").value(5))
                .andExpect(jsonPath("$.updatedBy").value("admin"));

    }
    @Test
    void shouldDeleteProductSuccessfully() throws Exception{
       doNothing().when(productService).delete(3L);

        mockMvc.perform(delete("/api/products/3"))
                .andExpect(status().isOk());

        verify(productService, times(1)).delete(3L);
    }


    @Test
    void shouldSaveMultipleProductsSuccessfully() throws Exception {
        when(productService.saveAll(anyList())).thenReturn(2);

        mockMvc.perform(post("/api/products/saveall")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(productList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Batch insert completed"))
                .andExpect(jsonPath("$.recordsInserted").value(2));

        verify(productService, times(1)).saveAll(anyList());
    }
}
