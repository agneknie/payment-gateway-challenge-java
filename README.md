# Payment Gateway Challenge - Java Implementation

## Requirements
- JDK 17
- Gradle
- Docker (for bank simulator testing)

## Run Application
```bash
docker-compose up -d
./gradlew bootRun
```
Application runs on http://localhost:8090

## Testing
```bash
./gradlew test
```

## Structure

The project follows a standard Spring Boot layout:

```
src/
├── main/
│   ├── java/com/checkout/payment/gateway/
│   │   ├── PaymentGatewayApplication.java     # Spring Boot main class
│   │   ├── configuration/                      # Application configuration beans
│   │   ├── controller/                         # REST API endpoints (PaymentGatewayController)
│   │   ├── service/                            # Business logic (PaymentGatewayService, BankSimulatorClient)
│   │   ├── repository/                         # Data persistence (PaymentsRepository - in-memory)
│   │   ├── model/                              # Request/response DTOs and data models
│   │   ├── enums/                              # Domain enumerations (Currency, PaymentStatus)
│   │   ├── exception/                          # Global exception handlers
│   │   ├── util/                               # Utility classes (RejectionMessages)
│   │   └── validation/                         # Custom validation annotations and validators
│   └── resources/
│       └── application.properties              # Application configuration (port, swagger settings)
└── test/
    └── java/com/checkout/payment/gateway/       # Unit tests for all layers

imposters/                                       # Bank simulator configuration files
docker-compose.yml                               # Bank simulator container setup
build.gradle                                     # Gradle build configuration and dependencies
```

## API Documentation

### Process Payment
**POST /payment**

- **200 OK**: Returns `SuccessfulPaymentResponse` when payment processed successfully
- **400 Bad Request**: Returns `RejectedPaymentResponse` for malformed requests
- **422 Unprocessable Entity**: Returns `RejectedPaymentResponse` for validation errors

### Get Payment
**GET /payment/{id}**

- **200 OK**: Returns `SuccessfulPaymentResponse` if payment found
- **404 Not Found**: Returns `ErrorResponse` if payment not found

Full API specs: http://localhost:8090/swagger-ui/index.html

## Design Considerations

### Architecture
- Layered architecture (Controller → Service → Repository) for separation of concerns
- Comprehensive input validation to prevent invalid requests before processing
- In-memory storage for simplicity and demo purposes

### Assumptions
- Bank performs some validation, but payment gateway handles all specified field validations independently, not relying on the bank
- Rejected payments (due to validation) are not forwarded to the bank to prevent bank overload when many malformed requests come
- Only authorized or declined payments are stored and retrievable by merchants; rejected payments (due to validation errors) are not persisted
- Bank simulator provides consistent responses; unexpected responses default to payment decline
- Current month of the current year is invalid for expiry date validation

### Security & Validation
- Card number masking and storage of only last 4 digits to protect sensitive PAN information
- No other sensitive information in requests/responses, eliminating need for additional encryption/masking beyond PAN handling
- All specified field validations performed by the payment gateway before bank forwarding
- Rejected payment responses include only status and abstracted reject reason for security and overload prevention
- Testing suite covers scenarios, which are oriented towards requirements. Additionally, some of the more complex validation scenarios are covered.