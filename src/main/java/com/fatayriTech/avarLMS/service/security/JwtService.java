package com.fatayriTech.avarLMS.service.security;

import com.fatayriTech.avarLMS.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {

    private static final String SECRET_KEY = "12345678912345678912345678912345";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60 * 15; // 15 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 days

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
        String fullName = buildFullName(
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
                .claim("username", user.getUsername())
                .claim("fullName", fullName)
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

    public String extractUsername(String token) {
        return getClaims(token).get("username", String.class);
    }

    public String extractFullName(String token) {
        return getClaims(token).get("fullName", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractPermissions(String token) {
        Object permissions = getClaims(token).get("permissions");

        if (permissions == null) {
            return List.of();
        }

        return (List<String>) permissions;
    }
}
/*package com.fatayriTech.avar.service.security;

import com.fatayriTech.avar.model.AppUser;
import com.fatayriTech.avar.model.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "12345678912345678912345678912345";

    private static final long ACCESS_TOKEN_EXPIRATION = 1000L * 60; // 1 minute
    private static final long REFRESH_TOKEN_EXPIRATION = 1000L * 60 * 60 * 24 * 7; // 7 days

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(AppUser user) {
        return generateAccessToken(user);
    }

    public String generateAccessToken(AppUser user) {
        return buildToken(user, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(AppUser user) {
        return buildToken(user, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(AppUser user, long expiration) {
        Employee employee = user.getEmployee();

        Long employeeId = null;
        String fullName;

        if (employee != null) {
            employeeId = employee.getId();
            fullName = buildFullName(
                    employee.getFirstName(),
                    employee.getMiddleName(),
                    employee.getLastName()
            );
        } else {
            fullName = buildFullName(
                    user.getFirstName(),
                    user.getMiddleName(),
                    user.getLastName()
            );
        }

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("username", user.getUsername())
                .claim("employeeId", employeeId)
                .claim("fullName", fullName)
                .claim("departmentId",
                        employee != null && employee.getDepartment() != null
                                ? employee.getDepartment().getId()
                                : null
                )
                .claim("departmentName",
                        employee != null && employee.getDepartment() != null
                                ? employee.getDepartment().getName()
                                : null
                )
                .claim("positionId",
                        employee != null && employee.getPosition() != null
                                ? employee.getPosition().getId()
                                : null
                )
                .claim("positionName",
                        employee != null && employee.getPosition() != null
                                ? employee.getPosition().getName()
                                : null
                )
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
            if (!name.isEmpty()) {
                name.append(" ");
            }
            name.append(middleName.trim());
        }

        if (lastName != null && !lastName.isBlank()) {
            if (!name.isEmpty()) {
                name.append(" ");
            }
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

    public Long extractEmployeeId(String token) {
        Object employeeId = getClaims(token).get("employeeId");

        if (employeeId == null) {
            return null;
        }

        return Long.valueOf(employeeId.toString());
    }

    public String extractFullName(String token) {
        return getClaims(token).get("fullName", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}*/