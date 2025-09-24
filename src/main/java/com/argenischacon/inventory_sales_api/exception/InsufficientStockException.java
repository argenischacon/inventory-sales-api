package com.argenischacon.inventory_sales_api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.CONFLICT) //409 Conflict
public class InsufficientStockException extends RuntimeException{
    private final Long productId;
    private final int requestQuantity;
    private final int availableStock;

    public InsufficientStockException (String message, Long productId, int requestQuantity, int availableStock){
        super(message);
        this.productId = productId;
        this.requestQuantity = requestQuantity;
        this.availableStock = availableStock;
    }
}
