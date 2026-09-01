package com.dalal.identityservicepfe.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequestDto(
        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "Format de l'email invalide")
        String email,

        @NotBlank(message = "Le nouveau mot de passe est obligatoire")
        @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
        String newPassword,

        @NotBlank(message = "La confirmation du mot de passe est obligatoire")
        String confirmationPassword
) {}
