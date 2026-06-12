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

import java.time.LocalDate;

@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void performRegisterUserSuccessfully() throws Exception {
        //1 - request object
        RegisterRequestDto request = new RegisterRequestDto(
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

        //2 - java object to json object
        String jsonRequestBody = objectMapper.writeValueAsString(request);

        //3 - acting as an http client
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequestBody))

                //4 - server response verification
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(Matchers.nullValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(Matchers.any(String.class)));
    }
}