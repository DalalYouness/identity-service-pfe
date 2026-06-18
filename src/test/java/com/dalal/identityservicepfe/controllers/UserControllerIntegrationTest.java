package com.dalal.identityservicepfe.controllers;

import com.dalal.identityservicepfe.dtos.LoginRequestDto;
import com.dalal.identityservicepfe.dtos.RegisterRequestDto;
import com.dalal.identityservicepfe.enums.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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
@Transactional // Rolls back database changes after each test method to guarantee strict test isolation
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /*
    * *******************
    *   Registration
    * *******************
    * */
    @Test
    public void performRegisterUserSuccessfully() throws Exception {
        // 1 - Create a valid registration request object matching validation constraints
        RegisterRequestDto request = new RegisterRequestDto(
                "Dalal",
                "PFE",
                "dalal.success@example.com",
                "My_password1!",
                "0612345675",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "123 Rue de la Marche Verte",
                "Maroc",
                "Casablanca"
        );

        // 2 - Serialize the Java object into a JSON string
        String jsonRequestBody = objectMapper.writeValueAsString(request);

        // 3 - Simulate the HTTP POST request to the API endpoint
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))

                // 4 - Assert and verify the server response properties
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles").value(Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.any(String.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiresIn").exists());


    }

    @Test
    public void failureRegistrationIfEmailExist() throws Exception {
        // 1 - Create the payload that will be used twice to force a duplicate entry
        RegisterRequestDto setupRequest = new RegisterRequestDto(
                "Dalal",
                "PFE",
                "dalal.youness@example.com",
                "My_password1!",
                "0612345675",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "123 Rue de la Marche Verte",
                "Maroc",
                "Casablanca"
        );
        String setupJson = objectMapper.writeValueAsString(setupRequest);

        // 2 - Perform the first registration to populate the database inside this isolated context
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(setupJson))
                .andExpect(MockMvcResultMatchers.status().isCreated());

        // -------------------------------------------------------------

        // 3 - Attempt to register with the exact same email address a second time
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(setupJson))

                // 4 - Assert that the global exception handler intercepts the failure with a 409 Conflict status
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("L'adresse email est déjà utilisée."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error").value("Conflict"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.timestamp").exists());
    }

    //password as exemple (for testing validation)
    @Test
    public void failureRegistrationIfPasswordIsWeak() throws Exception {
        RegisterRequestDto setupRequest = new RegisterRequestDto(
                "Dalal",
                "PFE",
                "dalal.youness@example.com",
                "weak_password",
                "0612345675",
                LocalDate.of(2000, 1, 1),
                Gender.MALE,
                "123 Rue de la Marche Verte",
                "Maroc",
                "Casablanca"
        );
        String jsonObject = objectMapper.writeValueAsString(setupRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonObject))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    //Gender as an exemple of invalid format
    @Test
    public void failureRegistrationIfInvalidFormat() throws Exception {
        // 1 - We cannot use the RegisterRequestDto object directly here because Java won't allow an invalid Enum value.
        // Instead, we will write the raw JSON payload manually with an invalid gender value ("ROBOT").
        String invalidJsonPayload = """
                {
                    "firstName": "Dalal",
                    "lastName": "youness",
                    "email": "dalal.youness@example.com",
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
     *   login
     * *******************
     * */

    @Test
    public void login_ShouldReturnOKAndToken_WhenCredentialsAreValid() throws Exception {
        // using password directly is pretty normale because we are just testing
        LoginRequestDto loginRequest = new LoginRequestDto("dalal.dev2026@example.com"
        ,"SecurePassword1!");

        //converting the objet to a json
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        //make a post request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson)
                .accept(MediaType.APPLICATION_JSON)
        )  .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.any(String.class)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.roles").value(Matchers.notNullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiresIn").exists());


    }

    @Test
    public void login_ShouldReturn401Unauthorized_WhenCredentialsAreInvalid() throws Exception {
        LoginRequestDto loginRequest = new LoginRequestDto("dalal12@gmail.com", "myPassword123!");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.any(String.class)));
    }

    @Test
    public void login_ShouldReturn400BadRequest_WhenInputDataIsInvalid() throws Exception {

        LoginRequestDto loginRequest = new LoginRequestDto("", "");
        String loginJson = objectMapper.writeValueAsString(loginRequest);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
    //done Alhamdulilah 👌
}
