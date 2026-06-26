package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.entities.Role;
import lombok.Builder;

import java.util.Set;

@Builder
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
