package com.chethani.personalization;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class ProductMetadataIntegrationTest extends AbstractIntegrationTest {

    private static final String PRODUCT_METADATA_API = "/api/product-metadata";

    private static final String PRODUCT_ID = "BB-2144746855";
    private static final String CATEGORY = "Babies";
    private static final String BRAND = "Babyom";

    private static final String VALIDATION_FAILED = "Validation failed";

    @Test
	public void shouldCreateProductMetadata() {
		createProductMetadata(PRODUCT_ID, CATEGORY, BRAND);
	}

    @Test
	public void shouldReturnBadRequestWhenProductMetadataRequestInvalid() {
		String requestBody = """
                {
                    "productId": "",
                    "category": "",
                    "brand": ""
                }
                """;

        RestAssured.given()
				.header("Authorization", "Bearer " + dataTeamServiceToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PRODUCT_METADATA_API)
                .then()
                .statusCode(400)
				.body("status", Matchers.equalTo(400))
				.body("message", Matchers.equalTo(VALIDATION_FAILED))
				.body("errors.size()", Matchers.equalTo(3))
				.body("errors", Matchers.containsInAnyOrder(
						"productId must not be blank",
						"category must not be blank",
						"brand must not be blank"
				));
	}

    @Test
	void shouldRejectWhenRequestWithoutToken() {
		String requestBody = """
                {
                    "productId": "%s",
                    "category": "%s",
                    "brand": "%s"
                }
                """.formatted(PRODUCT_ID, CATEGORY, BRAND);
        RestAssured.given()
				// no Authorization header at all
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PRODUCT_METADATA_API)
                .then()
                .statusCode(401)
				.body("message", Matchers.equalTo("Unauthorized"))
				.body("errors", Matchers.contains("Authentication is required to access this resource."));
	}

    @Test
	void shouldForbiddenWhenAuthenticatedCallerLacksTheRequiredAuthority() {
		String requestBody = """
                {
                    "productId": "%s",
                    "category": "%s",
                    "brand": "%s"
                }
                """.formatted(PRODUCT_ID, CATEGORY, BRAND);
        RestAssured.given()
				.header("Authorization", "Bearer " + ecommerceServiceToken) // this token does not have the required authority
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PRODUCT_METADATA_API)
                .then()
                .statusCode(403)
				.body("errors", Matchers.contains("You do not have permission to perform this action."));	
	}

    

}
