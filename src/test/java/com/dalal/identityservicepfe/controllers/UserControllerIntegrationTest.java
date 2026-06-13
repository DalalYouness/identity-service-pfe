package com.dalal.identityservicepfe.controllers;

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
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.any(String.class)));
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
}
