package com.vaibhav.product_service.exception;

public class CategoryExistException extends RuntimeException{

    public CategoryExistException(String message){
        super(message);
    }
}
