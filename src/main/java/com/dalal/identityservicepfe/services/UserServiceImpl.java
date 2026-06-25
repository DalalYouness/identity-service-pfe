package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.*;
import com.dalal.identityservicepfe.entities.ClientProfil;
import com.dalal.identityservicepfe.entities.Role;
import com.dalal.identityservicepfe.entities.User;
import com.dalal.identityservicepfe.enums.RoleName;
import com.dalal.identityservicepfe.exceptions.EmailAlreadyExistsException;
import com.dalal.identityservicepfe.exceptions.InvalidPasswordException;
import com.dalal.identityservicepfe.exceptions.UserNotFoundException;
import com.dalal.identityservicepfe.mappers.UserMapper;
import com.dalal.identityservicepfe.repositories.ProfilRepository;
import com.dalal.identityservicepfe.repositories.RoleRepository;
import com.dalal.identityservicepfe.repositories.UserRepository;
import com.dalal.identityservicepfe.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ProfilRepository profilRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Value("${jwt.expiration}")
    private String expiresIn;

    @Override
    public AuthResponseDto register(RegisterRequestDto registerRequestDto) throws Exception {

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
        user.setFullName(fullName);

        //saving data in database
        userRepository.save(user);
        profilRepository.save(clientProfil);

        //generate token
        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDto(
                token,
                user.getEmail(),
                fullName,
                fullName + " enregistré avec succès.",
                user.getRoles(),
                expiresIn
        );
    }

    public AuthResponseDto login(LoginRequestDto loginRequestDto) throws Exception {
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.email(), loginRequestDto.password());
        Authentication authentication = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        User user = (User) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(user.getEmail());
        System.out.println("generated token in login : " + jwtToken );

        String fullName = user.getFullName();
        return new AuthResponseDto(jwtToken,user.getEmail(),fullName,"Connexion réussie avec succès.",user.getRoles(),expiresIn);


    }

    @Override
    public void updatePassword(UpdatePwdRequestDto updatePwdRequestDto, String email) {
        if (!updatePwdRequestDto.newPassword().equals(updatePwdRequestDto.confirmPassword())) {
            throw new IllegalArgumentException("Le nouveau mot de passe et sa confirmation ne correspondent pas.");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("user non trouvé");
        }

        if (!passwordEncoder.matches(updatePwdRequestDto.oldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(updatePwdRequestDto.newPassword()));
        userRepository.save(user);
    }

    // TODO:
    // 1. Remove checked exception (throws Exception)
    // 2. Add email verification for email change
    // 3. Add email verification during registration
    @Override
    public Map<String, String> changeEmail(ChangeEmailRequestDto changeEmailRequestDto, String email) throws Exception { // 👈 حيدنا throws Exception
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("user non trouvé");
        }

        if (userRepository.existsByEmail(changeEmailRequestDto.newEmail())) {
            throw new EmailAlreadyExistsException("Veuillez saisir une adresse email valide et disponible.");
        }

        if(!passwordEncoder.matches(changeEmailRequestDto.currentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Le mot de passe est incorrect.");
        }

        String newEmail = changeEmailRequestDto.newEmail();
        user.setEmail(newEmail);
        userRepository.save(user);

        String newJwtToken = jwtService.generateToken(newEmail);
        return Map.of("token", newJwtToken);
    }


}
