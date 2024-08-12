package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.yml", properties = "server.port=8081")
public class UsersControllerIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidDetailsProvided_returnsUserDetails() throws JSONException {
        //Arrange
        JSONObject user = new JSONObject();
        user.put("firstName", "Christopher");
        user.put("lastName", "Olojede");
        user.put("email", "olojedechristopher@gmail.com");
        user.put("password", "12345678");
        user.put("repeatPassword", "12345678");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(user.toString(), headers);

        //Act
        ResponseEntity<UserRest> stringResponseEntity = testRestTemplate.postForEntity("/users", request, UserRest.class);
        UserRest createdUserDetails = stringResponseEntity.getBody();

        //Assert
        assertEquals(HttpStatus.OK, stringResponseEntity.getStatusCode());
        assertEquals(user.getString("firstName"), createdUserDetails.getFirstName(), "Returned user's first name seems to be incorrect!");
        assertEquals(user.getString("lastName"), createdUserDetails.getLastName(), "Returned user's last name seems to be incorrect!");
        assertEquals(user.getString("email"), createdUserDetails.getEmail(), "Returned user's email seems to be incorrect!");
        assertFalse(createdUserDetails.getUserId().trim().isEmpty(), "User id should not be empty!");
    }

    @Test
    @DisplayName("GET /users requires JWT")
    void testGetUsers_whenMissingJWT_return403() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity requestEntity = new HttpEntity(headers);
        
        // Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users", HttpMethod.GET, requestEntity,
                new ParameterizedTypeReference<List<UserRest>>() {});


        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(), "HTTP Status code 403 Forbidden should have been returned");


    }
}
