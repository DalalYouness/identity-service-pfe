package com.dalal.identityservicepfe.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeEmailRequestDto(
        @NotBlank(message = "ce champs ne doit pas être vide")
        @Email(message = "L'adresse email n'est pas valide")
        @Size(max = 50, message = "L'email ne doit pas dépasser 50 caractères")
        String newEmail,
        @NotBlank(message = "ce champs ne doit pas être vide")
        String currentPassword
) {
}
