package com.argenischacon.inventory_sales_api.repository;

import com.argenischacon.inventory_sales_api.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, RevisionRepository<Product, Long, Integer> {
    boolean existsByName(String name);

    Optional<Product> findByName(String name);
}
