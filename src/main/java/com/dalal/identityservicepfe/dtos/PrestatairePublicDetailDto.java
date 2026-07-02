package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.enums.Gender;

public record PrestatairePublicDetailDto(
        Long id,
        String firstName,
        String lastName,
        Gender gender,
        String city,
        String country,
        String bio,
        String interventionArea
        // we can use the image here
) {}
