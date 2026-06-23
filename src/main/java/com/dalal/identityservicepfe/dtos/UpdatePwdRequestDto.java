package com.dalal.identityservicepfe.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdatePwdRequestDto(
        @NotBlank(message = "ce champs ne doit pas être vide")
        String oldPassword,
        @NotBlank(message = "ce champs ne doit pas être vide")
        @Pattern(
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,30}$",
                message = "Le mot de passe doit contenir entre 8 et 30 caractères, incluant une majuscule, une minuscule, un chiffre et un caractère spécial"
        )
        String newPassword,
        @NotBlank(message = "ce champs ne doit pas être vide")
        String confirmPassword
) {
}
