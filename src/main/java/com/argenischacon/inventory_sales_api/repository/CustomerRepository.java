package com.argenischacon.inventory_sales_api.repository;

import com.argenischacon.inventory_sales_api.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>, RevisionRepository<Customer, Long, Integer> {
    boolean existsByDni(String dni);

    Optional<Customer> findByDni(String dni);
}
