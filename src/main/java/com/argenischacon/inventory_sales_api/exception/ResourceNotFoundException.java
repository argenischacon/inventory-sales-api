package com.argenischacon.inventory_sales_api.exception;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException (String message){
        super(message);
    }
}
