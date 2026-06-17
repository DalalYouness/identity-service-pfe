package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.LoginRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.AuthResponseDto;

public interface UserService {
    AuthResponseDto register(RegisterRequestDto registerRequestDto) throws Exception;
    AuthResponseDto login(LoginRequestDto loginRequestDto) throws Exception;
}
