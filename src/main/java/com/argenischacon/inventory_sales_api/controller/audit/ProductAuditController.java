package com.argenischacon.inventory_sales_api.controller.audit;

import com.argenischacon.inventory_sales_api.dto.audit.ProductRevisionDTO;
import com.argenischacon.inventory_sales_api.service.audit.ProductAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/audit")
@RequiredArgsConstructor
public class ProductAuditController {

    private final ProductAuditService productAuditService;

    @GetMapping("/{productId}/revisions")
    public ResponseEntity<List<ProductRevisionDTO>> getProductRevisions(@PathVariable Long productId) {
        List<ProductRevisionDTO> revisions = new ArrayList<>();
        revisions = productAuditService.getProductRevisions(productId);
        return ResponseEntity.ok(revisions);
    }
}
