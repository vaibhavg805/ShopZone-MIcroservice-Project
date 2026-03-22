package com.vaibhav.product_service.exception;

public class CategoryIdNotExist extends RuntimeException{
    public CategoryIdNotExist(String message){
        super(message);
    }
}
