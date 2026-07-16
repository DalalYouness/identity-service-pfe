package com.dalal.identityservicepfe.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;


@Entity
@DiscriminatorValue("PRESTATAIRE")
@Getter
@Setter
@NoArgsConstructor
public class PrestataireProfil extends Profil {
}
