package com.dalal.identityservicepfe.mappers;

import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.UserProfileMinDto;
import com.dalal.identityservicepfe.entities.*;
import com.dalal.identityservicepfe.enums.RoleName;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUserEntity(RegisterRequestDto request);
    ClientProfil toClientProfilEntity(RegisterRequestDto request);
    AdminProfil toAdminProfilEntity(RegisterRequestDto request);

    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "gender", target = "gender")
    @Mapping(source = "user.accountStatus", target = "accountStatus")
    @Mapping(source = "user.roles", target = "roles")
    UserProfileMinDto toProfileMinDto(Profil profil);

    default Set<RoleName> mapRoles(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getRoleName)
                .collect(Collectors.toSet());
    }
}

