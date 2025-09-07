package com.argenischacon.inventory_sales_api.repository;

import com.argenischacon.inventory_sales_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
