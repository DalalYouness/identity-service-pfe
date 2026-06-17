package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.enums.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterRequestDto(
        @NotBlank(message = "Le prénom est obligatoire")
        @Size(min = 2, max = 30, message = "Le prénom doit contenir entre 2 et 30 caractères")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        @Size(min = 2, max = 30, message = "Le nom doit contenir entre 2 et 30 caractères")
        String lastName,

        @NotBlank(message = "L'adresse email est obligatoire")
        @Email(message = "L'adresse email n'est pas valide")
        @Size(max = 50, message = "L'email ne doit pas dépasser 50 caractères")
        String email,

        @NotBlank(message = "Le mot de passe ne doit pas être vide")
        @Pattern(
                regexp = "^(?=.*[0-8])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,30}$",
                message = "Le mot de passe doit contenir entre 8 et 30 caractères, incluant une majuscule, une minuscule, un chiffre et un caractère spécial"
        )
        String password,

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        @Pattern(
                regexp = "^(\\+212|0)([5-7])\\d{8}$",
                message = "Le numéro de téléphone n'est pas valide (Format marocain attendu)"
        )
        String phoneNumber,

        @NotNull(message = "La date de naissance est obligatoire")
        @Past(message = "La date de naissance doit être dans le passé")
        LocalDate birthDate,

        @NotNull(message = "Le genre est obligatoire")
        Gender gender,

        @NotBlank(message = "L'adresse est obligatoire")
        @Size(min = 5, max = 150, message = "L'adresse doit contenir entre 5 et 150 caractères")
        String address,

        @NotBlank(message = "Le pays est obligatoire")
        @Size(min = 2, max = 50, message = "Le pays doit contenir entre 2 et 50 caractères")
        String country,

        @NotBlank(message = "La ville est obligatoire")
        @Size(min = 2, max = 50, message = "La ville doit contenir entre 2 et 50 caractères")
        String city

) {
}
//done Alhamdulilah 👌