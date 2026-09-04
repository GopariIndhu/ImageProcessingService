# Image Processing Service

A Spring Boot REST API for uploading and managing images, secured with JWT-based authentication.

## Features

- User signup and login with hashed passwords (BCrypt)
- Stateless authentication using JSON Web Tokens (JWT)
- Image upload with automatic metadata extraction (dimensions, content type, size)
- Uploaded files are stored on disk with a unique generated filename, while original filenames and metadata are persisted in the database
- Protected endpoints — all routes except `/api/auth/**` require a valid JWT

## Tech Stack

- **Java 21**
- **Spring Boot 4.1.1** (Web MVC, Data JPA, Security, Validation)
- **MySQL** (via `mysql-connector-j`)
- **JJWT** for JWT creation/validation
- **Lombok**
- **Maven** (with the included `mvnw` wrapper)

## Project Structure

```
src/main/java/com/example/image_processing_service/
├── config/          # Security configuration
├── controller/      # REST controllers (auth, images, test)
├── dto/             # Request/response payloads
├── entity/          # JPA entities (User, Image)
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, JWT service, user details service
└── service/         # Business logic (auth, image handling)
```

## Prerequisites

- JDK 21+
- MySQL server running locally (or accessible remotely)
- Maven (or use the bundled `./mvnw` wrapper — no local install required)

## Setup


2. **Create the database**

   ```sql
   CREATE DATABASE image_processing_db;
   ```

3. **Configure application properties**

   Update `src/main/resources/application.properties` with your own database credentials and JWT secret:

   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/image_processing_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password

   jwt.secret=your_own_secret_key
   jwt.expiration=86400000

   file.upload-dir=uploads
   ```

   > ⚠️ **Security note:** the checked-in `application.properties` currently contains a real database password and JWT secret. Before pushing this project anywhere public, move these values out into environment variables or a `.env`/`application-local.properties` file that's excluded via `.gitignore`, and rotate the exposed credentials.

4. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

   The API will start on `http://localhost:8080` by default.

## API Endpoints

### Auth

| Method | Endpoint             | Description                    | Auth required |
|--------|-----------------------|--------------------------------|:--------------:|
| POST   | `/api/auth/signup`    | Register a new user            | No             |
| POST   | `/api/auth/login`     | Log in and receive a JWT       | No             |

**Signup request body**
```json
{
  "name": "Jane Doe",
  "email": "jane@example.com",
  "password": "yourpassword"
}
```

**Login request body**
```json
{
  "email": "jane@example.com",
  "password": "yourpassword"
}
```

**Login response**
```json
{
  "token": "<jwt-token>"
}
```

### Images

| Method | Endpoint        | Description                          | Auth required |
|--------|-----------------|---------------------------------------|:--------------:|
| POST   | `/api/images`   | Upload an image (multipart/form-data) | Yes            |

Send the JWT in the `Authorization` header as `Bearer <token>`.

```bash
curl -X POST http://localhost:8080/api/images \
  -H "Authorization: Bearer <token>" \
  -F "file=@/path/to/image.jpg"
```

**Response**
```json
{
  "id": 1,
  "originalFilename": "image.jpg",
  "contentType": "image/jpeg",
  "size": 204800,
  "width": 1920,
  "height": 1080,
  "uploadedAt": "2026-08-29T10:15:30"
}
```

### Test

| Method | Endpoint      | Description                                   | Auth required |
|--------|---------------|------------------------------------------------|:--------------:|
| GET    | `/api/test`   | Confirms JWT authentication is working          | Yes            |

## Running Tests

```bash
./mvnw test
```

## Roadmap / Ideas

- Image transformations (resize, crop, rotate, format conversion)
- Retrieve, list, and delete uploaded images
- Cloud storage support (S3-compatible) instead of local disk
- Pagination for image listings

## License

No license has been specified yet. Consider adding one (e.g. MIT) if you plan to share or accept contributions to this project.
