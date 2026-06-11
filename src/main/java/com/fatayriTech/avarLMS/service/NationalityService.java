package com.fatayriTech.avarLMS.service;

import com.fatayriTech.avarLMS.exceptions.AlreadyExistsException;
import com.fatayriTech.avarLMS.exceptions.ResourceNotFoundException;
import com.fatayriTech.avarLMS.model.Nationality;
import com.fatayriTech.avarLMS.repository.NationalityRepo;
import com.fatayriTech.avarLMS.request.NationalityRequest;
import com.fatayriTech.avarLMS.response.NationalityResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NationalityService {

    private final NationalityRepo nationalityRepo;

    public NationalityResponse createNationality(NationalityRequest request) {
        if (nationalityRepo.existsByNameIgnoreCase(request.getName())) {
            throw new AlreadyExistsException("Nationality already exists with name: " + request.getName());
        }

        if (nationalityRepo.existsByCodeIgnoreCase(request.getCode())) {
            throw new AlreadyExistsException("Nationality already exists with code: " + request.getCode());
        }

        Nationality nationality = new Nationality();
        nationality.setName(request.getName());
        nationality.setCode(request.getCode());
        nationality.setActive(request.isActive());

        return mapToResponse(nationalityRepo.save(nationality));
    }

    public List<NationalityResponse> getAllNationalities() {
        return nationalityRepo.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public NationalityResponse getNationalityById(Long id) {
        Nationality nationality = nationalityRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nationality not found"));

        return mapToResponse(nationality);
    }

    public NationalityResponse updateNationality(Long id, NationalityRequest request) {
        Nationality nationality = nationalityRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nationality not found"));

        nationality.setName(request.getName());
        nationality.setCode(request.getCode());
        nationality.setActive(request.isActive());

        return mapToResponse(nationalityRepo.save(nationality));
    }

    public void deleteNationality(Long id) {
        Nationality nationality = nationalityRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Nationality not found"));

        nationalityRepo.delete(nationality);
    }

    private NationalityResponse mapToResponse(Nationality nationality) {
        return new NationalityResponse(
                nationality.getId(),
                nationality.getName(),
                nationality.getCode(),
                nationality.isActive()
        );
    }
}