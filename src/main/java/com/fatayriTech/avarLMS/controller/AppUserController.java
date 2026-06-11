package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.LoginRequest;
import com.fatayriTech.avarLMS.request.SignupRequest;
import com.fatayriTech.avarLMS.response.LoginResponse;
import com.fatayriTech.avarLMS.response.SignupResponse;
import com.fatayriTech.avarLMS.service.AuthService.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/auth")
@RequiredArgsConstructor
public class AppUserController {

    private final AuthService authService;

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<LoginResponse>> login(
            @RequestBody LoginRequest request
    ) {
        return authService.login(request)
                .thenApply(loginResponse -> {
                    ResponseCookie cookie = ResponseCookie
                            .from("refreshToken", loginResponse.getRefreshToken())
                            .httpOnly(true)
                            .secure(false) // true in production with HTTPS
                            .sameSite("Lax")
                            .path("/")
                            .maxAge(7 * 24 * 60 * 60)
                            .build();

                    loginResponse.setRefreshToken(null);

                    return ResponseEntity.ok()
                            .header(HttpHeaders.SET_COOKIE, cookie.toString())
                            .body(loginResponse);
                });
    }

    @PostMapping("/signup")
    public CompletableFuture<SignupResponse> signup(
            @RequestBody SignupRequest request
    ) {
        return authService.signup(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    @PostMapping("/refresh")
    public CompletableFuture<ResponseEntity<LoginResponse>> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new RuntimeException("Refresh token cookie is missing");
        }

        return authService.refresh(refreshToken)
                .thenApply(ResponseEntity::ok);
    }
}