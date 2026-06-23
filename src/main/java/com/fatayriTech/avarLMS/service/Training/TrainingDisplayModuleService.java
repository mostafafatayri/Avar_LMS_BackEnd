package com.fatayriTech.avarLMS.service.Training;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import com.fatayriTech.avarLMS.model.*;
import com.fatayriTech.avarLMS.repository.*;
import com.fatayriTech.avarLMS.request.training.TrainingDisplayModuleItemRequest;
import com.fatayriTech.avarLMS.request.training.TrainingDisplayModuleRequest;
import com.fatayriTech.avarLMS.response.training.TrainingDisplayModuleItemResponse;
import com.fatayriTech.avarLMS.response.training.TrainingDisplayModuleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingDisplayModuleService {

    private final TrainingCatalogueRepo trainingCatalogueRepo;
    private final TrainingDisplayModuleRepo moduleRepo;
    private final TrainingDisplayModuleItemRepo itemRepo;

    private final TrainingLectureRepo lectureRepo;
    private final TrainingVideoRepo videoRepo;
    private final TrainingQuizRepo quizRepo;

    public List<TrainingDisplayModuleResponse> getModules(
            Long organizationId,
            Long trainingId
    ) {
        return moduleRepo
                .findByOrganizationIdAndTrainingCatalogueIdAndActiveTrueOrderByDisplayOrderAsc(
                        organizationId,
                        trainingId
                )
                .stream()
                .map(this::mapModule)
                .toList();
    }

    @Transactional
    public TrainingDisplayModuleResponse createModule(
            Long organizationId,
            Long trainingId,
            TrainingDisplayModuleRequest request
    ) {
        TrainingCatalogue training = trainingCatalogueRepo
                .findByIdAndOrganizationIdAndActiveTrue(trainingId, organizationId)
                .orElseThrow(() -> new RuntimeException("Training not found"));

        TrainingDisplayModule module = TrainingDisplayModule.builder()
                .organizationId(organizationId)
                .trainingCatalogue(training)
                .title(request.getTitle())
                .description(request.getDescription())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .active(true)
                .build();

        TrainingDisplayModule savedModule = moduleRepo.save(module);

        if (request.getItems() != null) {
            saveItems(organizationId, trainingId, savedModule, request.getItems());
        }

        return mapModule(savedModule);
    }

    @Transactional
    public TrainingDisplayModuleResponse updateModule(
            Long organizationId,
            Long trainingId,
            Long moduleId,
            TrainingDisplayModuleRequest request
    ) {
        TrainingDisplayModule module = findModule(organizationId, trainingId, moduleId);

        module.setTitle(request.getTitle());
        module.setDescription(request.getDescription());
        module.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);

        TrainingDisplayModule savedModule = moduleRepo.save(module);

        if (request.getItems() != null) {
            List<TrainingDisplayModuleItem> oldItems =
                    itemRepo.findByOrganizationIdAndModuleIdAndActiveTrueOrderByDisplayOrderAsc(
                            organizationId,
                            savedModule.getId()
                    );

            oldItems.forEach(item -> item.setActive(false));
            itemRepo.saveAll(oldItems);

            saveItems(organizationId, trainingId, savedModule, request.getItems());
        }

        return mapModule(savedModule);
    }

    public void deleteModule(
            Long organizationId,
            Long trainingId,
            Long moduleId
    ) {
        TrainingDisplayModule module = findModule(organizationId, trainingId, moduleId);
        module.setActive(false);
        moduleRepo.save(module);
    }

    @Transactional
    public TrainingDisplayModuleItemResponse addItem(
            Long organizationId,
            Long trainingId,
            Long moduleId,
            TrainingDisplayModuleItemRequest request
    ) {
        TrainingDisplayModule module = findModule(organizationId, trainingId, moduleId);

        validateReferencedItem(
                organizationId,
                trainingId,
                request.getItemType(),
                request.getItemRefId()
        );

        TrainingDisplayModuleItem item = TrainingDisplayModuleItem.builder()
                .organizationId(organizationId)
                .module(module)
                .itemType(request.getItemType())
                .itemRefId(request.getItemRefId())
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .required(request.getRequired() == null || request.getRequired())
                .active(true)
                .build();

        return mapItem(itemRepo.save(item));
    }

    @Transactional
    public TrainingDisplayModuleItemResponse updateItem(
            Long organizationId,
            Long trainingId,
            Long moduleId,
            Long itemId,
            TrainingDisplayModuleItemRequest request
    ) {
        findModule(organizationId, trainingId, moduleId);

        TrainingDisplayModuleItem item = itemRepo
                .findByIdAndOrganizationIdAndModuleIdAndActiveTrue(
                        itemId,
                        organizationId,
                        moduleId
                )
                .orElseThrow(() -> new RuntimeException("Module item not found"));

        validateReferencedItem(
                organizationId,
                trainingId,
                request.getItemType(),
                request.getItemRefId()
        );

        item.setItemType(request.getItemType());
        item.setItemRefId(request.getItemRefId());
        item.setDisplayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0);
        item.setRequired(request.getRequired() == null || request.getRequired());

        return mapItem(itemRepo.save(item));
    }

    public void deleteItem(
            Long organizationId,
            Long trainingId,
            Long moduleId,
            Long itemId
    ) {
        findModule(organizationId, trainingId, moduleId);

        TrainingDisplayModuleItem item = itemRepo
                .findByIdAndOrganizationIdAndModuleIdAndActiveTrue(
                        itemId,
                        organizationId,
                        moduleId
                )
                .orElseThrow(() -> new RuntimeException("Module item not found"));

        item.setActive(false);
        itemRepo.save(item);
    }

    private void saveItems(
            Long organizationId,
            Long trainingId,
            TrainingDisplayModule module,
            List<TrainingDisplayModuleItemRequest> requests
    ) {
        for (TrainingDisplayModuleItemRequest request : requests) {
            validateReferencedItem(
                    organizationId,
                    trainingId,
                    request.getItemType(),
                    request.getItemRefId()
            );

            TrainingDisplayModuleItem item = TrainingDisplayModuleItem.builder()
                    .organizationId(organizationId)
                    .module(module)
                    .itemType(request.getItemType())
                    .itemRefId(request.getItemRefId())
                    .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                    .required(request.getRequired() == null || request.getRequired())
                    .active(true)
                    .build();

            itemRepo.save(item);
        }
    }

    private TrainingDisplayModule findModule(
            Long organizationId,
            Long trainingId,
            Long moduleId
    ) {
        return moduleRepo
                .findByIdAndOrganizationIdAndTrainingCatalogueIdAndActiveTrue(
                        moduleId,
                        organizationId,
                        trainingId
                )
                .orElseThrow(() -> new RuntimeException("Module not found"));
    }

    private void validateReferencedItem(
            Long organizationId,
            Long trainingId,
            TrainingDisplayItemType itemType,
            Long itemRefId
    ) {
        if (itemType == null) {
            throw new RuntimeException("Item type is required");
        }

        if (itemRefId == null) {
            throw new RuntimeException("Item reference is required");
        }

        if (itemType == TrainingDisplayItemType.LECTURE) {
            lectureRepo.findByIdAndOrganizationIdAndTrainingCatalogueId(
                    itemRefId,
                    organizationId,
                    trainingId
            ).orElseThrow(() -> new RuntimeException("Lecture not found"));
            return;
        }

        if (itemType == TrainingDisplayItemType.VIDEO) {
            videoRepo.findByIdAndOrganizationIdAndTrainingCatalogueId(
                    itemRefId,
                    organizationId,
                    trainingId
            ).orElseThrow(() -> new RuntimeException("Video not found"));
            return;
        }

        if (itemType == TrainingDisplayItemType.QUIZ) {
            quizRepo.findByIdAndOrganizationIdAndTrainingCatalogueId(
                    itemRefId,
                    organizationId,
                    trainingId
            ).orElseThrow(() -> new RuntimeException("Quiz not found"));
        }
    }

    private TrainingDisplayModuleResponse mapModule(TrainingDisplayModule module) {
        List<TrainingDisplayModuleItemResponse> items = itemRepo
                .findByOrganizationIdAndModuleIdAndActiveTrueOrderByDisplayOrderAsc(
                        module.getOrganizationId(),
                        module.getId()
                )
                .stream()
                .map(this::mapItem)
                .toList();

        return TrainingDisplayModuleResponse.builder()
                .id(module.getId())
                .organizationId(module.getOrganizationId())
                .trainingCatalogueId(module.getTrainingCatalogue().getId())
                .title(module.getTitle())
                .description(module.getDescription())
                .displayOrder(module.getDisplayOrder())
                .active(module.getActive())
                .items(items)
                .creationDate(module.getCreationDate())
                .modificationDate(module.getModificationDate())
                .build();
    }

    private TrainingDisplayModuleItemResponse mapItem(TrainingDisplayModuleItem item) {
        return TrainingDisplayModuleItemResponse.builder()
                .id(item.getId())
                .organizationId(item.getOrganizationId())
                .moduleId(item.getModule().getId())
                .itemType(item.getItemType())
                .itemRefId(item.getItemRefId())
                .itemTitle(resolveItemTitle(item))
                .itemDescription(resolveItemDescription(item))
                .displayOrder(item.getDisplayOrder())
                .required(item.getRequired())
                .active(item.getActive())
                .creationDate(item.getCreationDate())
                .modificationDate(item.getModificationDate())
                .build();
    }

    private String resolveItemTitle(TrainingDisplayModuleItem item) {
        if (item.getItemType() == TrainingDisplayItemType.LECTURE) {
            return lectureRepo.findById(item.getItemRefId())
                    .map(TrainingLecture::getTitle)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.VIDEO) {
            return videoRepo.findById(item.getItemRefId())
                    .map(TrainingVideo::getTitle)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.QUIZ) {
            return quizRepo.findById(item.getItemRefId())
                    .map(TrainingQuiz::getTitle)
                    .orElse("-");
        }

        return "-";
    }

    private String resolveItemDescription(TrainingDisplayModuleItem item) {
        if (item.getItemType() == TrainingDisplayItemType.LECTURE) {
            return lectureRepo.findById(item.getItemRefId())
                    .map(TrainingLecture::getDescription)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.VIDEO) {
            return videoRepo.findById(item.getItemRefId())
                    .map(TrainingVideo::getDescription)
                    .orElse("-");
        }

        if (item.getItemType() == TrainingDisplayItemType.QUIZ) {
            return quizRepo.findById(item.getItemRefId())
                    .map(TrainingQuiz::getDescription)
                    .orElse("-");
        }

        return "-";
    }
}