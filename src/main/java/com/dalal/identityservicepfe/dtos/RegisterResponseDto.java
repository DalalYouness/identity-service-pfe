package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.entities.Role;

import java.util.List;
import java.util.Set;

public record RegisterResponseDto(
        String token,
        String email,
        String username,
        String message,
        Set<Role> roles,
        String expiresIn
) {
}
//done Alhamdulilah 👌
