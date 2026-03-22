package com.vaibhav.product_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(CategoryExistException.class)
    public ResponseEntity<String> CategoryExistExceptionHandler(CategoryExistException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(CategoryIdNotExist.class)
    public ResponseEntity<String> CategoryIdNotExistExceptionHandler(CategoryIdNotExist ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> ResourceNotFoundException(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BrandExistException.class)
    public ResponseEntity<String> BrandAlreadyExistExceptionHandler(BrandExistException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(BrandNotExistException.class)
    public ResponseEntity<String> BrandNotExistExceptionExceptionHandler(BrandNotExistException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> ProductNotFoundExceptionExceptionHandler(ProductNotFoundException ex){
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }


}
