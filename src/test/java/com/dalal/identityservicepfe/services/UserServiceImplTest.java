package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterResponseDto;
import com.dalal.identityservicepfe.entities.ClientProfil;
import com.dalal.identityservicepfe.entities.Role;
import com.dalal.identityservicepfe.entities.User;
import com.dalal.identityservicepfe.enums.Gender;
import com.dalal.identityservicepfe.enums.RoleName;
import com.dalal.identityservicepfe.exceptions.EmailAlreadyExistsException;
import com.dalal.identityservicepfe.mappers.UserMapper;
import com.dalal.identityservicepfe.repositories.ProfilRepository;
import com.dalal.identityservicepfe.repositories.RoleRepository;
import com.dalal.identityservicepfe.repositories.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProfilRepository profilRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private  UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    //success test
    @Test
    void register() {
        //Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "youness", "dalal", "younessdalal@gmail.com", "dalalyouness1998",
                "0630524782", LocalDate.of(1998, Month.FEBRUARY, 27),
                Gender.MALE, "sidi maarouf", "morocco", "casablanca"
        );

        User mockUser = User.builder()
                .id(1L)
                .email(registerRequestDto.email())
                .password(registerRequestDto.password())
                .build();

        ClientProfil mockProfil = new ClientProfil();
        mockProfil.setId(mockUser.getId());
        mockProfil.setFirstName(registerRequestDto.firstName());
        mockProfil.setLastName(registerRequestDto.lastName());
        mockProfil.setAddress(registerRequestDto.address());
        mockProfil.setGender(registerRequestDto.gender());
        mockProfil.setBirthDate(registerRequestDto.birthDate());
        mockProfil.setCity(registerRequestDto.city());
        mockProfil.setCountry(registerRequestDto.country());
        mockProfil.setCountry(registerRequestDto.country());


        Mockito.when(userRepository.existsByEmail(registerRequestDto.email())).thenReturn(false);
        Mockito.when(passwordEncoder.encode(registerRequestDto.password())).thenReturn("hashed_password_123");
        Mockito.when(userMapper.toUserEntity(registerRequestDto)).thenReturn(mockUser);


        Mockito.when(userMapper.toClientProfilEntity(registerRequestDto)).thenReturn(mockProfil);


        Role role = Role.builder().roleName(RoleName.ROLE_CLIENT).build();
        Mockito.when(roleRepository.findByRoleName(RoleName.ROLE_CLIENT)).thenReturn(role);

        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(mockUser);
        Mockito.when(profilRepository.save(Mockito.any(ClientProfil.class))).thenReturn(mockProfil);


        var response = userService.register(registerRequestDto);

        String fullName = registerRequestDto.firstName() + " " + registerRequestDto.lastName();
        RegisterResponseDto registerResponseDto = new RegisterResponseDto(null,fullName,fullName + " enregistré avec succès.");

        Assertions.assertEquals(registerResponseDto,response);
    }

    @Test
    void registerFailureIfEmailAlreadyExists() {
        //Arrange
        //Arrange
        RegisterRequestDto registerRequestDto = new RegisterRequestDto(
                "youness", "dalal", "younessdalal@gmail.com", "dalalyouness1998",
                "0630524782", LocalDate.of(1998, Month.FEBRUARY, 27),
                Gender.MALE, "sidi maarouf", "morocco", "casablanca"
        );
        Mockito.when(userRepository.existsByEmail(registerRequestDto.email())).thenReturn(true);
        // Act & Assert
        Assertions.assertThrows(EmailAlreadyExistsException.class,
                ()-> userService.register(registerRequestDto)
                        ,"L'adresse email est déjà utilisée.");


    }

}

