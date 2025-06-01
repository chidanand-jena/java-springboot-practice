package com.cj.productsvc.controller;

import com.cj.productsvc.repo.ProductRepositoryImpl;
import com.cj.productsvc.repo.WarrantyRepository;
import com.cj.productsvc.service.ProductServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.Mockito.mock;

// REVISED ProductControllerTestConfig within ProductControllerTest
//@TestConfiguration
class ProductControllerTestConfig {

   /* @Bean
    @Primary
    public JdbcTemplate mockJdbcTemplate() {
        return mock(JdbcTemplate.class);
    }*/

    // Mock the concrete implementation if that's what ProductServiceImpl requires
 /*   @Bean
    @Primary
    public ProductRepositoryImpl mockProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
        return mock(ProductRepositoryImpl.class); // Mock the concrete implementation
    }*/

    // Mock the concrete WarrantyRepository
  /*  @Bean
    @Primary
    public WarrantyRepository mockWarrantyRepository(JdbcTemplate jdbcTemplate) {
        return mock(WarrantyRepository.class);
    }*/

    // Provide a real instance of ProductServiceImpl, injecting its mocked dependencies
  /*  @Bean
    @Primary
    public ProductServiceImpl productServiceImpl(ProductRepositoryImpl mockProductRepositoryImpl, WarrantyRepository mockWarrantyRepository) {
        // Ensure this constructor matches ProductServiceImpl's @RequiredArgsConstructor
        return new ProductServiceImpl(mockProductRepositoryImpl, mockWarrantyRepository);
    }*/
}
