Movie Booking System - Backend

A robust, secure RESTful API built with Spring Boot to power a high-traffic movie ticket booking platform. This system handles user authentication, real-time seat availability, and complex booking transactions.

🚀 Live Demo

The backend is currently hosted on Render:
https://movie-ticket-booking-backend-qnhs.onrender.com/api/v1/movies

🛠 Tech Stack

Language: Java 17

Framework: Spring Boot 3.x

Security: Spring Security & JWT (JSON Web Tokens)

Database: PostgreSQL (Hosted on Neon.tech)

ORM: Spring Data JPA / Hibernate

Build Tool: Maven

Deployment: Docker & Render

✨ Key Features

Stateless Authentication: Secure login and registration using JWT.

Complex Relations: Manages Movie -> Theatre -> Showtime -> Booking relationships.

Seat Management: Real-time calculation of occupied seats to prevent double-booking.

Serialization Mastery: Resolved complex Hibernate Proxy/Lazy Loading issues using customized Jackson serialization.

Production Ready: Configured with environment-specific variables and PostgreSQL for cloud scalability.

⚙️ Environment Variables

To run this project, you will need to add the following variables to your .env or IDE configuration:

DB_URL: PostgreSQL JDBC connection string.

DB_USER: Database username.

DB_PASSWORD: Database password.

JWT_SECRET: 64-character random string for signing tokens.

CORS_ALLOWED_ORIGINS: Comma-separated list of allowed frontend URLs.

📖 API Documentation

GET - /api/v1/movies -  Fetch all movies

GET - /api/v1/showtimes/movie/{id} - Get showtimes grouped by theatre

POST - /api/v1/auth/login - Authenticate user and get token

POST - /api/v1/bookings - Create a new ticket booking

GET - /api/v1/bookings/user - Get history for logged-in user

🛠 Local Setup

Clone the repository.

Update application.properties with your local DB credentials.

Run ./mvnw spring-boot:run

Access the API at http://localhost:8080/api/v1
