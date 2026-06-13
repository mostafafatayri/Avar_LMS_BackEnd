package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DomainRepo extends JpaRepository<Domain, Long> {

    Optional<Domain> findByDomainIgnoreCase(String domain);

    boolean existsByDomainIgnoreCase(String domain);
}