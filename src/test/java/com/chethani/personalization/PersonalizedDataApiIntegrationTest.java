package com.chethani.personalization;

import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PersonalizedDataApiIntegrationTest {

	private static final String PRODUCT_METADATA_API = "/api/product-metadata";
    private static final String SHOPPER_SHELF_API = "/api/shopper-shelf";

	private static final String SHOPPER_ID = "S-1000";

	private static final String PRODUCT_ID = "BB-2144746855";
	private static final String PRODUCT_ID_2 = "MB-2093193398";
	private static final String PRODUCT_ID_3 = "MD-543564697";

	private static final String CATEGORY = "Babies";
	private static final String CATEGORY_2 = "Women";
	private static final String CATEGORY_3 = "Men";

	private static final String BRAND = "Babyom";
	private static final String OTHER_BRAND = "OtherBrand";

	private static final Double RELEVANCY_SCORE = 55.16626010671777;
	private static final Double RELEVANCY_SCORE_2 = 31.089209569320897;
	private static final Double RELEVANCY_SCORE_3 = 73.01492966268303;

	private static final String VALIDATION_FAILED = "Validation failed";

	@LocalServerPort
	private Integer port;

	@BeforeEach
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	@Test
	public void shouldCreateProductMetadata() {
		createProductMetadata(PRODUCT_ID, CATEGORY, BRAND);
	}

	@Test
	public void shouldCreateShopperShelf() {
		createProductMetadata(PRODUCT_ID, CATEGORY, BRAND);
		createShopperShelf(SHOPPER_ID, List.of(new TestShelfItem(PRODUCT_ID, RELEVANCY_SCORE)));
	}

	@Test
	public void shouldReadShopperShelfByShopperId() {
		createProductMetadata(PRODUCT_ID, CATEGORY, BRAND);
		createShopperShelf(SHOPPER_ID, List.of(new TestShelfItem(PRODUCT_ID, RELEVANCY_SCORE)));

		RestAssured.given()
				.queryParam("shopperId", SHOPPER_ID)	
				.when()
				.get(SHOPPER_SHELF_API)
				.then()
				.statusCode(200)
				.body("size()", Matchers.equalTo(1))
				.body("[0].productId", Matchers.equalTo(PRODUCT_ID))
				.body("[0].category", Matchers.equalTo(CATEGORY))
				.body("[0].brand", Matchers.equalTo(BRAND));
	}

	@Test
	public void shouldReadShopperShelfFilteredByCategory() {
		createSampleProductsAndShelf();
		RestAssured.given()
				.queryParam("shopperId", SHOPPER_ID)
				.queryParam("category", CATEGORY)
				.when()
				.get(SHOPPER_SHELF_API)
				.then()
				.statusCode(200)
				.body("size()", Matchers.equalTo(1))
				.body("category", Matchers.everyItem(Matchers.equalTo(CATEGORY)));
	}

	@Test
	public void shouldReadShopperShelfFilteredByBrand() {
		createSampleProductsAndShelf();
		RestAssured.given()
				.queryParam("shopperId", SHOPPER_ID)
				.queryParam("brand", BRAND)
				.when()
				.get(SHOPPER_SHELF_API)
				.then()
				.statusCode(200)
				.body("size()", Matchers.equalTo(2))
				.body("brand", Matchers.everyItem(Matchers.equalTo(BRAND)));
	}

	@Test
	public void shouldApplyLimit() {
		createSampleProductsAndShelf();
		RestAssured.given()
            .queryParam("shopperId", SHOPPER_ID)
            .queryParam("limit", 2)
            .when()
            .get(SHOPPER_SHELF_API)
            .then()
            .statusCode(200)
            .body("size()", Matchers.equalTo(2));
	}

	@Test
	public void shouldReturnShopperShelfOrderedByRelevancyScoreDescending(){
		createSampleProductsAndShelf();
		RestAssured.given()
            .queryParam("shopperId", SHOPPER_ID)
            .when()
            .get(SHOPPER_SHELF_API)
            .then()
            .statusCode(200)
            .body("size()", Matchers.equalTo(3))
			.body("[0].productId", Matchers.equalTo(PRODUCT_ID_3))
			.body("[1].productId", Matchers.equalTo(PRODUCT_ID))
			.body("[2].productId", Matchers.equalTo(PRODUCT_ID_2));
	}

	@Test
	public void shouldReturnBadRequestWhenShopperIdMissing() {
		RestAssured.given()
				.when()
				.get(SHOPPER_SHELF_API)
				.then()
				.statusCode(400)
				.body("status", Matchers.equalTo(400))
				.body("message", Matchers.equalTo(VALIDATION_FAILED))
				.body("errors.size()", Matchers.equalTo(1))
				.body("errors[0]", Matchers.equalTo("Missing required request parameter: shopperId"));
	}

	@Test
	public void shouldReturnBadRequestWhenLimitGreaterThan100() {
		RestAssured.given()
				.queryParam("shopperId", SHOPPER_ID)
				.queryParam("limit", 101)
				.when()
				.get(SHOPPER_SHELF_API)
				.then()
				.statusCode(400)
				.body("status", Matchers.equalTo(400))
				.body("message", Matchers.equalTo(VALIDATION_FAILED))
				.body("errors.size()", Matchers.equalTo(1))
				.body("errors[0]", Matchers.equalTo("Limit must be less than or equal to 100"));
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

	private void createProductMetadata(String productId, String category, String brand) {
        String requestBody = """
                {
                    "productId": "%s",
                    "category": "%s",
                    "brand": "%s"
                }
                """.formatted(productId, category, brand);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(PRODUCT_METADATA_API)
                .then()
                .statusCode(201);
    }

	private record TestShelfItem(String productId, Double relevancyScore) {}

	private void createShopperShelf(String shopperId, List<TestShelfItem> shelfItems) {
        String shelfJson = shelfItems.stream()
            .map(item -> """
                    {
                        "productId": "%s",
                        "relevancyScore": %s
                    }
                    """.formatted(item.productId(), item.relevancyScore()))
            .collect(Collectors.joining(","));

		String requestBody = """
				{
					"shopperId": "%s",
					"shelf": [%s]
				}
				""".formatted(shopperId, shelfJson);

        RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post(SHOPPER_SHELF_API)
                .then()
                .statusCode(201);
    }

	private void createSampleProductsAndShelf() {
		createProductMetadata(PRODUCT_ID, CATEGORY, BRAND);
		createProductMetadata(PRODUCT_ID_2, CATEGORY_2, BRAND);
		createProductMetadata(PRODUCT_ID_3, CATEGORY_3, OTHER_BRAND);

		createShopperShelf(SHOPPER_ID, List.of(
				new TestShelfItem(PRODUCT_ID, RELEVANCY_SCORE),
				new TestShelfItem(PRODUCT_ID_2, RELEVANCY_SCORE_2),
				new TestShelfItem(PRODUCT_ID_3, RELEVANCY_SCORE_3)
		));
	}

}
