package com.fatayriTech.avarLMS.service.OrganizationService;

import com.fatayriTech.avarLMS.dto.OrganizationCardDto;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.model.UserOrganization;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.UserOrganizationRepo;
import com.fatayriTech.avarLMS.request.OrganizationRequest;
import com.fatayriTech.avarLMS.response.OrganizationResponse;
import com.fatayriTech.avarLMS.repository.UserRepo;
import com.fatayriTech.avarLMS.model.Domain;
import com.fatayriTech.avarLMS.repository.DomainRepo;
import com.fatayriTech.avarLMS.request.OrganizationDomainRequest;
import com.fatayriTech.avarLMS.response.OrganizationDomainResponse;
import com.fatayriTech.avarLMS.response.OrganizationDomainUserResponse;
import lombok.RequiredArgsConstructor;
import com.fatayriTech.avarLMS.model.SecurityRole;
import com.fatayriTech.avarLMS.repository.SecurityRoleRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepo organizationRepo;
    private final UserOrganizationRepo userOrganizationRepo;
    private final UserRepo userRepo;
    private final DomainRepo domainRepo;
    private final SecurityRoleRepo securityRoleRepo;

    public List<OrganizationResponse> getAllOrganizations() {
        return organizationRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public OrganizationResponse getOrganizationById(Long id) {
        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return mapToResponse(organization);
    }

    public OrganizationResponse createOrganization(OrganizationRequest request) {
        if (organizationRepo.existsByCode(request.getCode())) {
            throw new RuntimeException("Organization code already exists");
        }

        Organization organization = new Organization();

        organization.setCode(request.getCode());
        organization.setName(request.getName());
        organization.setIndustry(request.getIndustry());
        organization.setContactEmail(request.getContactEmail());
        organization.setContactPhone(request.getContactPhone());
        organization.setLogoUrl(request.getLogoUrl());
        organization.setActive(request.getActive() != null ? request.getActive() : true);
        organization.setLicenseStartDate(request.getLicenseStartDate());
        organization.setLicenseEndDate(request.getLicenseEndDate());
        organization.setMaxUsers(request.getMaxUsers() != null ? request.getMaxUsers() : 50);

        Organization saved = organizationRepo.save(organization);

        User superAdmin = userRepo.findByEmail("superadmin@avar.com")
                .orElseThrow(() -> new RuntimeException("Super admin user not found"));

        UserOrganization userOrganization = new UserOrganization();
        userOrganization.setUser(superAdmin);
        userOrganization.setOrganization(saved);
        userOrganization.setDefaultOrganization(false);
        userOrganization.setActive(true);

        userOrganizationRepo.save(userOrganization);

        return mapToResponse(saved);
    }

    public OrganizationResponse updateOrganization(Long id, OrganizationRequest request) {
        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setName(request.getName());
        organization.setIndustry(request.getIndustry());
        organization.setContactEmail(request.getContactEmail());
        organization.setContactPhone(request.getContactPhone());
        organization.setLogoUrl(request.getLogoUrl());

        if (request.getActive() != null) {
            organization.setActive(request.getActive());
        }

        organization.setLicenseStartDate(request.getLicenseStartDate());
        organization.setLicenseEndDate(request.getLicenseEndDate());

        if (request.getMaxUsers() != null) {
            organization.setMaxUsers(request.getMaxUsers());
        }

        Organization saved = organizationRepo.save(organization);

        return mapToResponse(saved);
    }

    public void deleteOrganization(Long id) {
        Organization organization = organizationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setActive(false);
        organizationRepo.save(organization);
    }

    private OrganizationResponse mapToResponse(Organization organization) {
        return OrganizationResponse.builder()
                .id(organization.getId())
                .code(organization.getCode())
                .name(organization.getName())
                .industry(organization.getIndustry())
                .contactEmail(organization.getContactEmail())
                .contactPhone(organization.getContactPhone())
                .logoUrl(organization.getLogoUrl())
                .active(organization.getActive())
                .licenseStartDate(organization.getLicenseStartDate())
                .licenseEndDate(organization.getLicenseEndDate())
                .maxUsers(organization.getMaxUsers())
                .creationDate(organization.getCreationDate())
                .modificationDate(organization.getModificationDate())
                .build();
    }

    public List<OrganizationCardDto> getOrganizationsForUser(Long userId) {
        return userOrganizationRepo.findByUserIdAndActiveTrue(userId)
                .stream()
                .map(userOrganization -> {
                    Organization org = userOrganization.getOrganization();

                    return new OrganizationCardDto(
                            org.getId(),
                            org.getName(),
                            org.getCode(),
                            org.getLogoUrl(),
                            org.getIndustry()
                    );
                })
                .toList();
    }


    public OrganizationDomainResponse getOrganizationDomain(Long organizationId) {
        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (organization.getDomain() == null) {
            return null;
        }

        return mapToDomainResponse(organization.getDomain());
    }

    public OrganizationDomainResponse attachDomainToOrganization(
            Long organizationId,
            OrganizationDomainRequest request
    ) {
        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        String normalizedDomain = normalizeDomain(request.getDomain());

        Domain domain = domainRepo.findByDomainIgnoreCase(normalizedDomain)
                .orElseGet(() -> {
                    Domain newDomain = new Domain();
                    newDomain.setDomain(normalizedDomain);
                    newDomain.setAllowed(request.getAllowed() == null || request.getAllowed());
                    return domainRepo.save(newDomain);
                });

        if (request.getAllowed() != null) {
            domain.setAllowed(request.getAllowed());
            domain = domainRepo.save(domain);
        }

        organization.setDomain(domain);
        organizationRepo.save(organization);

        return mapToDomainResponse(domain);
    }

    public void removeDomainFromOrganization(Long organizationId) {
        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        organization.setDomain(null);
        organizationRepo.save(organization);
    }

    public List<OrganizationDomainUserResponse> getUsersByOrganizationDomain(Long organizationId) {
        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        if (organization.getDomain() == null) {
            return List.of();
        }



        return userRepo.findByDomainId(organization.getDomain().getId())
                .stream()
                .filter(user ->
                        user.getRoles()
                                .stream()
                                .noneMatch(role ->
                                        role.getCode().equals("ADMIN") ||
                                                role.getCode().equals("SUPER_ADMIN")
                                )
                )
                .map(user -> {
                    OrganizationDomainUserResponse response = mapToDomainUserResponse(user);
                    response.setHasOrganizationAccess(
                            userOrganizationRepo.existsByUserIdAndOrganizationIdAndActiveTrue(
                                    user.getId(),
                                    organizationId
                            )
                    );
                    return response;
                })
                .toList();
    }
    private OrganizationDomainResponse mapToDomainResponse(Domain domain) {
        return OrganizationDomainResponse.builder()
                .id(domain.getId())
                .domain(domain.getDomain())
                .allowed(domain.isAllowed())
                .build();
    }

    private OrganizationDomainUserResponse mapToDomainUserResponse(User user) {
        String fullName = String.join(" ",
                user.getFirstName() != null ? user.getFirstName() : "",
                user.getMiddleName() != null ? user.getMiddleName() : "",
                user.getLastName() != null ? user.getLastName() : ""
        ).trim().replaceAll(" +", " ");

        return OrganizationDomainUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(fullName.isBlank() ? user.getUsername() : fullName)
                .build();
    }

    private String normalizeDomain(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new RuntimeException("Domain is required");
        }

        return domain
                .trim()
                .toLowerCase()
                .replace("https://", "")
                .replace("http://", "")
                .replace("www.", "");
    }

    public List<OrganizationDomainUserResponse> getOrganizationAdmins(Long organizationId) {
        return userOrganizationRepo.findByOrganizationIdAndActiveTrue(organizationId)
                .stream()
                .map(UserOrganization::getUser)
                .filter(user ->
                        user.getRoles()
                                .stream()
                                .anyMatch(role ->
                                        role.getCode().equals("ADMIN") ||
                                                role.getCode().equals("SUPER_ADMIN")
                                )
                )
                .map(this::mapToDomainUserResponse)
                .toList();
    }

    public void grantOrganizationView(Long organizationId, Long userId) {
        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserOrganization userOrganization = userOrganizationRepo
                .findByUserIdAndOrganizationId(userId, organizationId)
                .orElseGet(() -> {
                    UserOrganization newUserOrganization = new UserOrganization();
                    newUserOrganization.setUser(user);
                    newUserOrganization.setOrganization(organization);
                    newUserOrganization.setDefaultOrganization(false);
                    return newUserOrganization;
                });

        userOrganization.setActive(true);
        userOrganizationRepo.save(userOrganization);
    }

    public void removeOrganizationView(Long organizationId, Long userId) {
        UserOrganization userOrganization = userOrganizationRepo
                .findByUserIdAndOrganizationId(userId, organizationId)
                .orElseThrow(() -> new RuntimeException("User does not have access to this organization"));

        userOrganization.setActive(false);
        userOrganization.setDefaultOrganization(false);

        userOrganizationRepo.save(userOrganization);
    }

    public void makeOrganizationAdmin(Long organizationId, Long userId) {
        grantOrganizationView(organizationId, userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        SecurityRole adminRole = securityRoleRepo.findByCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        user.getRoles().add(adminRole);
        userRepo.save(user);
    }

    public void removeOrganizationAdmin(Long organizationId, Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isSuperAdmin = user.getRoles()
                .stream()
                .anyMatch(role -> role.getCode().equals("SUPER_ADMIN"));

        if (isSuperAdmin) {
            throw new RuntimeException("Super admin cannot be removed");
        }

        SecurityRole adminRole = securityRoleRepo.findByCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("ADMIN role not found"));

        user.getRoles().removeIf(role -> role.getId().equals(adminRole.getId()));
        userRepo.save(user);
    }
}