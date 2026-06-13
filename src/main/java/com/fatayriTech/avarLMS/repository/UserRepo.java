package com.fatayriTech.avarLMS.repository;

import com.fatayriTech.avarLMS.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmailOrUsername(String email, String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<User> findByRefreshToken(String refreshToken);

    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<User> searchUsers(@Param("keyword") String keyword);

    List<User> findByDomainId(Long domainId);
}