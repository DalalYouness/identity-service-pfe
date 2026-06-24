package com.dalal.identityservicepfe.controllers;

import com.dalal.identityservicepfe.dtos.LoginRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.AuthResponseDto;
import com.dalal.identityservicepfe.dtos.UpdatePwdRequestDto;
import com.dalal.identityservicepfe.exceptions.UserNotFoundException;
import com.dalal.identityservicepfe.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) throws Exception {
        AuthResponseDto registerResponseDto = userService.register(registerRequestDto);
        return new ResponseEntity<>(registerResponseDto, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) throws Exception {
        AuthResponseDto response = userService.login(loginRequestDto);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update-password")
    @PreAuthorize("isAuthenticated()") //any role is accepted just hhe have to be authenticated
    public ResponseEntity<Map<String,String>> updatePassword(@RequestBody @Valid UpdatePwdRequestDto updatePwdRequestDto, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        userService.updatePassword(updatePwdRequestDto,username);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifié avec succès !"));
    }

}
