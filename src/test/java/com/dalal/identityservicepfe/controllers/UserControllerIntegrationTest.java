package com.dalal.identityservicepfe.controllers;

import com.dalal.identityservicepfe.dtos.LoginRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.dtos.UpdatePwdRequestDto;
import com.dalal.identityservicepfe.enums.Gender;
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

}