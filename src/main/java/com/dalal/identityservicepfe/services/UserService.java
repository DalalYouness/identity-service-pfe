package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.*;
import org.springframework.data.domain.Page;


import java.util.Map;

public interface UserService {
    AuthResponseDto register(RegisterRequestDto registerRequestDto) throws Exception;
    AuthResponseDto login(LoginRequestDto loginRequestDto) throws Exception;
    void updatePassword(UpdatePwdRequestDto updatePwdRequestDto,String email) ;
    AuthResponseDto changeEmail(ChangeEmailRequestDto changeEmailRequestDto, String email) throws Exception;
    void deleteAccount(String email);
    AuthResponseDto addAdministrator(RegisterRequestDto registerRequestDto) throws Exception;
    Page<UserProfileMinDto> getAllUsers(int page, int size);
    Page<PrestataireMinResponseDto> searchPrestatairesByName(String query, int page, int size);
    Page<PrestataireMinResponseDto> filterPrestatairesByCity(String city, int page, int size);
    PrestatairePublicDetailDto getPrestatairePublicWithoutContact(Long id);
    UserProfileResponseDto getAuthenticatedUserProfile(String email);
    UserProfileResponseDto updateAuthenticatedUserProfile(String email, UpdateProfileRequestDto dto);
    PrestataireAuthResponseDto getPrestataireDetailForClient(Long prestataireId);
    BecomePrestataireRespDto becomePrestataire(String email, BecomePrestataireDto becomePrestataireDto) throws Exception;
    void switchToClient(String email);
    //List<UserProfileMinDto> getProfilesByRole(String roleStr);
}
