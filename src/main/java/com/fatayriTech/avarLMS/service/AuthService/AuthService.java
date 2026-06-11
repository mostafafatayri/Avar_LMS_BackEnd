package com.fatayriTech.avarLMS.service.AuthService;

import com.fatayriTech.avarLMS.dto.OrganizationCardDto;
import com.fatayriTech.avarLMS.model.User;
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
                new LoginResponse(accessToken, refreshToken, "Bearer",organizations)
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

        User user = new User();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setConfirmed(true);

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
                new LoginResponse(newAccessToken, null, "Bearer",organizations)
        );
    }
}


/*package com.fatayriTech.avar.service.AuthService;

import com.fatayriTech.avar.model.AppUser;
import com.fatayriTech.avar.model.SecurityRole;
import com.fatayriTech.avar.repository.AppUserRepo;
import com.fatayriTech.avar.repository.SecurityRoleRepo;
import com.fatayriTech.avar.request.LoginRequest;
import com.fatayriTech.avar.request.SignupRequest;
import com.fatayriTech.avar.response.LoginResponse;
import com.fatayriTech.avar.response.SignupResponse;
import com.fatayriTech.avar.service.EmailService.EmailQueueService;
import com.fatayriTech.avar.service.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AppUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final SecurityRoleRepo roleRepo;
    private final EmailQueueService emailQueueService;

    @Async
    public CompletableFuture<LoginResponse> login(LoginRequest loginRequest) {

        String identifier = loginRequest.getIdentifier();

        AppUser user = userRepo.findByEmailOrUsernameWithEmployeeDetails(identifier)
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

        return CompletableFuture.completedFuture(
                new LoginResponse(accessToken, refreshToken, "Bearer")
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

        SecurityRole memberRole = roleRepo.findByCode("MEMBER")
                .orElseGet(() -> {
                    SecurityRole role = new SecurityRole();
                    role.setName("Member");
                    role.setCode("MEMBER");
                    role.setDescription("Default member access");
                    role.setActive(true);
                    return roleRepo.save(role);
                });

        AppUser user = new AppUser();
        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstName());
        user.setMiddleName(request.getMiddleName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setConfirmed(true);
        user.setRoles(Set.of(memberRole));

        userRepo.save(user);

        emailQueueService.queueEmail(
                user.getEmail(),
                "OTP Verification",
                "Dear "+user.getFirstName()+
                        "\nHope this email finds you well, " +
                        "Your OTP code is: 5544 \nBest regards"
        );
        return CompletableFuture.completedFuture(
                new SignupResponse("User registered successfully")
        );
    }


    @Async
    public CompletableFuture<LoginResponse> refresh(String refreshToken) {



        AppUser user = userRepo.findByRefreshTokenWithEmployeeDetails(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Refresh token expired or invalid");
        }

        String newAccessToken = jwtService.generateToken(user);

        return CompletableFuture.completedFuture(
                new LoginResponse(newAccessToken, null, "Bearer")
        );
    }
}*/