package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.enums.RoleName;

import java.util.Set;

public record BecomePrestataireRespDto(
        String message,
        String token,
        Set<RoleName> roles
) {
}
