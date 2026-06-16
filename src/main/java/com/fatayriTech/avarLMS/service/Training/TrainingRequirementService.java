package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.model.TrainingRequirement;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.repository.TrainingRequirementRepo;
import com.fatayriTech.avarLMS.request.training.TrainingRequirementRequest;
import com.fatayriTech.avarLMS.response.training.TrainingRequirementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingRequirementService {

    private final TrainingRequirementRepo requirementRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;

    public List<TrainingRequirementResponse> getAll(
            Long organizationId,
            Long trainingId
    ) {
        return requirementRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingRequirementResponse create(
            Long organizationId,
            Long trainingId,
            TrainingRequirementRequest request
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        TrainingRequirement requirement = TrainingRequirement.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .title(request.getTitle())
                .description(request.getDescription())
                .requirementType(request.getRequirementType())
                .requirementValue(request.getRequirementValue())
                .mandatory(request.getMandatory() != null ? request.getMandatory() : true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(true)
                .build();

        return mapToResponse(requirementRepo.save(requirement));
    }

    public TrainingRequirementResponse update(
            Long organizationId,
            Long trainingId,
            Long requirementId,
            TrainingRequirementRequest request
    ) {
        TrainingRequirement requirement = findRequirement(
                organizationId,
                trainingId,
                requirementId
        );

        requirement.setTitle(request.getTitle());
        requirement.setDescription(request.getDescription());
        requirement.setRequirementType(request.getRequirementType());
        requirement.setRequirementValue(request.getRequirementValue());
        requirement.setMandatory(request.getMandatory() != null ? request.getMandatory() : true);
        requirement.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        return mapToResponse(requirementRepo.save(requirement));
    }

    public void delete(
            Long organizationId,
            Long trainingId,
            Long requirementId
    ) {
        TrainingRequirement requirement = findRequirement(
                organizationId,
                trainingId,
                requirementId
        );

        requirement.setActive(false);
        requirementRepo.save(requirement);
    }

    private TrainingRequirement findRequirement(
            Long organizationId,
            Long trainingId,
            Long requirementId
    ) {
        return requirementRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueId(
                        requirementId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Requirement not found"));
    }

    private TrainingRequirementResponse mapToResponse(TrainingRequirement requirement) {
        return TrainingRequirementResponse.builder()
                .id(requirement.getId())
                .organizationId(requirement.getOrganizationId())
                .trainingCatalogueId(requirement.getTrainingCatalogue().getId())
                .title(requirement.getTitle())
                .description(requirement.getDescription())
                .requirementType(requirement.getRequirementType())
                .requirementValue(requirement.getRequirementValue())
                .mandatory(requirement.getMandatory())
                .displayOrder(requirement.getDisplayOrder())
                .active(requirement.getActive())
                .creationDate(requirement.getCreationDate())
                .modificationDate(requirement.getModificationDate())
                .build();
    }
}