package com.argenischacon.inventory_sales_api.audit;

import org.hibernate.envers.RevisionListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomRevisionListener implements RevisionListener {
    @Override
    public void newRevision(Object o) {
        CustomRevisionEntity customRevisionEntity = (CustomRevisionEntity) o;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            customRevisionEntity.setUsername("SYSTEM");
            return;
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        customRevisionEntity.setUsername(userDetails.getUsername());
    }
}
