The API will be available at http://localhost:8080
Running Tests
bash# Run all tests
./gradlew test

# Run only unit tests
./gradlew test --tests "*Test"

# Run only integration tests
./gradlew test --tests "*Spec"
Database Console
Access H2 console at http://localhost:8080/h2-console

JDBC URL: jdbc:h2:mem:testdb
Username: sa
Password: (empty)

API Usage Examples
Create User
bashcurl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "firstName": "John",
    "lastName": "Doe"
  }'
Get All Users
bashcurl http://localhost:8080/api/users
Check Username Availability
bashcurl http://localhost:8080/api/users/check-username/johndoe
Validation Rules

Username: 3-20 characters, letters/numbers/underscores only, unique
Email: Valid email format, unique
First/Last Name: Required, max 50 characters

Error Handling
The API returns appropriate HTTP status codes and error messages:

400 Bad Request - Validation errors or invalid data
409 Conflict - Duplicate username or email
404 Not Found - User not found
500 Internal Server Error - Unexpected errors
