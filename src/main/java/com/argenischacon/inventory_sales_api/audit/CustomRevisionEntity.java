package com.argenischacon.inventory_sales_api.audit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;

@Entity
@Table(name = "revisions")
@RevisionEntity(CustomRevisionListener.class)
@Getter
@Setter
public class CustomRevisionEntity extends DefaultRevisionEntity implements Comparable<CustomRevisionEntity> {

    @Column(name = "username")
    private String username;

    @Override
    public int compareTo(CustomRevisionEntity o) {
        return Integer.compare(this.getId(), o.getId());
    }
}
