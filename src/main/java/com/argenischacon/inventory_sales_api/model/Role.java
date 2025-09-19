package com.argenischacon.inventory_sales_api.model;

public enum Role {
    USER,
    ADMIN;

    @Override
    public String toString() {
        return "ROLE_"+ this.name();
    }
}
