package com.dalal.identityservicepfe.controllers;

import com.dalal.identityservicepfe.dtos.LoginRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.AuthResponseDto;
import com.dalal.identityservicepfe.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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

}
