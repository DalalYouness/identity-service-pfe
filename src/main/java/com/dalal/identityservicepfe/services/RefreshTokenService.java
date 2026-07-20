package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.entities.RefreshToken;
import com.dalal.identityservicepfe.entities.User;

public interface RefreshTokenService {
    void logOutByRefreshToken(User user);
    RefreshToken createRefreshToken(User user);
    RefreshToken verifyExpiration(RefreshToken token);
}
