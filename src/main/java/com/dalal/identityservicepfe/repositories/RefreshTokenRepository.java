package com.dalal.identityservicepfe.repositories;

import com.dalal.identityservicepfe.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    void deleteByUser(User user);
}
