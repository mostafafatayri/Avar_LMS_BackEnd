package com.fatayriTech.avarLMS.controller;



import com.fatayriTech.avarLMS.request.SubTeamRequest;
import com.fatayriTech.avarLMS.response.SubTeamResponse;
import com.fatayriTech.avarLMS.service.WorkStructure.SubTeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/sub-teams")
@RequiredArgsConstructor
public class SubTeamController {

    private final SubTeamService subTeamService;

    @GetMapping
    public List<SubTeamResponse> getAll() {
        return subTeamService.getAll();
    }

    @GetMapping("/{id}")
    public SubTeamResponse getById(@PathVariable Long id) {
        return subTeamService.getById(id);
    }

    @PostMapping
    public SubTeamResponse create(@RequestBody SubTeamRequest request) {
        return subTeamService.create(request);
    }

    @PutMapping("/{id}")
    public SubTeamResponse update(
            @PathVariable Long id,
            @RequestBody SubTeamRequest request
    ) {
        return subTeamService.update(id, request);
    }

    @PatchMapping("/{id}/active")
    public SubTeamResponse setActive(@PathVariable Long id) {
        return subTeamService.setActive(id);
    }

    @PatchMapping("/{id}/inactive")
    public SubTeamResponse setInactive(@PathVariable Long id) {
        return subTeamService.setInactive(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        subTeamService.delete(id);
    }
}