package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterResponseDto;

public interface UserService {
    RegisterResponseDto register(RegisterRequestDto registerRequestDto) throws Exception;
}
