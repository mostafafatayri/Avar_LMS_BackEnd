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
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepo organizationRepo;
    private final UserOrganizationRepo userOrganizationRepo;
    private final UserRepo userRepo;

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
}