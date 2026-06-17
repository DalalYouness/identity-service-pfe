package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.entities.Role;

import java.util.Set;

public record AuthResponseDto(
        String token,
        String email,
        String fullName,
        String message,
        Set<Role> roles,
        String expiresIn
) {
}
//done Alhamdulilah 👌
