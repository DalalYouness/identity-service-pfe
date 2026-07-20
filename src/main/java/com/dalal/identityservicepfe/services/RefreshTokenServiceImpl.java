package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.entities.RefreshToken;
import com.dalal.identityservicepfe.entities.User;
import com.dalal.identityservicepfe.exceptions.UserNotFoundException;
import com.dalal.identityservicepfe.repositories.RefreshTokenRepository;
import com.dalal.identityservicepfe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    // logout
    @Override
    public void logOutByRefreshToken(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("User non trouvé dans la base de données");
        }
        refreshTokenRepository.deleteByUser(user);
    }

    // for login and register
    @Override
    public RefreshToken createRefreshToken(User user) {

        refreshTokenRepository.deleteByUser(user);
        long refreshTokenDurationInSeconds = 2592000; // month in seconde
        Instant refreshTokenExpirationTime = Instant.now().plusSeconds(refreshTokenDurationInSeconds);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(refreshTokenExpirationTime)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {

        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }
}
