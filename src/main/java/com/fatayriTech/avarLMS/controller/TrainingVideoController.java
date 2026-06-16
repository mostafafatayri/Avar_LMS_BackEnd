package com.fatayriTech.avarLMS.controller.Training;

import com.fatayriTech.avarLMS.request.training.TrainingVideoRequest;
import com.fatayriTech.avarLMS.response.training.TrainingVideoResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingVideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/videos")
public class TrainingVideoController {

    private final TrainingVideoService trainingVideoService;

    @GetMapping
    public List<TrainingVideoResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return trainingVideoService.getAll(organizationId, trainingId);
    }

    @PostMapping
    public TrainingVideoResponse create(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingVideoRequest request
    ) {
        return trainingVideoService.create(organizationId, trainingId, request);
    }

    @PutMapping("/{videoId}")
    public TrainingVideoResponse update(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long videoId,
            @RequestBody TrainingVideoRequest request
    ) {
        return trainingVideoService.update(
                organizationId,
                trainingId,
                videoId,
                request
        );
    }

    @DeleteMapping("/{videoId}")
    public void delete(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long videoId
    ) {
        trainingVideoService.delete(
                organizationId,
                trainingId,
                videoId
        );
    }

    @PostMapping("/{videoId}/upload")
    public TrainingVideoResponse upload(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @PathVariable Long videoId,
            @RequestParam("file") MultipartFile file
    ) {
        return trainingVideoService.uploadVideo(
                organizationId,
                trainingId,
                videoId,
                file
        );
    }
}