package com.fatayriTech.avarLMS.service.security;

import com.fatayriTech.avarLMS.repository.SecurityRoleRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationSecurityService {

    private final SecurityRoleRepo securityRoleRepo;

    public Set<SimpleGrantedAuthority> getAuthoritiesForUser(Long userId) {
        return securityRoleRepo.findByMasterUserId(userId)
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toSet());
    }
}