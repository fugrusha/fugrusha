package com.example.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserWebServiceEndpointTest {

    private final String CONTEXT_PATH = "/mobile-app-ws";
    private final String EMAIL = "test@gmail.com";
    private final String JSON = "application/json";
    private static String authorization;
    private static String userId;

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Order(1)
    @Test
    public void testUserLogin() throws Exception {
        Map<String, String> loginCredentials = new HashMap<>();
        loginCredentials.put("email", "test@test.com");
        loginCredentials.put("password", EMAIL);

        Response response = given()
                .contentType(JSON)
                .accept(JSON)
                .body(loginCredentials)
                .when()
                .post(CONTEXT_PATH + "/users/login")
                .then()
                .statusCode(200).extract().response();

        authorization = response.header("Authorization");
        userId = response.header("UserID");

        assertNotNull(authorization);
        assertNotNull(userId);
    }

    @Order(2)
    @Test
    public void testGetUserDetails() throws Exception {
        Map<String, String> loginCredentials = new HashMap<>();
        loginCredentials.put("email", "test@test.com");
        loginCredentials.put("password", EMAIL);

        Response response = given()
                .pathParam("id", userId)
                .header("Authorization", authorization)
                .contentType(JSON)
                .accept(JSON)
                .body(loginCredentials)
                .when()
                .get(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200).extract().response();

        String userPublicId = response.jsonPath().getString("userId");
        String email = response.jsonPath().getString("email");
        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        List<Map<String, String>> addresses = response.jsonPath().getList("addresses");
        String addressId = addresses.get(0).get("addressId");

        assertNotNull(userPublicId);
        assertNotNull(email);
        assertNotNull(firstName);
        assertNotNull(lastName);

        assertEquals(EMAIL, email);
        assertEquals(2, addresses.size());
        assertEquals(30, addressId.length());
    }

    @Order(3)
    @Test
    public void testUpdateUserDetails() throws Exception {
        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "New name");
        userDetails.put("lastName", "New last name");

        Response response = given()
                .pathParam("id", userId)
                .header("Authorization", authorization)
                .contentType(JSON)
                .accept(JSON)
                .body(userDetails)
                .when()
                .put(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200).extract().response();

        String firstName = response.jsonPath().getString("firstName");
        String lastName = response.jsonPath().getString("lastName");
        List<Map<String, String>> addresses = response.jsonPath().getList("addresses");

        assertEquals("New name", firstName);
        assertEquals("New last name", lastName);
        assertEquals(2, addresses.size());
    }

    @Order(4)
    @Test
    public void testDeleteUserDetails() throws Exception {

        Response response = given()
                .pathParam("id", userId)
                .header("Authorization", authorization)
                .contentType(JSON)
                .accept(JSON)
                .when()
                .delete(CONTEXT_PATH + "/users/{id}")
                .then()
                .statusCode(200).extract().response();

        String operationResult = response.jsonPath().getString("operationResult");
        String operationName = response.jsonPath().getString("OperationName");

        assertEquals("SUCCESS", operationResult);
        assertEquals("DELETE", operationName);
    }
}
