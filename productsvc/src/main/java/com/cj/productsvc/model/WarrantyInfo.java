package com.cj.productsvc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarrantyInfo {
    private Long id;
    private int durationMonths;
    private String warrantyType;
    private String description;
}
