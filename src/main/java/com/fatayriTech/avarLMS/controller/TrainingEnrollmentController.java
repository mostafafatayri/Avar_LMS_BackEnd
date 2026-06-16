package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.training.TrainingEnrollmentRequest;
import com.fatayriTech.avarLMS.response.training.TrainingEnrollmentResponse;
import com.fatayriTech.avarLMS.service.Training.TrainingEnrollmentService;
import com.fatayriTech.avarLMS.service.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/training-catalogue/{trainingId}/enrollments")
public class TrainingEnrollmentController {

    private final TrainingEnrollmentService enrollmentService;

    @GetMapping
    public List<TrainingEnrollmentResponse> getAll(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId
    ) {
        return enrollmentService.getAll(organizationId, trainingId);
    }

    @GetMapping("/my")
    public TrainingEnrollmentResponse getMyEnrollment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return enrollmentService.getMyEnrollment(
                organizationId,
                trainingId,
                currentUser
        );
    }

    @PostMapping("/my")
    public TrainingEnrollmentResponse enrollMe(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return enrollmentService.enroll(
                organizationId,
                trainingId,
                currentUser
        );
    }

    @PutMapping("/my/status")
    public TrainingEnrollmentResponse updateMyStatus(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @RequestBody TrainingEnrollmentRequest request,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        return enrollmentService.updateMyStatus(
                organizationId,
                trainingId,
                request,
                currentUser
        );
    }

    @DeleteMapping("/my")
    public void cancelMyEnrollment(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @PathVariable Long trainingId,
            @AuthenticationPrincipal CurrentUser currentUser
    ) {
        enrollmentService.cancelMyEnrollment(
                organizationId,
                trainingId,
                currentUser
        );
    }
}