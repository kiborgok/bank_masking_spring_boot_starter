Bank Masking Spring Boot Starter — Technical Assessment
A reusable Spring Boot Starter that transparently masks sensitive fields in log output, paired with a Books CRUD API that demonstrates its usage.

Repository Structure
bank-masking-project/
├── pom.xml
├── README.md
│
├── bank-masking-spring-boot-starter/     # Module 1
│   └── src/main/java/com/p11/masking/
│       ├── annotation/Mask.java          # @Mask – declarative field masking
│       ├── config/
│       │   ├── MaskingProperties.java    # @ConfigurationProperties (p11.masking.*)
│       │   └── MaskingAutoConfiguration.java
│       ├── core/
│       │   ├── MaskStyle.java            # FULL / PARTIAL / SHOW_LAST
│       │   └── MaskingService.java       # Core masking engine
│       ├── jackson/
│       │   ├── MaskingBeanSerializerModifier.java
│       │   └── MaskingPropertyWriter.java
│       └── logback/
│           └── MaskingTurboFilter.java   # Logback interception layer
│
└── bank-books-api-demo/                  # Module 2 – consumer application
└── src/main/java/com/p11/books/
├── controller/BookController.java
├── service/BookService.java
├── repository/BookRepository.java
├── mapper/BookMapper.java
├── dto/BookDto.java
├── entity/Book.java
└── exception/

Architecture & Design Decisions
1. Logback TurboFilter — Transparent Log Interception
   The MaskingTurboFilter is registered programmatically with Logback's LoggerContext at startup. It intercepts every log call before the message is formatted. For each POJO argument in a log statement:

The filter detects non-primitive objects.
It serialises the object using the masking ObjectMapper.
It replaces the argument slot with the masked JSON string.
The original object is never modified.

2. Jackson BeanSerializerModifier — Field-level Masking
   MaskingBeanSerializerModifier intercepts Jackson's serialisation pipeline and replaces BeanPropertyWriter for sensitive fields with MaskingPropertyWriter. Because Jackson applies modifiers recursively, nested objects and collections are handled automatically.

A property is treated as sensitive when:

Its name appears in p11.masking.fields (config-based), OR
It is annotated with @Mask (annotation-based, bonus feature).
3. Separate maskingObjectMapper
   A dedicated ObjectMapper bean named maskingObjectMapper is created with the masking modifier registered. The application's primary ObjectMapper remains untouched — masking only applies to log output, not API responses.

4. Masking Styles
   Style	Example input	Example output
   FULL	secret123	*********
   PARTIAL	alex@example.com	a***@example.com style
   SHOW_LAST	4111111111111111	************1111
5. SOLID Principles Applied
   S — MaskStyle handles only masking math; MaskingService only orchestrates; TurboFilter only intercepts.
   O — New masking styles can be added to the MaskStyle enum without modifying consumers.
   L — MaskingPropertyWriter extends BeanPropertyWriter substituting correctly.
   I — MaskingService exposes two focused methods: mask(String) and toMaskedJson(Object).
   D — Auto-configuration wires everything; consumers just add the dependency.
   Configuration Reference
   p11:
   masking:
   enabled: true           
   fields:
    - email
    - phoneNumber
    - ssn
    - creditCardNumber
      mask-style: PARTIAL        # FULL | PARTIAL | SHOW_LAST
      mask-character: "*"
      How to Run Locally
      Prerequisites
      Java 17+
      Maven 3.9+
      Build and Run
# 1. Build and install the starter (must be first)
cd bank-masking-spring-boot-starter
mvn clean install -DskipTests

# 2. Run the demo API
cd ../bank-books-api-demo
mvn spring-boot:run
The API will start on http://localhost:8080.

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
H2 Console: http://localhost:8080/h2-console (JDBC URL: jdbc:h2:mem:booksdb)

Sample Request
curl -X POST http://localhost:8080/api/v1/books \
-H "Content-Type: application/json" \
-d '{
"title": "The River Between",
"author": "Ngugi Wa Thiongo",
"email": "ngugi@example.com",
"phoneNumber": "+254712345678",
"publisher": "LongHorn"
}'
Log output (email and phone masked):

Creating book: {"id":null,"title":"The River Between","author":"Ngugi Wa Thiongo","email":"r****","phoneNumber":"+****","publisher":"LongHorn"}
API response (values returned unmasked from DB):

{
"id": 1,
"title": "The River Between",
"author": "Ngugi Wa Thiongo",
"email": "ngugi@example.com",
"phoneNumber": "+254712345678",
"publisher": "LongHorn"
}

How to Run Tests
# Run all tests (both modules)
mvn clean test

# Starter tests only
cd bank-masking-spring-boot-starter
mvn test

# Demo app tests only
cd bank-books-api-demo
mvn test

Coverage Proof
JaCoCo reports are generated at:

bank-masking-spring-boot-starter/target/site/jacoco/index.html