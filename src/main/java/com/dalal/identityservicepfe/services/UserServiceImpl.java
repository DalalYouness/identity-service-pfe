package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterResponseDto;
import com.dalal.identityservicepfe.entities.ClientProfil;
import com.dalal.identityservicepfe.entities.Role;
import com.dalal.identityservicepfe.entities.User;
import com.dalal.identityservicepfe.enums.RoleName;
import com.dalal.identityservicepfe.exceptions.EmailAlreadyExistsException;
import com.dalal.identityservicepfe.mappers.UserMapper;
import com.dalal.identityservicepfe.repositories.ProfilRepository;
import com.dalal.identityservicepfe.repositories.RoleRepository;
import com.dalal.identityservicepfe.repositories.UserRepository;
import com.dalal.identityservicepfe.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfilRepository profilRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Value("${jwt.expiration}")
    private String expiresIn;

    @Override
    @Transactional
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) throws Exception {

        //check if email already exist
        if(userRepository.existsByEmail(registerRequestDto.email())) {
            throw new EmailAlreadyExistsException("L'adresse email est déjà utilisée.");
        }

        //mapping
        User user = userMapper.toUserEntity(registerRequestDto);
        ClientProfil clientProfil = userMapper.toClientProfilEntity(registerRequestDto);

        //hashing password
        user.setPassword(passwordEncoder.encode(registerRequestDto.password()));

        //setting relationnel data
        Role role = roleRepository.findByRoleName(RoleName.ROLE_CLIENT);
        user.getRoles().add(role);
        clientProfil.setUser(user);

        //creating username
        String fullName = registerRequestDto.firstName() + " " + registerRequestDto.lastName();
        user.setUsername(fullName);

        //saving data in database
        userRepository.save(user);
        profilRepository.save(clientProfil);

        //generate token
        String token = jwtService.generateToken(fullName);

        return new RegisterResponseDto(
                token,
                user.getEmail(),
                fullName,
                fullName + " enregistré avec succès.",
                user.getRoles(),
                expiresIn
        );
    }


}
