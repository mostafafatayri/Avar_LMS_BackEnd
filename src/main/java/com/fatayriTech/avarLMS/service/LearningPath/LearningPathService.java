package com.fatayriTech.avarLMS.service.LearningPath;

import com.fatayriTech.avarLMS.enums.LearningPathCompletionType;
import com.fatayriTech.avarLMS.enums.LearningPathModule;
import com.fatayriTech.avarLMS.enums.LearningPathStatus;
import com.fatayriTech.avarLMS.model.LearningPath;
import com.fatayriTech.avarLMS.model.LearningPathItem;
import com.fatayriTech.avarLMS.model.TrainingCatalogue;
import com.fatayriTech.avarLMS.repository.LearningPathAssignmentRepo;
import com.fatayriTech.avarLMS.repository.LearningPathItemRepo;
import com.fatayriTech.avarLMS.repository.LearningPathRepo;
import com.fatayriTech.avarLMS.repository.TrainingCatalogueRepo;
import com.fatayriTech.avarLMS.request.learningPath.LearningPathItemRequest;
import com.fatayriTech.avarLMS.request.learningPath.LearningPathRequest;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathItemResponse;
import com.fatayriTech.avarLMS.response.learningPath.LearningPathResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningPathService {

    private final LearningPathRepo learningPathRepo;
    private final LearningPathItemRepo itemRepo;
    private final LearningPathAssignmentRepo assignmentRepo;
    private final TrainingCatalogueRepo trainingCatalogueRepo;

    public List<LearningPathResponse> getAll(Long organizationId) {
        return learningPathRepo
                .findByOrganizationIdAndActiveTrueOrderByCreationDateDesc(organizationId)
                .stream()
                .map(path -> mapPathToResponse(path, false))
                .toList();
    }

    public LearningPathResponse getById(Long organizationId, Long pathId) {
        LearningPath path = findPath(organizationId, pathId);
        return mapPathToResponse(path, true);
    }

    public List<LearningPathResponse> getParentLearningPaths(Long organizationId) {
        return learningPathRepo
                .findByOrganizationIdAndParentLearningPathIsNullAndActiveTrueOrderByCreationDateDesc(
                        organizationId
                )
                .stream()
                .map(path -> mapPathToResponse(path, false))
                .toList();
    }

    public LearningPathResponse create(Long organizationId, LearningPathRequest request) {
        validatePathRequest(request);

        if (learningPathRepo.existsByOrganizationIdAndNameIgnoreCaseAndActiveTrue(
                organizationId,
                request.getName().trim()
        )) {
            throw new RuntimeException("Learning path name already exists");
        }

        LearningPath parent = resolveParentLearningPath(
                organizationId,
                request.getParentLearningPathId(),
                null
        );

        LearningPathCompletionType completionType =
                request.getCompletionType() != null
                        ? request.getCompletionType()
                        : LearningPathCompletionType.PERCENTAGE;

        LearningPath path = LearningPath.builder()
                .organizationId(organizationId)
                .module(request.getModule() != null ? request.getModule() : LearningPathModule.L_AND_D)
                .name(request.getName().trim())
                .description(request.getDescription())
                .durationDays(request.getDurationDays() != null ? request.getDurationDays() : 0)
                .completionType(completionType)
                .completionPercentage(
                        completionType == LearningPathCompletionType.PERCENTAGE
                                ? request.getCompletionPercentage()
                                : null
                )
                .completionCount(
                        completionType == LearningPathCompletionType.SPECIFIC_COUNT
                                ? request.getCompletionCount()
                                : null
                )
                .lockingEnabled(Boolean.TRUE.equals(request.getLockingEnabled()))
                .status(request.getStatus() != null ? request.getStatus() : LearningPathStatus.DRAFT)
                .approvalRequired(request.getApprovalRequired() != null ? request.getApprovalRequired() : false)
                .parentLearningPath(parent)
                .displayOrder(request.getDisplayOrder() == null ? 0 : request.getDisplayOrder())
                .active(true)
                .build();

        return mapPathToResponse(learningPathRepo.save(path), true);
    }

    public LearningPathResponse update(
            Long organizationId,
            Long pathId,
            LearningPathRequest request
    ) {
        validatePathRequest(request);

        LearningPath path = findPath(organizationId, pathId);

        LearningPath parent = resolveParentLearningPath(
                organizationId,
                request.getParentLearningPathId(),
                pathId
        );

        LearningPathCompletionType completionType =
                request.getCompletionType() != null
                        ? request.getCompletionType()
                        : LearningPathCompletionType.PERCENTAGE;

        path.setModule(request.getModule() != null ? request.getModule() : LearningPathModule.L_AND_D);
        path.setName(request.getName().trim());
        path.setDescription(request.getDescription());
        path.setDurationDays(request.getDurationDays() != null ? request.getDurationDays() : 0);
        path.setCompletionType(completionType);

        if (completionType == LearningPathCompletionType.PERCENTAGE) {
            path.setCompletionPercentage(request.getCompletionPercentage());
            path.setCompletionCount(null);
        } else {
            path.setCompletionPercentage(null);
            path.setCompletionCount(request.getCompletionCount());
        }

        path.setLockingEnabled(Boolean.TRUE.equals(request.getLockingEnabled()));
        path.setStatus(request.getStatus() != null ? request.getStatus() : LearningPathStatus.DRAFT);
        path.setApprovalRequired(request.getApprovalRequired() != null ? request.getApprovalRequired() : false);
        path.setParentLearningPath(parent);
        path.setDisplayOrder(request.getDisplayOrder() == null ? 0 : request.getDisplayOrder());

        return mapPathToResponse(learningPathRepo.save(path), true);
    }

    public void delete(Long organizationId, Long pathId) {
        LearningPath path = findPath(organizationId, pathId);
        path.setActive(false);
        learningPathRepo.save(path);
    }

    public LearningPathItemResponse addItem(
            Long organizationId,
            Long pathId,
            LearningPathItemRequest request
    ) {
        LearningPath path = findPath(organizationId, pathId);

        if (request.getTrainingCatalogueId() == null) {
            throw new RuntimeException("Training catalogue id is required");
        }

        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationId(request.getTrainingCatalogueId(), organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        if (itemRepo.existsByOrganizationIdAndLearningPathIdAndTrainingCatalogueIdAndActiveTrue(
                organizationId,
                pathId,
                request.getTrainingCatalogueId()
        )) {
            throw new RuntimeException("Training already exists in this learning path");
        }

        LearningPathItem item = LearningPathItem.builder()
                .organizationId(organizationId)
                .learningPath(path)
                .trainingCatalogue(training)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .mandatory(request.getMandatory() != null ? request.getMandatory() : true)
                .lockUntilPreviousCompleted(
                        request.getLockUntilPreviousCompleted() != null
                                ? request.getLockUntilPreviousCompleted()
                                : false
                )
                .active(true)
                .build();

        return mapItemToResponse(itemRepo.save(item));
    }

    public LearningPathItemResponse updateItem(
            Long organizationId,
            Long pathId,
            Long itemId,
            LearningPathItemRequest request
    ) {
        LearningPathItem item = findItem(organizationId, pathId, itemId);

        item.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        item.setMandatory(request.getMandatory() != null ? request.getMandatory() : true);
        item.setLockUntilPreviousCompleted(
                request.getLockUntilPreviousCompleted() != null
                        ? request.getLockUntilPreviousCompleted()
                        : false
        );

        return mapItemToResponse(itemRepo.save(item));
    }

    public void deleteItem(Long organizationId, Long pathId, Long itemId) {
        LearningPathItem item = findItem(organizationId, pathId, itemId);
        item.setActive(false);
        itemRepo.save(item);
    }

    public List<LearningPathResponse> getSubPaths(Long organizationId, Long pathId) {
        findPath(organizationId, pathId);

        return learningPathRepo
                .findByOrganizationIdAndParentLearningPathIdAndActiveTrueOrderByDisplayOrderAscCreationDateAsc(
                        organizationId,
                        pathId
                )
                .stream()
                .map(path -> mapPathToResponse(path, false))
                .toList();
    }

    private LearningPathResponse mapPathToResponse(
            LearningPath path,
            boolean includeDetails
    ) {
        List<LearningPathItemResponse> items = List.of();

        if (includeDetails) {
            items = itemRepo
                    .findByOrganizationIdAndLearningPathIdAndActiveTrueOrderByDisplayOrderAsc(
                            path.getOrganizationId(),
                            path.getId()
                    )
                    .stream()
                    .map(this::mapItemToResponse)
                    .toList();
        }

        return LearningPathResponse.builder()
                .id(path.getId())
                .organizationId(path.getOrganizationId())
                .module(path.getModule())
                .name(path.getName())
                .description(path.getDescription())
                .durationDays(path.getDurationDays())
                .completionType(path.getCompletionType())
                .completionPercentage(path.getCompletionPercentage())
                .completionCount(path.getCompletionCount())
                .lockingEnabled(path.getLockingEnabled())
                .status(path.getStatus())
                .approvalRequired(path.getApprovalRequired())
                .displayOrder(path.getDisplayOrder())
                .parentLearningPathId(
                        path.getParentLearningPath() != null
                                ? path.getParentLearningPath().getId()
                                : null
                )
                .parentLearningPathName(
                        path.getParentLearningPath() != null
                                ? path.getParentLearningPath().getName()
                                : null
                )
                .subPathCount(
                        learningPathRepo.countByOrganizationIdAndParentLearningPathIdAndActiveTrue(
                                path.getOrganizationId(),
                                path.getId()
                        )
                )
                .active(path.getActive())
                .trainingCount(calculateTotalTrainingCount(path))
                .assignmentCount(
                        assignmentRepo.countByOrganizationIdAndLearningPathIdAndActiveTrue(
                                path.getOrganizationId(),
                                path.getId()
                        )
                )
                .creationDate(path.getCreationDate())
                .modificationDate(path.getModificationDate())
                .items(items)
                .assignments(List.of())
                .build();
    }

    private long calculateTotalTrainingCount(LearningPath path) {
        long directCount = itemRepo.countByOrganizationIdAndLearningPathIdAndActiveTrue(
                path.getOrganizationId(),
                path.getId()
        );

        long subPathTrainingCount = learningPathRepo
                .findByOrganizationIdAndParentLearningPathIdAndActiveTrueOrderByDisplayOrderAscCreationDateAsc(
                        path.getOrganizationId(),
                        path.getId()
                )
                .stream()
                .mapToLong(subPath ->
                        itemRepo.countByOrganizationIdAndLearningPathIdAndActiveTrue(
                                subPath.getOrganizationId(),
                                subPath.getId()
                        )
                )
                .sum();

        return directCount + subPathTrainingCount;
    }

    private LearningPathItemResponse mapItemToResponse(LearningPathItem item) {
        TrainingCatalogue training = item.getTrainingCatalogue();

        return LearningPathItemResponse.builder()
                .id(item.getId())
                .organizationId(item.getOrganizationId())
                .learningPathId(item.getLearningPath().getId())
                .trainingCatalogueId(training.getId())
                .trainingTitle(training.getTitle())
                .displayOrder(item.getDisplayOrder())
                .mandatory(item.getMandatory())
                .lockUntilPreviousCompleted(item.getLockUntilPreviousCompleted())
                .active(item.getActive())
                .creationDate(item.getCreationDate())
                .modificationDate(item.getModificationDate())
                .build();
    }

    private LearningPath resolveParentLearningPath(
            Long organizationId,
            Long parentLearningPathId,
            Long currentPathId
    ) {
        if (parentLearningPathId == null) {
            return null;
        }

        if (currentPathId != null && parentLearningPathId.equals(currentPathId)) {
            throw new RuntimeException("A learning path cannot be parent of itself");
        }

        return learningPathRepo
                .findByIdAndOrganizationIdAndActiveTrue(parentLearningPathId, organizationId)
                .orElseThrow(() -> new RuntimeException("Parent learning path not found"));
    }

    private LearningPath findPath(Long organizationId, Long pathId) {
        return learningPathRepo
                .findByIdAndOrganizationIdAndActiveTrue(pathId, organizationId)
                .orElseThrow(() -> new RuntimeException("Learning path not found"));
    }

    private LearningPathItem findItem(
            Long organizationId,
            Long pathId,
            Long itemId
    ) {
        return itemRepo
                .findByIdAndOrganizationIdAndLearningPathIdAndActiveTrue(
                        itemId,
                        organizationId,
                        pathId
                )
                .orElseThrow(() -> new RuntimeException("Learning path item not found"));
    }

    private void validatePathRequest(LearningPathRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new RuntimeException("Learning path name is required");
        }

        LearningPathCompletionType completionType =
                request.getCompletionType() != null
                        ? request.getCompletionType()
                        : LearningPathCompletionType.PERCENTAGE;

        if (completionType == LearningPathCompletionType.PERCENTAGE) {
            if (request.getCompletionPercentage() == null) {
                throw new RuntimeException("Completion percentage is required");
            }

            if (request.getCompletionPercentage() < 1 || request.getCompletionPercentage() > 100) {
                throw new RuntimeException("Completion percentage must be between 1 and 100");
            }
        }

        if (completionType == LearningPathCompletionType.SPECIFIC_COUNT) {
            if (request.getCompletionCount() == null || request.getCompletionCount() < 1) {
                throw new RuntimeException("Completion count is required");
            }
        }
    }
}