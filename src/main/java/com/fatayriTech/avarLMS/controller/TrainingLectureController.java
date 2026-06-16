package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.TrainingLectureRequest;
import com.fatayriTech.avarLMS.response.training.TrainingLectureAttachmentResponse;
import com.fatayriTech.avarLMS.response.training.TrainingLectureResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingLectureService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/lectures")
@RequiredArgsConstructor
public class TrainingLectureController {

    private final TrainingLectureService trainingLectureService;

    @GetMapping
    public List<TrainingLectureResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return trainingLectureService.getAll(organizationId, trainingId);
    }

    @PostMapping
    public TrainingLectureResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingLectureRequest request
    ) {
        return trainingLectureService.create(organizationId, trainingId, request);
    }

    @PutMapping("/{lectureId}")
    public TrainingLectureResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long lectureId,
            @RequestBody TrainingLectureRequest request
    ) {
        return trainingLectureService.update(
                organizationId,
                trainingId,
                lectureId,
                request
        );
    }

    @DeleteMapping("/{lectureId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long lectureId
    ) {
        trainingLectureService.delete(organizationId, trainingId, lectureId);
    }

    @PostMapping("/{lectureId}/attachments")
    public TrainingLectureAttachmentResponse uploadAttachment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long lectureId,
            @RequestParam("file") MultipartFile file
    ) {
        return trainingLectureService.uploadAttachment(
                organizationId,
                trainingId,
                lectureId,
                file
        );
    }

    @DeleteMapping("/{lectureId}/attachments/{attachmentId}")
    public void deleteAttachment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long lectureId,
            @PathVariable Long attachmentId
    ) {
        trainingLectureService.deleteAttachment(
                organizationId,
                trainingId,
                lectureId,
                attachmentId
        );
    }
}