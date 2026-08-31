package com.chethani.personalization;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class SecurityIntegrationTest extends AbstractIntegrationTest {

    private static final String SHOPPER_SHELF_API = "/api/shopper-shelf";

    @Test
    void shouldRejectMalformedToken() {
        RestAssured.given()
                .header("Authorization", "Bearer this-is-not-a-real-jwt")
                .contentType(ContentType.JSON)
                .when()
                .get(SHOPPER_SHELF_API)
                .then()
                .statusCode(401)
                .body("message", Matchers.equalTo("Unauthorized"))
                .body("errors", Matchers.contains("Authentication is required to access this resource."));
    }

    @Test
    void shouldRejectTokenWithTamperedSignature() {
        String tamperedToken = tamperSignature(dataTeamServiceToken);

        RestAssured.given()
                .header("Authorization", "Bearer " + tamperedToken)
                .contentType(ContentType.JSON)
                .when()
                .get(SHOPPER_SHELF_API)
                .then()
                .statusCode(401)
                .body("message", Matchers.equalTo("Unauthorized"))
                .body("errors", Matchers.contains("Authentication is required to access this resource."));
    }

    // Replaces the signature with garbage so verification reliably fails.
    private String tamperSignature(String token) {
        String[] parts = token.split("\\.");
        return parts[0] + "." + parts[1] + ".invalidSignatureValue123";
    }

}
