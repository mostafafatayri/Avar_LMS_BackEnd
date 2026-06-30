package com.fatayriTech.avarLMS.service.security;

import com.fatayriTech.avarLMS.model.Employee;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.repository.Employee.EmployeeRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtService {

    private static final String SECRET_KEY = "12345678912345678912345678912345";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 15;
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7;

    private final EmployeeRepo employeeRepo;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        return generateAccessToken(user);
    }

    public String generateAccessToken(User user) {
        return buildToken(user, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(User user, long expiration) {
        Employee employee = employeeRepo.findByMasterUserId(user.getId()).orElse(null);

        String fullName = employee != null
                ? buildFullName(
                employee.getFirstName(),
                employee.getMiddleName(),
                employee.getLastName()
        )
                : buildFullName(
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName()
        );

        List<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getCode())
                .toList();

        List<String> permissions = user.getRoles()
                .stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> permission.getName())
                .distinct()
                .toList();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("employeeId", employee != null ? employee.getId() : null)
                .claim("username", user.getUsername())
                .claim("fullName", fullName)
                .claim(
                        "departmentId",
                        employee != null && employee.getDepartment() != null
                                ? employee.getDepartment().getId()
                                : null
                )
                .claim(
                        "departmentName",
                        employee != null && employee.getDepartment() != null
                                ? employee.getDepartment().getName()
                                : ""
                )
                .claim(
                        "positionId",
                        employee != null && employee.getPosition() != null
                                ? employee.getPosition().getId()
                                : null
                )
                .claim(
                        "positionName",
                        employee != null && employee.getPosition() != null
                                ? employee.getPosition().getName()
                                : ""
                )
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private String buildFullName(String firstName, String middleName, String lastName) {
        StringBuilder name = new StringBuilder();

        if (firstName != null && !firstName.isBlank()) {
            name.append(firstName.trim());
        }

        if (middleName != null && !middleName.isBlank()) {
            if (!name.isEmpty()) name.append(" ");
            name.append(middleName.trim());
        }

        if (lastName != null && !lastName.isBlank()) {
            if (!name.isEmpty()) name.append(" ");
            name.append(lastName.trim());
        }

        return name.toString();
    }

    public boolean isTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object userId = getClaims(token).get("userId");

        if (userId == null) {
            return null;
        }

        return Long.valueOf(userId.toString());
    }

    public Long extractEmployeeId(String token) {
        Object employeeId = getClaims(token).get("employeeId");

        if (employeeId == null) {
            return null;
        }

        return Long.valueOf(employeeId.toString());
    }

    public String extractUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    public String extractFullName(String token) {
        return getClaims(token).get("fullName", String.class);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        Object permissions = getClaims(token).get("permissions");

        if (permissions == null) {
            return List.of();
        }

        return (List<String>) permissions;
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}