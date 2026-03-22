package com.vaibhav.product_service.exception;

public class BrandNotExistException extends RuntimeException{
    public BrandNotExistException(String message){
        super(message);
    }
}
