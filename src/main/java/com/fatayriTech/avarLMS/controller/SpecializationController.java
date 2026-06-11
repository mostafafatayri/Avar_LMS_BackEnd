package com.fatayriTech.avarLMS.controller;

import com.fatayriTech.avarLMS.request.SpecializationRequest;
import com.fatayriTech.avarLMS.response.SpecializationResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.SpecializationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/specializations")
@RequiredArgsConstructor
public class SpecializationController {

    private final SpecializationService specializationService;

    @GetMapping
    public List<SpecializationResponse> getAll() {
        return specializationService.getAll();
    }

    @GetMapping("/{id}")
    public SpecializationResponse getById(@PathVariable Long id) {
        return specializationService.getById(id);
    }

    @PostMapping
    public SpecializationResponse create(@RequestBody SpecializationRequest request) {
        return specializationService.create(request);
    }

    @PutMapping("/{id}")
    public SpecializationResponse update(
            @PathVariable Long id,
            @RequestBody SpecializationRequest request
    ) {
        return specializationService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public SpecializationResponse setActive(@PathVariable Long id) {
        return specializationService.setActive(id);
    }

    @PatchMapping("/{id}/inactive")
    public SpecializationResponse setInactive(@PathVariable Long id) {
        return specializationService.setInactive(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        specializationService.delete(id);
    }
}