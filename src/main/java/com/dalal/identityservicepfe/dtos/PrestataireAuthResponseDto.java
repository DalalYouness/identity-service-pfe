package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.enums.Gender;

public record PrestataireAuthResponseDto(
        Long id,
        String firstName,
        String lastName,
        Gender gender,
        String city,
        String country,
        String address,
        String bio,
        String interventionArea
) {}