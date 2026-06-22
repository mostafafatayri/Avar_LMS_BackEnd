package com.fatayriTech.avarLMS.response.organization;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrganizationAccessRequestResponse {

    private Long id;

    private Long requesterUserId;

    private String requesterEmail;

    private String requesterName;

    private String emailDomain;

    private Long organizationId;

    private String organizationName;

    private String status;

    private String message;

    private LocalDateTime creationDate;
}