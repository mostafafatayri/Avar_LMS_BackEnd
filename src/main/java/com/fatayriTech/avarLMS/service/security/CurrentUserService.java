package com.fatayriTech.avarLMS.service.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService {

    public CurrentUser getCurrentUser() {

        return (CurrentUser)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
    }

    public Long getUserId() {
        return getCurrentUser().getUserId();
    }

    public Long getOrganizationId() {
        return getCurrentUser().getOrganizationId();
    }
}