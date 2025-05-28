package com.cj.productsvc.exception;

public class WarrantyNotFoundException extends RuntimeException{
    public WarrantyNotFoundException(String message) {
        super(message);
    }
}
