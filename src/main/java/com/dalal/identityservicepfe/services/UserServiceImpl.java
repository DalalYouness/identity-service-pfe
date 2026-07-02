package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.*;
import com.dalal.identityservicepfe.entities.*;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
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
        if(role == null) {
            throw new RoleNotFoundException("Le rôle CLIENT est introuvable.");
        }
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

    @Override
    public void deleteAccount(String email) {
        User user = userRepository.findByEmail(email);
        if(user == null) {
            throw new UserNotFoundException("user non trouvé");
        }
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public AuthResponseDto addAdministrator(RegisterRequestDto registerRequestDto) throws Exception {
        if(userRepository.existsByEmail(registerRequestDto.email())) {
            throw new EmailAlreadyExistsException("L'adresse email est déjà utilisée.");
        }

        //user
        User user =  userMapper.toUserEntity(registerRequestDto);
        Role role = roleRepository.findByRoleName(RoleName.ROLE_ADMIN);
        if(role == null) {
            throw new RoleNotFoundException("Le rôle ADMIN est introuvable.");
        }
        user.getRoles().add(role);
        //hashing
        user.setPassword(passwordEncoder.encode(registerRequestDto.password()));

        // full name
        String fullName =  registerRequestDto.firstName() + " " + registerRequestDto.lastName();
        user.setFullName(fullName);

        //admin profil
        AdminProfil adminProfil = userMapper.toAdminProfilEntity(registerRequestDto);
        adminProfil.setUser(user);

        //save data
        userRepository.save(user);
        profilRepository.save(adminProfil);

        AuthResponseDto authResponseDto = AuthResponseDto.builder()
                .roles(user.getRoles())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .expiresIn(expiresIn)
                .message(fullName + " enregistré avec succès.")
                .build();

        return authResponseDto;
    }

    @Override
    public Page<UserProfileMinDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Profil> profilsPage = profilRepository.findAll(pageable);
        return profilsPage.map(userMapper::toProfileMinDto);
    }


    @Override
    public Page<PrestataireMinResponseDto> searchPrestatairesByName(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<PrestataireProfil> prestataires = profilRepository.searchByFullName(query, pageable);
        return prestataires.map(userMapper::toPrestataireMinDto);
    }

    @Override
    public Page<PrestataireMinResponseDto> filterPrestatairesByCity(String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<PrestataireProfil> prestataires = profilRepository.
                findByCityIgnoreCase(city, pageable);
        return prestataires.map(userMapper::toPrestataireMinDto);
    }

    @Override
    public PrestatairePublicDetailDto getPrestatairePublicWithoutContact(Long id) {

        Profil profil = profilRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Prestataire non trouvé"));

        if (!(profil instanceof PrestataireProfil prestataireProfil)) {
            throw new IllegalArgumentException("Ce profil n'est pas un prestataire");
        }
        return userMapper.toPrestatairePublicDetailDto(prestataireProfil);
    }

    @Override
    public UserProfileResponseDto getAuthenticatedUserProfile(String email) {

        Profil profil = profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("utilisateur non trouvé"));

        if (profil instanceof PrestataireProfil prestataireProfil) {
            return userMapper.toUserProfileDto(prestataireProfil);
        } else if (profil instanceof ClientProfil clientProfil) {
            return userMapper.toUserProfileDto(clientProfil);
        } else if (profil instanceof  AdminProfil admin) {
            return userMapper.toUserProfileDto(admin);
        }

        throw new IllegalArgumentException("Type de profil inconnu");
    }

    @Override
    public UserProfileResponseDto updateAuthenticatedUserProfile(String email, UpdateProfileRequestDto dto) {

        Profil profil = profilRepository.findByUserEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé"));

        profil.setFirstName(dto.firstName());
        profil.setLastName(dto.lastName());
        profil.setPhoneNumber(dto.phoneNumber());
        profil.setAddress(dto.address());
        profil.setCity(dto.city());
        profil.setCountry(dto.country());

        if (profil instanceof PrestataireProfil prestataireProfil) {

            prestataireProfil.setInterventionArea(dto.interventionArea());

            PrestataireProfil updated = profilRepository.save(prestataireProfil);

            return userMapper.toUserProfileDto(updated);

        } else if (profil instanceof ClientProfil clientProfil) {

            clientProfil.setBio(dto.bio());
            ClientProfil updated = profilRepository.save(clientProfil);
            return userMapper.toUserProfileDto(updated);

        } else if (profil instanceof AdminProfil adminProfil) {

            AdminProfil updated = profilRepository.save(adminProfil);
            return userMapper.toUserProfileDto(updated);
        }

        throw new IllegalArgumentException("Type de profil inconnu");
    }

    @Override
    public PrestataireAuthResponseDto getPrestataireDetailForClient(Long prestataireId) {
        Profil profil = profilRepository.findById(prestataireId).orElseThrow(
                () -> new UserNotFoundException("Prestataire non trouvé")
        );
        if(!(profil instanceof PrestataireProfil prestataireProfil)) {
            throw new IllegalArgumentException("Ce profil n'est pas un prestataire");
        }
        return userMapper.toPrestataireAuthDetailDto(prestataireProfil);

    }

}
