package com.fatayriTech.avarLMS.response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchResponse {
    private Long id;
    private String username;
    private String email;
    private String fullName;
}