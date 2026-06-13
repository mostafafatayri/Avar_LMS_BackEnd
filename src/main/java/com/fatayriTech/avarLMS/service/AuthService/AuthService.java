package com.fatayriTech.avarLMS.service.AuthService;

import com.fatayriTech.avarLMS.dto.OrganizationCardDto;
import com.fatayriTech.avarLMS.model.Domain;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.repository.DomainRepo;
import com.fatayriTech.avarLMS.repository.UserRepo;
import com.fatayriTech.avarLMS.request.LoginRequest;
import com.fatayriTech.avarLMS.request.SignupRequest;
import com.fatayriTech.avarLMS.response.LoginResponse;
import com.fatayriTech.avarLMS.response.SignupResponse;
import com.fatayriTech.avarLMS.service.EmailService.EmailQueueService;
import com.fatayriTech.avarLMS.service.OrganizationService.OrganizationService;
import com.fatayriTech.avarLMS.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final DomainRepo domainRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailQueueService emailQueueService;
    private final OrganizationService organizationService;

    @Async
    public CompletableFuture<LoginResponse> login(LoginRequest loginRequest) {
        String identifier = loginRequest.getIdentifier();

        User user = userRepo.findByEmailOrUsername(identifier, identifier)
                .orElseThrow(() -> new BadCredentialsException("Invalid username/email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username/email or password");
        }

        if (!user.isConfirmed()) {
            throw new BadCredentialsException("The user is not confirmed yet");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        user.setRefreshToken(refreshToken);
        userRepo.save(user);

        List<OrganizationCardDto> organizations =
                organizationService.getOrganizationsForUser(user.getId());

        return CompletableFuture.completedFuture(
                new LoginResponse(accessToken, refreshToken, "Bearer", organizations)
        );
    }

    @Async
    public CompletableFuture<SignupResponse> signup(SignupRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new BadCredentialsException("Email already exists");
        }

        if (userRepo.existsByUsername(request.getUsername())) {
            throw new BadCredentialsException("Username already exists");
        }

        String emailDomain = extractDomainFromEmail(request.getEmail());

        Domain domain = domainRepo.findByDomainIgnoreCase(emailDomain)
                .orElseGet(() -> {
                    Domain newDomain = new Domain();
                    newDomain.setDomain(emailDomain);
                    newDomain.setAllowed(false);
                    return domainRepo.save(newDomain);
                });

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setConfirmed(true);
        user.setDomain(domain);

        userRepo.save(user);

        emailQueueService.queueEmail(
                user.getEmail(),
                "OTP Verification",
                "Dear " + user.getFirstName() +
                        "\nHope this email finds you well, " +
                        "Your OTP code is: 5544 \nBest regards"
        );

        return CompletableFuture.completedFuture(
                new SignupResponse("User registered successfully")
        );
    }

    @Async
    public CompletableFuture<LoginResponse> refresh(String refreshToken) {
        User user = userRepo.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtService.generateToken(user);

        List<OrganizationCardDto> organizations =
                organizationService.getOrganizationsForUser(user.getId());

        return CompletableFuture.completedFuture(
                new LoginResponse(newAccessToken, null, "Bearer", organizations)
        );
    }

    private String extractDomainFromEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new BadCredentialsException("Invalid email address");
        }

        String[] parts = email.trim().toLowerCase().split("@");

        if (parts.length != 2 || parts[1].isBlank()) {
            throw new BadCredentialsException("Invalid email address");
        }

        return parts[1];
    }
}