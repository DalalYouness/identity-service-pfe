package com.dalal.identityservicepfe.dtos;

import com.dalal.identityservicepfe.enums.AccountStatus;
import com.dalal.identityservicepfe.enums.Gender;
import com.dalal.identityservicepfe.enums.RoleName;

import java.util.Set;

public record UserProfileMinDto(
        String firstName,
        String lastName,
        Gender gender,
        AccountStatus accountStatus,
        Set<RoleName> roles
) {}
