package com.dalal.identityservicepfe.mappers;

import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.entities.AdminProfil;
import com.dalal.identityservicepfe.entities.ClientProfil;
import com.dalal.identityservicepfe.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUserEntity(RegisterRequestDto request);
    ClientProfil toClientProfilEntity(RegisterRequestDto request);
    AdminProfil toAdminProfilEntity(RegisterRequestDto request);
}
//done Alhamdulilah 👌
