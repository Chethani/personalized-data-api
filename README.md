# Personalized Data API

A Spring Boot backend service that provides personalized product data for shoppers, featuring internal APIs for storing product metadata and shopper shelf data, plus an external API for retrieving personalized products with optional filters.

## Tech Stack

- **Java** - Programming language
- **Spring Boot** - Application framework
- **Spring Web** - REST API support
- **Spring Data JPA** - Data persistence
- **MySQL** - Relational database
- **Flyway** - Database migrations
- **Bean Validation** - Input validation
- **Testcontainers** - Integration testing with MySQL
- **RestAssured** - REST API testing
- **Maven** - Build management

## Features

- Store and update product metadata (productId, category, brand)
- Manage personalized shopper shelf data with relevancy scores
- Retrieve personalized products for a shopper with optional category and brand filters
- Fast queries with optimized database indexes
- Results ordered by relevancy score in descending order
- Input validation with structured error responses
- Integration tests for key API flows using Testcontainers

## API Documentation

The available endpoints, request examples, response examples, and validation error format are documented here.

### 1. POST /api/product-metadata

Store or update product metadata.

**Request:**
```json
{
  "productId": "BB-2144746855",
  "category": "Babies",
  "brand": "Babyom"
}
```

**Response:** 201 Created
```json
{
  "message": "Product metadata added successfully"
}
```
---

### 2. POST /api/shopper-shelf

Store personalized shelf data for a shopper. This operation replaces existing shelf data for the shopper within a transaction.

**Request:**
```json
{
  "shopperId": "S-1000",
  "shelf": [
    {
      "productId": "BB-2144746855",
      "relevancyScore": 55.16626010671777
    }
  ]
}
```

**Response:** 201 Created
```json
{
  "message": "Shopper shelf data added successfully"
}
```

---

### 3. GET /api/shopper-shelf

Retrieve personalized products for a shopper.

**Query Parameters:**
- `shopperId` (required): The shopper identifier
- `category` (optional): Filter by product category
- `brand` (optional): Filter by product brand
- `limit` (optional): Number of results to return (default: 10, max: 100)

**Example Request:**
```
GET /api/shopper-shelf?shopperId=S-1000&category=Babies&brand=Babyom&limit=10
```

**Response:**
```json
[
  {
    "productId": "BB-2144746855",
    "category": "Babies",
    "brand": "Babyom",
    "relevancyScore": 55.16626010671777
  }
]
```

Products are returned ordered by relevancy score in descending order.

---

## Error Response Format

Validation errors return a structured response with HTTP 400 status:

```json
{
  "status": 400,
  "message": "Validation failed",
  "errors": [
    "Limit must be less than or equal to 100"
  ]
}
```

## Database Design

### Tables

**product_metadata**
- `product_Id` (VARCHAR, PRIMARY KEY): Unique product identifier
- `category` (VARCHAR): Product category
- `brand` (VARCHAR): Product brand

**shopper_shelf**
- `shopper_Id` (VARCHAR): Shopper identifier
- `product_Id` (VARCHAR): Product identifier, foreign key to `product_metadata.product_id`
- `relevancy_Score` (DOUBLE): Relevancy score for the product
- **Primary Key:** (shopper_Id, product_Id)

### Indexes

- `idx_shopper_relevancy`: (`shopper_id`, `relevancy_score` DESC) - optimizes retrieval by shopper with relevance ordering
- `idx_category_brand`: (`category`, `brand`) - optimizes filtering by category and brand

## Assumptions

- Product IDs are unique identifiers and must exist before being added to a shopper's shelf
- Replacing a shopper's shelf data is an atomic operation; partial updates are not supported
- The relevancy score represents a numeric ranking where higher values indicate greater relevance
- All string inputs are case-sensitive
- The limit parameter enforces a maximum of 100 results for performance

## Performance Considerations

- Composite indexes on (`shopper_id`, `relevancy_score` DESC) and (`category`, `brand`) enable efficient queries
- Filters are applied at the database query level, reducing data transfer
- The limit parameter prevents returning excessive data for large shelves
- Flyway migrations ensure consistent database schema

## How to Run Locally

### Prerequisites

- Java 21
- Maven 3.6+
- MySQL 8.0+ running locally or in a container

### Steps

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd personalized-data-api
   ```

2. **Configure database** 
    The application is configured to use MySQL with the following default local values:

    - Database: `personalization_db`
    - Username: `root`
    - Password: `mysql`
    - URL: `jdbc:mysql://localhost:3306/personalization_db?useSSL=false&allowPublicKeyRetrieval=true`

    These values can be overridden using environment variables:
    - `SPRING_DATASOURCE_URL`
    - `SPRING_DATASOURCE_USERNAME`
    - `SPRING_DATASOURCE_PASSWORD`

    Docker Compose creates the personalization_db database using the configured MySQL environment variables. Flyway creates the required tables and indexes when the application starts.

3. **Start MySQL using Docker Compose**
    ```bash
   docker compose up -d
   ```

4. **Build the project**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

   The service will start at `http://localhost:8080`

## How to Run Tests

Execute integration tests using Maven:

```bash
mvn test
```

Tests use Testcontainers to spin up a MySQL instance automatically. Ensure Docker is running.

### Test Coverage

- Product metadata creation and validation
- Shopper shelf creation and replacement
- Retrieval by shopperId
- Category and brand filtering
- Limit parameter validation
- Relevancy score ordering
- Validation error responses

## Future Improvements

- Add authentication and authorization to protect internal write APIs and external read APIs
- Add Redis caching for frequently accessed shopper recommendations
- Add pagination for browsing larger recommendation sets beyond the top limited results
- Add audit logging for product metadata and shopper shelf data changes
- Add monitoring and metrics for API latency, error rates, request volume, and database health
- Support batch product metadata uploads
