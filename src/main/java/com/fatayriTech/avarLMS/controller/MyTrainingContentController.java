package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.response.myTraining.MyTrainingContentResponse;
import com.fatayriTech.avarLMS.response.myTraining.MyTrainingProgressResponse;
import com.fatayriTech.avarLMS.request.training.MarkTrainingItemProgressRequest;
import com.fatayriTech.avarLMS.service.Training.MyTrainingContentService;
import com.fatayriTech.avarLMS.request.training.SubmitMyTrainingQuizRequest;
import com.fatayriTech.avarLMS.response.myTraining.MyTrainingQuizResponse;
import com.fatayriTech.avarLMS.response.myTraining.MyTrainingQuizSubmitResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/my-training-content")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MyTrainingContentController {

    private final MyTrainingContentService service;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{moduleItemId}")
    public MyTrainingContentResponse getContent(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long moduleItemId
    ) {
        return service.getContent(
                organizationId,
                userId,
                moduleItemId
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/{moduleItemId}/progress")
    public MyTrainingProgressResponse markProgress(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Employee-Id", required = false) Long employeeId,
            @PathVariable Long moduleItemId,
            @RequestBody MarkTrainingItemProgressRequest request
    ) {
        return service.markProgress(
                organizationId,
                userId,
                employeeId,
                moduleItemId,
                request
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{moduleItemId}/quiz")
    public MyTrainingQuizResponse getQuiz(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long moduleItemId
    ) {
        return service.getQuiz(organizationId, userId, moduleItemId);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{moduleItemId}/quiz/submit")
    public MyTrainingQuizSubmitResponse submitQuiz(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader(value = "X-Employee-Id", required = false) Long employeeId,
            @PathVariable Long moduleItemId,
            @RequestBody SubmitMyTrainingQuizRequest request
    ) {
        return service.submitQuiz(
                organizationId,
                userId,
                employeeId,
                moduleItemId,
                request
        );
    }
}