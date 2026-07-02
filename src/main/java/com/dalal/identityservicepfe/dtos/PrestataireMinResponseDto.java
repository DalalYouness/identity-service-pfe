package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.enums.Gender;

public record PrestataireMinResponseDto(
        Long id,
        String firstName,
        String lastName,
        Gender gender,
        String city,
        String country
        //image if we want
) {
}
