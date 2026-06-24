package com.dalal.identityservicepfe.services;

import com.dalal.identityservicepfe.dtos.LoginRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.AuthResponseDto;
import com.dalal.identityservicepfe.dtos.UpdatePwdRequestDto;
import com.dalal.identityservicepfe.entities.ClientProfil;
import com.dalal.identityservicepfe.entities.Role;
import com.dalal.identityservicepfe.entities.User;
import com.dalal.identityservicepfe.enums.Gender;
import com.dalal.identityservicepfe.enums.RoleName;
import com.dalal.identityservicepfe.exceptions.EmailAlreadyExistsException;
import com.dalal.identityservicepfe.exceptions.InvalidPasswordException;
import com.dalal.identityservicepfe.exceptions.UserNotFoundException;
import com.dalal.identityservicepfe.mappers.UserMapper;
import com.dalal.identityservicepfe.repositories.ProfilRepository;
import com.dalal.identityservicepfe.repositories.RoleRepository;
import com.dalal.identityservicepfe.repositories.UserRepository;
import com.dalal.identityservicepfe.security.JwtService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Month;
import java.util.Set;

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
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserServiceImpl userService;

    //***********
    // registration
    //***********

    //success test
    @Test
    void register() throws Exception {

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

        Mockito.when(jwtService.generateToken(Mockito.anyString())).thenReturn("my-token");

        var response = userService.register(registerRequestDto);

        String fullName = registerRequestDto.firstName() + " " + registerRequestDto.lastName();
        AuthResponseDto registerResponseDto = new AuthResponseDto("my-token",mockUser.getEmail(),fullName,fullName + " enregistré avec succès.", Set.of(role),null);

        Assertions.assertEquals(registerResponseDto,response);
    }
    @Test
    void registerFailureIfEmailAlreadyExists() {
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


    //***********
    // login
    //***********

    @Test
    public void loginSuccess_ShouldReturnAuthResponseDto_WhenCredentialsAreValid() throws Exception {

        LoginRequestDto loginRequestDto = new LoginRequestDto("sofiadouaa18@gmail.com", "sofiaDouaa@188");

        User mockUser = User.builder()
                .id(1L)
                .email(loginRequestDto.email())
                .password(loginRequestDto.password())
                .fullName("youness dalal")
                .roles(null)
                .build();

        // if we will use a mock just inside some methods it's enough to create it inside the methode directly
        Authentication mockAuthentication = Mockito.mock(Authentication.class);
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(mockAuthentication);

        Mockito.when(mockAuthentication.getPrincipal()).thenReturn(mockUser);
        Mockito.when(jwtService.generateToken(mockUser.getEmail())).thenReturn("my-token");

        var loginResponse = userService.login(loginRequestDto);

        AuthResponseDto expectedResponse = new AuthResponseDto(
                "my-token",
                loginRequestDto.email(),
                "youness dalal",
                "Connexion réussie avec succès.",
                null,
                null
        );

        Assertions.assertEquals(expectedResponse, loginResponse);
    }

    @Test
    public void loginFailure_ShouldThrowAuthenticationException_WhenCredentialsAreInvalid() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("sofiadouaa18@gmail.com", "sofiaDouaa@188");
        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        Assertions.assertThrows(AuthenticationException.class , () -> userService.login(loginRequestDto));
    }

    @Test
    public void updatePasswordSuccess() {

        UpdatePwdRequestDto updatePwdRequestDto = new UpdatePwdRequestDto("Dalal_1998!","Youness@98","Youness@98");
        String email = "younessdalal1@gmail.com";
        User user = User.builder().email("younessdalal1@gmail.com").password("hashed_password").build();

        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(updatePwdRequestDto.oldPassword(), user.getPassword())).thenReturn(true);
        Mockito.when(passwordEncoder.encode(updatePwdRequestDto.newPassword())).thenReturn("new_hashed_password");
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);

        userService.updatePassword(updatePwdRequestDto,email);

        Assertions.assertEquals("new_hashed_password",user.getPassword());

        Mockito.verify(userRepository, Mockito.times(1)).save(user);
    }

    @Test
    public void updatePasswordFailure_ShouldThrowInvalidPasswordException_WhenOldPasswordIsInvalid() {
        UpdatePwdRequestDto updatePwdRequestDto = new UpdatePwdRequestDto("Dalal_1998!","Youness@98","Youness@98");
        String email = "younessdalal1@gmail.com";
        User user = User.builder().email("younessdalal1@gmail.com").password("hashed_password").build();

        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(updatePwdRequestDto.oldPassword(), user.getPassword())).thenReturn(false);


        /* for info
        the message in that methode is not the exception message, but it's the message who will be displayed in the console if the test not passed so the correct alg is that
        Assertions.assertThrows(InvalidPasswordException.class,() -> userService.updatePassword(updatePwdRequestDto,email),"Ancien mot de pwd incorrect");*/
        InvalidPasswordException invalidPasswordException = Assertions.assertThrows(InvalidPasswordException.class,() -> userService.updatePassword(updatePwdRequestDto, email));
        Assertions.assertEquals("Ancien mot de passe incorrect", invalidPasswordException.getMessage());
        // it's necessary to verify that mock userRepository never call the save methode
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));

    }

    @Test
    public void updatePasswordFailure_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {

        UpdatePwdRequestDto updatePwdRequestDto = new UpdatePwdRequestDto("Dalal_1998!", "Youness@98", "Youness@98");
        String email = "unknown_user@gmail.com";

        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        UserNotFoundException exception = Assertions.assertThrows(UserNotFoundException.class, () -> {
            userService.updatePassword(updatePwdRequestDto, email);
        });

        Assertions.assertEquals("user non trouvé", exception.getMessage());

        Mockito.verifyNoInteractions(passwordEncoder);
        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any(User.class));
    }

}



