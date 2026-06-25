package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.*;
import com.dalal.identityservicepfe.exceptions.UserNotFoundException;

import java.util.Map;

public interface UserService {
    AuthResponseDto register(RegisterRequestDto registerRequestDto) throws Exception;
    AuthResponseDto login(LoginRequestDto loginRequestDto) throws Exception;
    void updatePassword(UpdatePwdRequestDto updatePwdRequestDto,String email) ;
    //void for a while
    Map<String ,String> changeEmail(ChangeEmailRequestDto changeEmailRequestDto, String email) throws Exception;
}
