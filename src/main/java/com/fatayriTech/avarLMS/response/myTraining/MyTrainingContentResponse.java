package com.fatayriTech.avarLMS.response.myTraining;

import com.fatayriTech.avarLMS.enums.TrainingDisplayItemType;
import com.fatayriTech.avarLMS.response.training.TrainingLectureAttachmentResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MyTrainingContentResponse {

    private Long trainingCatalogueId;

    private Long moduleId;

    private String moduleTitle;

    private Long moduleItemId;

    private TrainingDisplayItemType itemType;

    private Long itemRefId;

    private String title;

    private String description;

    private String videoUrl;

    private String videoType;

    private Integer durationMinutes;

    private List<TrainingLectureAttachmentResponse> attachments;

    private Integer passingScore;

    private Integer timeLimitMinutes;

    private Integer maxAttempts;

    private MyTrainingProgressResponse progress;
}