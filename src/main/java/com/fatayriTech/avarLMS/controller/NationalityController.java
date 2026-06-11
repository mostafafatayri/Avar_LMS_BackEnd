package com.fatayriTech.avarLMS.controller;


import com.fatayriTech.avarLMS.request.NationalityRequest;
import com.fatayriTech.avarLMS.response.NationalityResponse;
import com.fatayriTech.avarLMS.service.NationalityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/nationalities")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class NationalityController {

    private final NationalityService nationalityService;

    @PostMapping
    public NationalityResponse createNationality(@RequestBody NationalityRequest request) {
        return nationalityService.createNationality(request);
    }

    @GetMapping
    public List<NationalityResponse> getAllNationalities() {
        return nationalityService.getAllNationalities();
    }

    @GetMapping("/{id}")
    public NationalityResponse getNationalityById(@PathVariable Long id) {
        return nationalityService.getNationalityById(id);
    }

    @PutMapping("/{id}")
    public NationalityResponse updateNationality(
            @PathVariable Long id,
            @RequestBody NationalityRequest request
    ) {
        return nationalityService.updateNationality(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteNationality(@PathVariable Long id) {
        nationalityService.deleteNationality(id);
    }
}