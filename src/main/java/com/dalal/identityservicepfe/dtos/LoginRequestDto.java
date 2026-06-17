package com.dalal.identityservicepfe.dtos;

import jakarta.validation.constraints.NotBlank;

// at this point we have to check just the not blank validation
public record LoginRequestDto(
        @NotBlank(message = "L'adresse email est obligatoire")
        String email,
        @NotBlank(message = "Le mot de passe est obligatoire")
        String password
        ) { }
