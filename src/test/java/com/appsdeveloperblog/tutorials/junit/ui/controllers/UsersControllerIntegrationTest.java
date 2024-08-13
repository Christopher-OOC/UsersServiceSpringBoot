package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.security.SecurityConstants;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
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

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-test.yml", properties = "server.port=8081")
public class UsersControllerIntegrationTest {

    @Value("${server.port}")
    private int serverPort;

    @LocalServerPort
    private int localServerPort;

    @Autowired
    private TestRestTemplate testRestTemplate;

    private String authorizationToken;

    @Order(1)
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

    @Order(2)
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

    @Order(3)
    @Test
    @DisplayName("/login works")
    void testUserLogin_whenValidCredentialsProvided_returnsJWTINAuthorizationHeader() throws JSONException {

        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "olojedechristopher@gmail.com");
        loginCredentials.put("password", "12345678");

        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString());

        //Act
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/users/login", request, null);
        authorizationToken = response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0);


        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getHeaders().getValuesAsList(SecurityConstants.HEADER_STRING).get(0), "Response should contain Authorization header with JWT");
        assertNotNull(response.getHeaders().getValuesAsList("UserID").get(0), "Response should contain UserID");
    }

    @Test
    @Order(4)
    @DisplayName("GET /users works")
    void testGetUsers_whenValidJWTProvided_returnsUsers() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBearerAuth(authorizationToken);

        HttpEntity requestEntity = new HttpEntity(headers);

        // Act
        ResponseEntity<List<UserRest>> response = testRestTemplate.exchange("/users", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<UserRest>>() {
        });

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "HTTP Status code should be 200");
        assertTrue(response.getBody().size() == 1, "There should be exartly one in the database");
    }
}
