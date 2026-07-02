package com.dalal.identityservicepfe.dtos;

public record UpdateProfileRequestDto(
        String firstName,
        String lastName,
        String phoneNumber,
        String address,
        String city,
        String country,
        String bio,
        String interventionArea
) {
}
