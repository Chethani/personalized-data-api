package com.chethani.personalization;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class SecurityIntegrationTest extends AbstractIntegrationTest {

    private static final String PRODUCT_METADATA_API = "/api/product-metadata";

    @Test
    void shouldRejectMalformedToken() {
        RestAssured.given()
                .header("Authorization", "Bearer this-is-not-a-real-jwt")
                .contentType(ContentType.JSON)
                .when()
                .get(PRODUCT_METADATA_API)
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
                .get(PRODUCT_METADATA_API)
                .then()
                .statusCode(401)
                .body("message", Matchers.equalTo("Unauthorized"))
                .body("errors", Matchers.contains("Authentication is required to access this resource."));
    }

    // Dropping the last character guarantees the signature no longer matches the original.
    private String tamperSignature(String token) {
        String[] parts = token.split("\\.");
        String signature = parts[2];
        String tamperedSignature = signature.substring(0, signature.length() - 1)
                + (signature.endsWith("A") ? "B" : "A");
        return parts[0] + "." + parts[1] + "." + tamperedSignature;
    }

}
