package com.dalal.identityservicepfe.repositories;

import com.dalal.identityservicepfe.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
}
