package com.fatayriTech.avarLMS.service.WorkStructure;

import com.fatayriTech.avarLMS.model.Organization;
import com.fatayriTech.avarLMS.model.SeniorityLevel;
import com.fatayriTech.avarLMS.repository.OrganizationRepo;
import com.fatayriTech.avarLMS.repository.SeniorityLevelRepo;
import com.fatayriTech.avarLMS.request.SeniorityLevelRequest;
import com.fatayriTech.avarLMS.response.SeniorityLevelResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeniorityLevelService {

    private final SeniorityLevelRepo seniorityLevelRepo;
    private final OrganizationRepo organizationRepo;

    public List<SeniorityLevelResponse> getAll(Long organizationId) {
        return seniorityLevelRepo
                .findByOrganizationIdOrderByDisplayOrderAsc(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<SeniorityLevelResponse> getActive(Long organizationId) {
        return seniorityLevelRepo
                .findByOrganizationIdAndActiveTrueOrderByDisplayOrderAsc(organizationId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public SeniorityLevelResponse getById(Long organizationId, Long id) {
        return mapToResponse(findSeniorityLevel(organizationId, id));
    }

    public SeniorityLevelResponse create(Long organizationId, SeniorityLevelRequest request) {
        validateRequest(request);

        if (seniorityLevelRepo.existsByNameIgnoreCaseAndOrganizationId(
                request.getName(),
                organizationId
        )) {
            throw new RuntimeException("Seniority level name already exists in this organization");
        }

        if (seniorityLevelRepo.existsByDisplayOrderAndOrganizationId(
                request.getDisplayOrder(),
                organizationId
        )) {
            throw new RuntimeException("Seniority level order already exists in this organization");
        }

        Organization organization = organizationRepo.findById(organizationId)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        SeniorityLevel seniorityLevel = SeniorityLevel.builder()
                .organization(organization)
                .name(request.getName().trim())
                .displayOrder(request.getDisplayOrder())
                .description(request.getDescription())
                .active(request.getActive() == null || request.getActive())
                .build();

        return mapToResponse(seniorityLevelRepo.save(seniorityLevel));
    }

    public SeniorityLevelResponse update(
            Long organizationId,
            Long id,
            SeniorityLevelRequest request
    ) {
        validateRequest(request);

        SeniorityLevel seniorityLevel = findSeniorityLevel(organizationId, id);

        if (!seniorityLevel.getName().equalsIgnoreCase(request.getName())
                && seniorityLevelRepo.existsByNameIgnoreCaseAndOrganizationId(
                request.getName(),
                organizationId
        )) {
            throw new RuntimeException("Seniority level name already exists in this organization");
        }

        if (!seniorityLevel.getDisplayOrder().equals(request.getDisplayOrder())
                && seniorityLevelRepo.existsByDisplayOrderAndOrganizationId(
                request.getDisplayOrder(),
                organizationId
        )) {
            throw new RuntimeException("Seniority level order already exists in this organization");
        }

        seniorityLevel.setName(request.getName().trim());
        seniorityLevel.setDisplayOrder(request.getDisplayOrder());
        seniorityLevel.setDescription(request.getDescription());

        if (request.getActive() != null) {
            seniorityLevel.setActive(request.getActive());
        }

        return mapToResponse(seniorityLevelRepo.save(seniorityLevel));
    }

    public SeniorityLevelResponse setActive(Long organizationId, Long id) {
        SeniorityLevel seniorityLevel = findSeniorityLevel(organizationId, id);
        seniorityLevel.setActive(true);
        return mapToResponse(seniorityLevelRepo.save(seniorityLevel));
    }

    public SeniorityLevelResponse setInactive(Long organizationId, Long id) {
        SeniorityLevel seniorityLevel = findSeniorityLevel(organizationId, id);
        seniorityLevel.setActive(false);
        return mapToResponse(seniorityLevelRepo.save(seniorityLevel));
    }

    public void delete(Long organizationId, Long id) {
        SeniorityLevel seniorityLevel = findSeniorityLevel(organizationId, id);
        seniorityLevelRepo.delete(seniorityLevel);
    }

    private SeniorityLevel findSeniorityLevel(Long organizationId, Long id) {
        return seniorityLevelRepo.findByIdAndOrganizationId(id, organizationId)
                .orElseThrow(() -> new RuntimeException("Seniority level not found"));
    }

    private void validateRequest(SeniorityLevelRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Seniority level name is required");
        }

        if (request.getDisplayOrder() == null) {
            throw new RuntimeException("Seniority level order is required");
        }

        if (request.getDisplayOrder() <= 0) {
            throw new RuntimeException("Seniority level order must be greater than 0");
        }
    }

    private SeniorityLevelResponse mapToResponse(SeniorityLevel seniorityLevel) {
        return SeniorityLevelResponse.builder()
                .id(seniorityLevel.getId())
                .name(seniorityLevel.getName())
                .displayOrder(seniorityLevel.getDisplayOrder())
                .description(seniorityLevel.getDescription())
                .active(seniorityLevel.isActive())
                .creationDate(seniorityLevel.getCreationDate())
                .modificationDate(seniorityLevel.getModificationDate())
                .build();
    }
}