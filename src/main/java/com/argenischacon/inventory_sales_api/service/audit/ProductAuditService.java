package com.argenischacon.inventory_sales_api.service.audit;

import com.argenischacon.inventory_sales_api.audit.CustomRevisionEntity;
import com.argenischacon.inventory_sales_api.dto.audit.ProductRevisionDTO;
import com.argenischacon.inventory_sales_api.model.Product;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductAuditService {

    private final EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<ProductRevisionDTO> getProductRevisions(Long productId) {
        AuditReader reader = AuditReaderFactory.get(entityManager);

        List<Object[]> revisions = reader.createQuery()
                .forRevisionsOfEntity(Product.class, false, true)
                .add(AuditEntity.id().eq(productId))
                .getResultList();

        return revisions.stream()
                .map(obj -> {
                    Object[] tuple = (Object[]) obj;
                    Product productRev = (Product) tuple[0];
                    CustomRevisionEntity revisionInfo = (CustomRevisionEntity) tuple[1];
                    RevisionType revType = (RevisionType) tuple[2];

                    return new ProductRevisionDTO(
                            Long.valueOf(revisionInfo.getId()),
                            revisionInfo.getUsername(),
                            revisionInfo.getRevisionDate().toInstant(),
                            revType.name(),
                            productRev.getId(),
                            productRev.getName(),
                            productRev.getDescription(),
                            productRev.getUnitPrice(),
                            productRev.getStock(),
                            productRev.getCategory().getId()
                    );
                })
                .toList();
    }
}
