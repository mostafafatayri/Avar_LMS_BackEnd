package com.fatayriTech.avarLMS.repository;



import com.fatayriTech.avarLMS.model.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepo extends JpaRepository<Organization, Long> {
    boolean existsBySlug(String slug);

}