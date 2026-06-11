package com.fatayriTech.avarLMS.service.OrganizationService;


import org.springframework.transaction.annotation.Transactional;
import com.fatayriTech.avarLMS.dto.CreateOrganizationRequest;
import com.fatayriTech.avarLMS.dto.OrganizationCardDto;
import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.User;
import com.fatayriTech.avarLMS.model.UserOrganization;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.UserOrganizationRepo;
import com.fatayriTech.avarLMS.repository.UserRepo;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final UserOrganizationRepo userOrganizationRepo;
    private final OrganizationRepo organizationRepo;
    private final UserRepo userRepo;


    public List<OrganizationCardDto> getOrganizationsForUser(Long userId) {

        List<UserOrganization> links =
                userOrganizationRepo.findByUserIdAndActiveTrue(userId);

        return links.stream()
                .map(link -> {
                    var org = link.getOrganization();

                    return new OrganizationCardDto(
                            org.getId(),
                            org.getName(),
                            "testing",
                            "Member", // temporary until org-specific roles are loaded
                            0L,       // later from org DB
                            0L,       // later from org DB
                             "purple"
                    );
                })
                .toList();
    }


    @Transactional(transactionManager = "masterTransactionManager")
    public OrganizationCardDto createOrganization(CreateOrganizationRequest request, Long creatorUserId) {

        if (organizationRepo.existsBySlug(request.getSlug())) {
            throw new RuntimeException("Organization slug already exists");
        }

        User creator = userRepo.findById(creatorUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Organization organization = new Organization();
        organization.setName(request.getName());
        organization.setSlug(request.getSlug());
        organization.setColor(request.getColor() != null ? request.getColor() : "purple");
        organization.setActive(true);

        organization = organizationRepo.saveAndFlush(organization);

        String dbName = "Avar_Organization_" + organization.getId();

        organization.setDatabaseName(dbName);
        organization = organizationRepo.saveAndFlush(organization);



        UserOrganization access = new UserOrganization();
        access.setUser(creator);
        access.setOrganization(organization);
        access.setActive(true);
        access.setDefaultOrganization(false);

        userOrganizationRepo.saveAndFlush(access);



        return new OrganizationCardDto(
                organization.getId(),
                organization.getName(),
                organization.getSlug(),
                "Super Admin",
                1L,
                0L,
                organization.getColor()
        );
    }
}
                