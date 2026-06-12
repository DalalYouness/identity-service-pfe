package com.dalal.identityservicepfe.dtos;

public record RegisterResponseDto(
        String token,
        String username,
        String message
) {
}
