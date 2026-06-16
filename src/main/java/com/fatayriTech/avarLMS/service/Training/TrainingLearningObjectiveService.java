package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.model.TrainingLearningObjective;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.repository.TrainingLearningObjectiveRepo;
import com.fatayriTech.avarLMS.request.training.TrainingLearningObjectiveRequest;
import com.fatayriTech.avarLMS.response.training.TrainingLearningObjectiveResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingLearningObjectiveService {

    private final TrainingLearningObjectiveRepo objectiveRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;

    public List<TrainingLearningObjectiveResponse> getAll(Long organizationId, Long trainingId) {
        return objectiveRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public TrainingLearningObjectiveResponse create(
            Long organizationId,
            Long trainingId,
            TrainingLearningObjectiveRequest request
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        TrainingLearningObjective objective = TrainingLearningObjective.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .objectiveText(request.getObjectiveText())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(true)
                .build();

        return mapToResponse(objectiveRepo.save(objective));
    }

    public TrainingLearningObjectiveResponse update(
            Long organizationId,
            Long trainingId,
            Long objectiveId,
            TrainingLearningObjectiveRequest request
    ) {
        TrainingLearningObjective objective = objectiveRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueId(
                        objectiveId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Objective not found"));

        objective.setObjectiveText(request.getObjectiveText());
        objective.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        return mapToResponse(objectiveRepo.save(objective));
    }

    public void delete(Long organizationId, Long trainingId, Long objectiveId) {
        TrainingLearningObjective objective = objectiveRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueId(
                        objectiveId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Objective not found"));

        objective.setActive(false);
        objectiveRepo.save(objective);
    }

    private TrainingLearningObjectiveResponse mapToResponse(TrainingLearningObjective objective) {
        return TrainingLearningObjectiveResponse.builder()
                .id(objective.getId())
                .organizationId(objective.getOrganizationId())
                .trainingCatalogueId(objective.getTrainingCatalogue().getId())
                .objectiveText(objective.getObjectiveText())
                .displayOrder(objective.getDisplayOrder())
                .active(objective.getActive())
                .creationDate(objective.getCreationDate())
                .modificationDate(objective.getModificationDate())
                .build();
    }
}