package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.response.myTraining.MyTrainingDetailsResponse;
import com.fatayriTech.avarLMS.response.myTraining.MyTrainingResponse;
import com.fatayriTech.avarLMS.service.Training.MyTrainingService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/my-trainings/view")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class MyTrainingController {

    private final MyTrainingService myTrainingService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public List<MyTrainingResponse> getMyTrainings(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId
    ) {
        return myTrainingService.getMyTrainings(organizationId, userId);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{trainingId}/details")
    public MyTrainingDetailsResponse getMyTrainingDetails(
            @RequestHeader("X-Organization-Id") Long organizationId,
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long trainingId
    ) {
        return myTrainingService.getMyTrainingDetails(
                organizationId,
                userId,
                trainingId
        );
    }
}