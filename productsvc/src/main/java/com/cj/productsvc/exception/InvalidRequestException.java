package com.cj.productsvc.exception;

public class InvalidRequestException  extends RuntimeException {
    public InvalidRequestException(String message) {
        super(message);
    }
}
