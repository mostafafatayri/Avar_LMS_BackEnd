package com.fatayriTech.avarLMS.repository.Employee;

import com.fatayriTech.avarLMS.model.EmployeeInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeInviteRepo extends JpaRepository<EmployeeInvite, Long> {
    Optional<EmployeeInvite> findByToken(String token);
}