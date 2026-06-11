package com.fatayriTech.avarLMS.service.security;


public interface IJwtService {

    String generateToken(String user);

    String extractEmail(String token);

    boolean isTokenValid(String token);
}
