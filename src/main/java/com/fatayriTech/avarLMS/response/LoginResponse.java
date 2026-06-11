package com.fatayriTech.avarLMS.response;

import com.fatayriTech.avarLMS.dto.OrganizationCardDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private String tokenType;

    private List<OrganizationCardDto> organizations;
}