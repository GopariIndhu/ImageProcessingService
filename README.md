# Image Processing Service

A backend app (built with Spring Boot) where users can create an account, log in, and upload their own images. Each person can only see and manage their own photos — nobody else's.

## The Problem

If you're building something where people upload images (profile photos, documents, a gallery, anything), just saving the file to a folder isn't enough. You also need:

- **A login system**, so only people who signed up can upload or open files.
- **Ownership rules**, so one person can never see, download, or delete another person's images.
- **Checks on the file**, so someone can't upload a fake "image" that's actually something else.
- **Basic info about each file** (name, size, dimensions, when it was uploaded) that you can look up quickly, without opening the file itself.
- **A safe way to store files**, so two uploads never overwrite each other by accident.

Building all of this from scratch takes a lot of repeated work every time you start a new project.

## The Solution

This project already does all of that for you:

- **Login system** (`/api/auth/signup`, `/api/auth/login`) — sign up with your name, email, and password (the password is encrypted, not stored as plain text). Log in to get a security token (JWT), and use that token for everything else you do.
- **Each image belongs to one person** — every uploaded image is linked to the user who uploaded it. Before showing or deleting an image, the app checks that it actually belongs to the person asking.
- **File checking on upload** (`POST /api/images`) — when you upload a file, the app makes sure it's a real image before saving it. Empty or fake files get rejected.
- **Safe storage** — each file is saved on disk with a random, unique name, so two uploads never clash. The original file name is still remembered and shown to you.
- **File details saved in the database** — name, type, size, width, height, and upload date are all stored, so you can look them up without opening the actual file.
- **Get or delete your images** (`GET /api/images/{id}/file`, `DELETE /api/images/{id}`) — download an image you own, or delete it (removes it from both the disk and the database).

## Built With

- **Java 21** and **Spring Boot** (Web, Database, Security, Validation)
- **MySQL** for the database
- **JJWT** for creating and checking login tokens
- **BCrypt** for encrypting passwords
- **Maven** (comes with the included `mvnw` tool, so you don't need to install Maven separately)

## API Endpoints (what you can call)

| Method | Endpoint               | Login needed? | What it does                          |
|--------|-------------------------|:--------------:|----------------------------------------|
| POST   | `/api/auth/signup`      | No             | Create a new account                    |
| POST   | `/api/auth/login`       | No             | Log in and get a token                  |
| POST   | `/api/images`           | Yes            | Upload an image                         |
| GET    | `/api/images/{id}/file` | Yes            | Download/view one of your images        |
| DELETE | `/api/images/{id}`      | Yes            | Delete one of your images               |
| GET    | `/api/test`             | Yes            | Check that login is working             |

For anything that needs login, add your token to the request like this:

```
Authorization: Bearer <token>
```

## Getting Started

### What you need first

- Java 21 or newer
- MySQL installed and running (or your own database to connect to)
- Maven — or just use the `./mvnw` tool that's already included

### Set it up

Before running the app, open `src/main/resources/application.properties` and fill in your own database details and a secret key:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/image_processing_db
spring.datasource.username=your_username
spring.datasource.password=your_password

jwt.secret=your_own_long_random_secret
jwt.expiration=86400000

file.upload-dir=uploads
```

> **Important:** the `application.properties` file in this project currently has a real database password and secret key written in plain text. Please replace them with your own, and don't leave real passwords in a file that gets uploaded to GitHub. It's safer to use environment variables (like `${DB_PASSWORD}`) instead.

### Run the app

```bash
./mvnw spring-boot:run
```

The app will start on port `8080`, and it will automatically create an `uploads` folder to store the images (you can change this folder name in the settings).

## How the Code Is Organized

```
src/main/java/com/example/image_processing_service/
├── config/         # Login/security setup
├── controller/     # The API endpoints (login, images, test)
├── dto/            # Simple objects used to send/receive data
├── entity/         # The database tables (User, Image)
├── repository/     # Code that talks to the database
├── security/       # Token creation and checking
└── service/        # The main logic (login, image handling)
```
