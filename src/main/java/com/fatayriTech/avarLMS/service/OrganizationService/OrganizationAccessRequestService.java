package com.fatayriTech.avarLMS.service.OrganizationService;

import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.OrganizationAccessRequest;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.repository.OrganizationAccessRequestRepo;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.UserRepo;
import com.fatayriTech.avarLMS.response.organization.OrganizationAccessRequestResponse;
import com.fatayriTech.avarLMS.service.EmailService.EmailQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrganizationAccessRequestService {

    private final OrganizationRepo organizationRepo;
    private final OrganizationAccessRequestRepo accessRequestRepo;
    private final UserRepo userRepo;
    private final EmailQueueService emailQueueService;

    public OrganizationAccessRequestResponse contactAdministrator(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String email = user.getEmail();

        if (email == null || !email.contains("@")) {
            throw new RuntimeException("User email is invalid.");
        }

        String domainName = email.substring(email.indexOf("@") + 1).toLowerCase();

        if (isPublicEmailDomain(domainName)) {
            throw new RuntimeException(
                    "No organization is associated with public email domain: " + domainName
            );
        }

        Organization organization = organizationRepo
                .findByDomainDomainIgnoreCaseAndActiveTrue(domainName)
                .orElseThrow(() -> new RuntimeException(
                        "No organization is associated with your email domain: " + domainName
                ));

        accessRequestRepo
                .findByRequesterUserIdAndOrganizationIdAndStatus(
                        user.getId(),
                        organization.getId(),
                        "PENDING"
                )
                .ifPresent(existing -> {
                    throw new RuntimeException(
                            "You already have a pending access request for this organization."
                    );
                });

        OrganizationAccessRequest request = OrganizationAccessRequest.builder()
                .requesterUserId(user.getId())
                .requesterEmail(user.getEmail())
                .requesterName(resolveUserName(user))
                .emailDomain(domainName)
                .organization(organization)
                .status("PENDING")
                .message("User requested organization access based on email domain.")
                .build();

        OrganizationAccessRequest savedRequest = accessRequestRepo.save(request);

        queueAdminEmail(savedRequest, organization);

        return mapToResponse(savedRequest);
    }

    private void queueAdminEmail(
            OrganizationAccessRequest request,
            Organization organization
    ) {
        User primaryContact = organization.getPrimaryContactUser();

        String adminEmail = null;

        if (primaryContact != null && primaryContact.getEmail() != null && !primaryContact.getEmail().isBlank()) {
            adminEmail = primaryContact.getEmail();
        } else if (organization.getContactEmail() != null && !organization.getContactEmail().isBlank()) {
            adminEmail = organization.getContactEmail();
        }

        if (adminEmail == null || adminEmail.isBlank()) {
            throw new RuntimeException(
                    "Organization was found, but no primary administrator email is configured."
            );
        }

        String subject = "Organization Access Request - " + organization.getName();

        String body =
                "Hello,\n\n" +
                        "A user is requesting access to your organization in AVAR LMS.\n\n" +
                        "Organization: " + organization.getName() + "\n" +
                        "Organization Code: " + organization.getCode() + "\n\n" +
                        "Requester Name: " + request.getRequesterName() + "\n" +
                        "Requester Email: " + request.getRequesterEmail() + "\n" +
                        "Requester Domain: " + request.getEmailDomain() + "\n\n" +
                        "Request Status: " + request.getStatus() + "\n\n" +
                        "Please login to AVAR LMS to review and approve or reject this request.\n\n" +
                        "Regards, the admin is "+adminEmail+"\n" +
                        "AVAR LMS";

        emailQueueService.queueEmail("mostafa.fatayri@lyriclogic.com", subject, body);
    }

    private String resolveUserName(User user) {
        String firstName = user.getFirstName() != null ? user.getFirstName() : "";
        String lastName = user.getLastName() != null ? user.getLastName() : "";
        String fullName = (firstName + " " + lastName).trim();

        if (!fullName.isBlank()) {
            return fullName;
        }

        if (user.getUsername() != null && !user.getUsername().isBlank()) {
            return user.getUsername();
        }

        return user.getEmail();
    }

    private boolean isPublicEmailDomain(String domain) {
        return domain.equals("gmail.com")
                || domain.equals("yahoo.com")
                || domain.equals("hotmail.com")
                || domain.equals("outlook.com")
                || domain.equals("icloud.com")
                || domain.equals("live.com");
    }

    private OrganizationAccessRequestResponse mapToResponse(
            OrganizationAccessRequest request
    ) {
        Organization organization = request.getOrganization();

        return OrganizationAccessRequestResponse.builder()
                .id(request.getId())
                .requesterUserId(request.getRequesterUserId())
                .requesterEmail(request.getRequesterEmail())
                .requesterName(request.getRequesterName())
                .emailDomain(request.getEmailDomain())
                .organizationId(organization.getId())
                .organizationName(organization.getName())
                .status(request.getStatus())
                .message(request.getMessage())
                .creationDate(request.getCreationDate())
                .build();
    }
}