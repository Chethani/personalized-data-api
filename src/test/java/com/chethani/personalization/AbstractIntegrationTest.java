package com.chethani.personalization;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.utility.DockerImageName;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestcontainersConfiguration.class)
public abstract class AbstractIntegrationTest {

	@SuppressWarnings("resource")
    static KeycloakContainer keycloakContainer = new KeycloakContainer(DockerImageName.parse("quay.io/keycloak/keycloak:26.7"))
            .withRealmImportFile("personalized-data-api-realm-realm.json");

	static {
    	keycloakContainer.start();
	}

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/personalized-data-api-realm");
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> keycloakContainer.getAuthServerUrl() + "/realms/personalized-data-api-realm/protocol/openid-connect/certs");
    }

    protected String dataTeamServiceToken;
    protected String ecommerceServiceToken;

    @LocalServerPort
	protected Integer port;

    @BeforeAll
    void setUpTokens() {
        dataTeamServiceToken = getAccessToken("data-team-service", "test-secret-data-team");
        ecommerceServiceToken = getAccessToken("ecommerce-service", "test-secret-ecommerce");
        System.out.println(this.getClass().getSimpleName() + " - Token fetched at: " + java.time.Instant.now());
    }

    @BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

    protected String getAccessToken(String clientId, String clientSecret) {
    return RestAssured.given()
            .contentType("application/x-www-form-urlencoded")
            .formParam("grant_type", "client_credentials")
            .formParam("client_id", clientId)
            .formParam("client_secret", clientSecret)
            .when()
            .post(keycloakContainer.getAuthServerUrl() + "/realms/personalized-data-api-realm/protocol/openid-connect/token")
            .then()
            .statusCode(200)
            .extract()
            .path("access_token");
	}

    protected void createProductMetadata(String productId, String category, String brand) {
        String requestBody = """
                {
                    "productId": "%s",
                    "category": "%s",
                    "brand": "%s"
                }
                """.formatted(productId, category, brand);
		
        RestAssured.given()
				.header("Authorization", "Bearer " + dataTeamServiceToken)
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/product-metadata")
                .then()
				// Not asserting exact message text here — status code is the meaningful contract; exact wording is free to change without breaking tests.
                .statusCode(201);
    }

}
