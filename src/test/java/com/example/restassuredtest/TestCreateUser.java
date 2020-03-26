package com.example.restassuredtest;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestCreateUser {

    private final String CONTEXT_PATH = "/mobile-app-ws";

    @BeforeEach
    void setUp() throws Exception {
        RestAssured.baseURI="http://localhost";
        RestAssured.port=8080;
    }

    @Test
    public void testCreateUser() throws Exception {

        List<Map<String, Object>> userAddresses = new ArrayList<>();

        Map<String, Object> shippingAddress = new HashMap<>();
        shippingAddress.put("city", "Moscow");
        shippingAddress.put("country", "Russia");
        shippingAddress.put("streetName", "Volobueva");
        shippingAddress.put("postalCode", "464");
        shippingAddress.put("type", "shipping");

        Map<String, Object> billingAddress = new HashMap<>();
        billingAddress.put("city", "Moscow");
        billingAddress.put("country", "Russia");
        billingAddress.put("streetName", "Volobueva");
        billingAddress.put("postalCode", "464");
        billingAddress.put("type", "billing");

        userAddresses.add(shippingAddress);

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("firstName", "andrew");
        userDetails.put("lastName", "Popov");
        userDetails.put("email", "popov@gmail.com");
        userDetails.put("password", "1234");
        userDetails.put("addresses", userAddresses);

        Response response = given()
                .contentType("application/json")
                .accept("application/json")
                .body(userDetails)
                .when()
                .post(CONTEXT_PATH + "/users")
                .then()
                .statusCode(200)
                .contentType("application/json")
                .extract()
                .response();

        String userId = response.jsonPath().getString("userId");

        assertNotNull(userId);

        String body = response.getBody().asString();
        JSONObject responseBody = new JSONObject(body);
        JSONArray addresses = responseBody.getJSONArray("addresses");

        assertNotNull(addresses);
        assertEquals(2, addresses.length());

        String addressId = addresses.getJSONObject(0).getString("addressId");
        assertEquals(30, addressId.length());
    }
}
