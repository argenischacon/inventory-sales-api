package com.argenischacon.inventory_sales_api.controller.audit;

import com.argenischacon.inventory_sales_api.controller.api.ProductAuditAPI;
import com.argenischacon.inventory_sales_api.dto.audit.ProductRevisionDTO;
import com.argenischacon.inventory_sales_api.service.audit.ProductAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductAuditController implements ProductAuditAPI {
    private final ProductAuditService productAuditService;

    @Override
    public ResponseEntity<List<ProductRevisionDTO>> getProductRevisions(Long productId) {
        return ResponseEntity.ok(productAuditService.getProductRevisions(productId));
    }
}
