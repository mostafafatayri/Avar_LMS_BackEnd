package com.fatayriTech.avarLMS.service.security;

import com.fatayriTech.avarLMS.enums.DataScope;
import org.springframework.stereotype.Service;

@Service
public class SecurityScopeService {

    public DataScope getCurrentScope() {
        if (
                SecurityUtils.hasAuthority("ROLE_CREATE") ||
                        SecurityUtils.hasAuthority("EMPLOYEE_CREATE") ||
                        SecurityUtils.hasAuthority("EMPLOYEE_BULK_UPLOAD")
        ) {
            return DataScope.ALL;
        }

        if (SecurityUtils.hasAuthority("TRAINING_ASSIGNMENT_VIEW")
                && !SecurityUtils.hasAuthority("EMPLOYEE_CREATE")) {
            return DataScope.TEAM;
        }

        return DataScope.SELF;
    }
}