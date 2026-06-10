package com.dalal.identityservicepfe.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;


@Entity
@DiscriminatorValue("PRESTATAIRE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PrestataireProfil extends Profil {

    @Column(name = "intervention_area")
    private String interventionArea;
}