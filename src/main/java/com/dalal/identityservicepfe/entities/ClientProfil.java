package com.dalal.identityservicepfe.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;


@Entity
@DiscriminatorValue("CLIENT")
@Getter
@Setter
@NoArgsConstructor
public class ClientProfil extends Profil {

    @Override
    public String getActiveMode() {
        return "CLIENT";
    }
}
//done Alhamdulilah 👌