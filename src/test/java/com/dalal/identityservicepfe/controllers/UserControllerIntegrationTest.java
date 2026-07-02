package com.dalal.identityservicepfe.controllers;

import com.dalal.identityservicepfe.dtos.ChangeEmailRequestDto;
import com.dalal.identityservicepfe.dtos.LoginRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.UpdatePwdRequestDto;
import com.dalal.identityservicepfe.enums.Gender;
import com.dalal.identityservicepfe.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;


@AutoConfigureMockMvc
@SpringBootTest
@Transactional // Rolls back database changes after each test method
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;


    private RegisterRequestDto sharedUserRequest;
    private String sharedUserJson;

    @BeforeEach
    public void setUp() throws Exception {
        sharedUserRequest = new RegisterRequestDto(
                "Dalal",
                "Dev",
                "dalal.youness@gmail.com",
                "SecurePassword1!",
                "0612345675",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "123 Rue de la Marche Verte",
                "Maroc",
                "Casablanca"
        );
        sharedUserJson = objectMapper.writeValueAsString(sharedUserRequest);
    }

    /*
     * *******************
     * Registration
     * *******************
     * */
    @Test
    public void performRegisterUserSuccessfully() throws Exception {

        RegisterRequestDto successRequest = new RegisterRequestDto(
                "Dalal", "PFE", "dalal.success@example.com", "My_password1!",
                "0612345675", LocalDate.of(2000, 1, 1), Gender.MALE,
                "123 Rue de la Marche Verte", "Maroc", "Casablanca"
        );
        String jsonRequestBody = objectMapper.writeValueAsString(successRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.notNullValue()));
    }

    @Test
    public void failureRegistrationIfEmailExist() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sharedUserJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sharedUserJson))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("L'adresse email est déjà utilisée."));
    }

    @Test
    public void failureRegistrationIfPasswordIsWeak() throws Exception {
        RegisterRequestDto weakPwdRequest = new RegisterRequestDto(
                "Dalal", "PFE", "dalal.weak@example.com", "weak_password",
                "0612345675", LocalDate.of(2000, 1, 1), Gender.MALE,
                "123 Rue de la Marche Verte", "Maroc", "Casablanca"
        );
        String jsonObject = objectMapper.writeValueAsString(weakPwdRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void failureRegistrationIfInvalidFormat() throws Exception {
        String invalidJsonPayload = """
                {
                    "firstName": "Dalal",
                    "lastName": "youness",
                    "email": "dalal.invalid@example.com",
                    "password": "My_password1!",
                    "phoneNumber": "0612345675",
                    "birthDate": "2000-01-01",
                    "gender": "ROBOT",
                    "address": "123 Rue de la Marche Verte",
                    "country": "Maroc",
                    "city": "Casablanca"
                }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /*
     * *******************
     * Login
     * *******************
     * */
    @Test
    public void login_ShouldReturnOKAndToken_WhenCredentialsAreValid() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sharedUserJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        LoginRequestDto loginRequest = new LoginRequestDto(sharedUserRequest.email(), sharedUserRequest.password());
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(sharedUserRequest.email()));
    }

    @Test
    public void login_ShouldReturn401Unauthorized_WhenCredentialsAreInvalid() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("unknown.user@gmail.com", "myPassword123!");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void login_ShouldReturn400BadRequest_WhenInputDataIsInvalid() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("", "");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    /*
    * *****************
    * update password
    * *****************
    * */
    @Test
    public void updatePassword_ShouldReturn200_WhenTokenAndPasswordIsValid() throws Exception {
        // registration first
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                .content(sharedUserJson)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated()).andReturn();
        String contentAsString = result.getResponse().getContentAsString();
        String token = JsonPath.read(contentAsString, "$.token");

        UpdatePwdRequestDto updatePwdRequestDto = new UpdatePwdRequestDto("SecurePassword1!"
                ,"DalalSec1!",
                "DalalSec1!");
        String updatePasswordJson =  objectMapper.writeValueAsString(updatePwdRequestDto);
        //update password
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth/update-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatePasswordJson)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.notNullValue()));

    }

    @Test
    public void updatePassword_ShouldReturn400BadRequest_WhenOldPasswordIsIncorrect() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String token = JsonPath.read(contentAsString, "$.token");

        UpdatePwdRequestDto updatePwdRequestDto = new UpdatePwdRequestDto(
                "WrongPassword123!",
                "DalalSec1!",
                "DalalSec1!"
        );
        String updatePasswordJson = objectMapper.writeValueAsString(updatePwdRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordJson)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))


                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.notNullValue()));
    }

    @Test
    public void updatePassword_ShouldReturn400_WhenNewPasswordAndConfirmPasswordDoNotMatch() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String token = JsonPath.read(contentAsString, "$.token");

        UpdatePwdRequestDto updatePwdRequestDto = new UpdatePwdRequestDto(
                "SecurePassword1!",
                "DalalSec_1!",
                "MismatchedPassword123!"
        );
        String updatePasswordJson = objectMapper.writeValueAsString(updatePwdRequestDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth/update-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatePasswordJson)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())

                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.notNullValue()));
    }

    /*
     * *************************
     * change email
     * *************************
     * */

    @Test
    public void changeEmail_ShouldReturnOKAndNewToken_WhenDataIsValid() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String token = JsonPath.read(contentAsString, "$.token");

        ChangeEmailRequestDto changeEmailDto = new ChangeEmailRequestDto(
                "dalal.new.email@gmail.com",
                "SecurePassword1!"
        );
        String changeEmailJson = objectMapper.writeValueAsString(changeEmailDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changeEmailJson)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.notNullValue()));
    }

    @Test
    public void changeEmail_ShouldReturnBadRequest_WhenPasswordIsIncorrect() throws Exception {

        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String token = JsonPath.read(contentAsString, "$.token");

        ChangeEmailRequestDto changeEmailDto = new ChangeEmailRequestDto(
                "dalal.new.email@gmail.com",
                "WrongPassword123!"
        );
        String changeEmailJson = objectMapper.writeValueAsString(changeEmailDto);


        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changeEmailJson)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void changeEmail_ShouldReturnBadRequest_WhenNewEmailAlreadyExists() throws Exception {

        var resultUser = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String tokenUser = JsonPath.read(resultUser.getResponse().getContentAsString(), "$.token");

        ChangeEmailRequestDto changeEmailDto = new ChangeEmailRequestDto(
                "dalal.youness@gmail.com",
                "SecurePassword1!"
        );
        String changeEmailJson = objectMapper.writeValueAsString(changeEmailDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/auth/change-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(changeEmailJson)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenUser))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    /*
     * *************************
     * delete account
     * *************************
     * */

    @Test
    public void deleteAccount_ShouldReturnNoContent_WhenUserIsAuthenticated() throws Exception {
        // 1. Register
        var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        String token = JsonPath.read(contentAsString, "$.token");


        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/auth/delete-account")
                        .header("Authorization", "Bearer " + token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

    }

    /*
    * **********************
    *   Add admin
    * **********************
    * */
    @Test
    public void addAdministrator_ShouldReturnCreated_WhenCallerIsAdmin() throws Exception {

        var registerResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String adminToken = JsonPath.read(registerResult.getResponse().getContentAsString(), "$.token");

        RegisterRequestDto newAdminDto = new RegisterRequestDto(
                "Youness",
                "Admin",
                "youness.admin@gmail.com",
                "SecurePassword1!",
                "0611223344",
                LocalDate.of(1998, 5, 12),
                Gender.MALE,
                "Anfa Street",
                "Maroc",
                "Casablanca"
        );
        String newAdminJson = objectMapper.writeValueAsString(newAdminDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/add-administrator")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newAdminJson)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated()) // 👈 التّأكيد على 201 Created
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("youness.admin@gmail.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Youness Admin enregistré avec succès."));
    }
    /*
     * *****************************
     * get All users (for admin)
     * *****************************
     * */

    @Test
    public void getAllUsers_ShouldReturnOKAndPagedUsers_WhenCallerIsAdmin() throws Exception {
       // I didn't use sharedUser because i would to use some different data for admin
        RegisterRequestDto adminRegisterDto = new RegisterRequestDto(
                "Youness", "Dalal", "admin.younes@enset.com", "SecurePassword1!",
                "0611223399", LocalDate.of(1998, 1,1), Gender.MALE,
                "SM casablana", "Maroc", "Casablanca"
        );
        String adminJsonPayload = objectMapper.writeValueAsString(adminRegisterDto);


        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/add-administrator")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(adminJsonPayload))
                .andExpect(MockMvcResultMatchers.status().isCreated());


        LoginRequestDto loginRequest = new LoginRequestDto("admin.younes@enset.com", "SecurePassword1!");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        var loginResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String adminToken = JsonPath.read(loginResult.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.pageable.pageSize").value(5));
    }
    @Test
    public void getAllUsers_ShouldReturn403Forbidden_WhenCallerIsNotAdmin() throws Exception {

        var registerResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .content(sharedUserJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        String clientToken = JsonPath.read(registerResult.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/auth/users")
                        .header("Authorization", "Bearer " + clientToken)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))


                .andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}