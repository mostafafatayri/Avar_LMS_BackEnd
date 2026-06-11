package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.repository.UserRepo;
import com.fatayriTech.avarLMS.response.UserSearchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepo userRepo;

    @GetMapping("/search")
    public List<UserSearchResponse> searchUsers(
            @RequestParam(required = false) String keyword
    ) {
        if (keyword == null || keyword.trim().length() < 2) {
            return List.of();
        }

        return userRepo.searchUsers(keyword.trim())
                .stream()
                .limit(10)
                .map(user -> new UserSearchResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        buildFullName(user)
                ))
                .toList();
    }

    private String buildFullName(User user) {
        return String.join(" ",
                user.getFirstName() != null ? user.getFirstName() : "",
                user.getMiddleName() != null ? user.getMiddleName() : "",
                user.getLastName() != null ? user.getLastName() : ""
        ).trim();
    }
}