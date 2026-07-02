package com.dalal.identityservicepfe.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) // for avoiding everything null in json serialization
public record UserProfileResponseDto(
        Long id,
        String firstName,
        String lastName,
        String phoneNumber,
        String photo,
        String address,
        String city,
        String country,
        String bio,
        String interventionArea
) {}
