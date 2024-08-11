package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@WebMvcTest(controllers = UsersController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class})
//@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUserDetailsProvided_returnsCreatedUserDetails() throws Exception {
        // Arrange
        UserDetailsRequestModel user = new UserDetailsRequestModel();
        user.setFirstName("Christopher");
        user.setLastName("Olojede");
        user.setEmail("olojedechristopher@gmail.com");
        user.setPassword("chris1234");
        user.setRepeatPassword("chris1234");



        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType("application/json")
                .accept("application/json")
                .content(new ObjectMapper().writeValueAsString(user));

        //Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        UserRest createdUser = new ObjectMapper().readValue(contentAsString, UserRest.class);

        //Assert
        assertEquals(user.getFirstName(), createdUser.getFirstName(), "The returned user name is most likely incorrect");
        assertEquals(user.getEmail(), createdUser.getEmail(), "The returned user email is incorrect");
        assertFalse(createdUser.getUserId().isEmpty(), "userId should not be empty");
    }
}
