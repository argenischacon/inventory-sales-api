package com.argenischacon.inventory_sales_api.repository;

import com.argenischacon.inventory_sales_api.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.history.RevisionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long>, RevisionRepository<SaleDetail, Long, Integer> {
    List<SaleDetail> findBySaleId(Long saleId);
}
