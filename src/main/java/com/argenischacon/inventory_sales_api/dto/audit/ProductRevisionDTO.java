package com.argenischacon.inventory_sales_api.dto.audit;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductRevisionDTO(
        Long revisionId,
        String username,
        Instant revisionDate,
        String revisionType,
        Long id,
        String name,
        String description,
        BigDecimal unitPrice,
        Integer stock,
        Long categoryId
) { }
