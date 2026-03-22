package com.vaibhav.product_service.exception;

public class BrandExistException extends RuntimeException{
    public BrandExistException(String message){
        super(message);
    }
}
