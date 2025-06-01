package com.cj.productsvc.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private Long id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "Brand is mandatory")
    private String brand;

    private String description;

    @NotNull(message = "Price is mandatory")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than zero")
    private BigDecimal price;
    @NotNull(message = "Stock Quantity is mandatory")
    @Min(value = 0, message = "Stock quantity must be non-negative")
    private int stockQuantity;

    @Min(value = 1, message = "warrantyId must be greater than or equal to 1 if provided")
    private Long warrantyId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @NotBlank(message = "CreatedBy is mandatory")
    private String createdBy;

    private String updatedBy;
}
