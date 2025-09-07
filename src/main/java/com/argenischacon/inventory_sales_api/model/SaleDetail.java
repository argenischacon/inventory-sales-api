package com.argenischacon.inventory_sales_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SoftDelete;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table (name = "sale_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@SoftDelete(columnName = "deleted")
public class SaleDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Positive
    private Integer quantity;

    @Column(nullable = false, precision = 19, scale = 2)
    @PositiveOrZero
    private BigDecimal unitPrice;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (name = "product_id", nullable = false)
    @NotNull
    private Product product;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    @NotNull
    private Sale sale;

    public BigDecimal getSubTotal(){
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
