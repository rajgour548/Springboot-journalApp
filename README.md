# 📔 JournalApp — Spring Boot Backend

A backend-focused journal management application built using **Java and Spring Boot**, providing secure REST APIs for authentication, journal management, email verification, and AI-powered sentiment analysis. It integrates Spring Security, MongoDB, Redis, Kafka, and external APIs with a React frontend.

## ✨ Project Highlights

🔹 JWT-based authentication and Google OAuth2 login
🔹 Journal entry CRUD operations using REST APIs
🔹 Email verification using Brevo Transactional Email API
🔹 Redis-based temporary verification code storage with expiration
🔹 AI-powered sentiment analysis using Google Gemini API
🔹 Weekly scheduler publishes sentiment-analysis events to Kafka
🔹 Kafka consumer service processes events and sends emails through Brevo
🔹 Scheduled background tasks for maintenance and processing
🔹 Centralized exception handling using Spring Controller Advice
🔹 Interactive API documentation using Swagger UI

## 🏗️ Tech Stack

- **Backend** ➜ Java, Spring Boot
- **Security** ➜ Spring Security, JWT, Google OAuth2
- **Database** ➜ MongoDB
- **Caching** ➜ Redis
- **Messaging** ➜ Apache Kafka
- **AI Integration** ➜ Google Gemini API
- **Email Service** ➜ Brevo Transactional Email API
- **API Documentation** ➜ Swagger UI / OpenAPI
- **Build Tool** ➜ Maven
- **Frontend Integration** ➜ React, Vite, TypeScript
- **Deployment** ➜ Railway

## 🏗️ Architecture Overview

```text
React Frontend
      |
      | HTTP / REST API
      v
Spring Boot Backend
      |
      +---- Spring Security
      |       +---- JWT Authentication
      |       +---- Google OAuth2
      |
      +---- MongoDB
      |       +---- Users
      |       +---- Journal Entries
      |
      +---- Redis
      |       +---- Temporary Verification Codes
      |
      +---- Gemini API
      |       +---- Sentiment Analysis
      |
      +---- Weekly Scheduler
              |
              v
          Kafka Topic
              |
              v
        Consumer Service
              |
              v
         Brevo Email API
```

## 🌐 Live Links

- **Backend** ➜ [JournalApp on Railway](https://springboot-journalapp-production.up.railway.app/)
- **Swagger UI** ➜ [Explore API Documentation](https://springboot-journalapp-production.up.railway.app/swagger-ui/index.html)

## 🚀 How to Run Locally

### Prerequisites

- Java version compatible with the project configuration
- Maven
- MongoDB
- Redis
- Apache Kafka
- Required external API credentials

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/rajgour548/Springboot-journalApp.git
cd <your-repository>
```

Replace the placeholder with your actual GitHub repository URL.

### 2️⃣ Configure Environment Variables

Configure the variables referenced in `src/main/resources/application.properties`, including:

- MongoDB connection settings
- JWT signing secret
- Redis connection settings
- Brevo API credentials and sender details
- Google OAuth2 client credentials
- Gemini API key
- Kafka broker address and consumer group

🔐 **Security:** Never commit API keys, passwords, JWT secrets, or OAuth2 client secrets to GitHub.

### 3️⃣ Start Required Services

Start MongoDB, Redis, and Kafka, and verify that the application can connect to them.

### 4️⃣ Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The application runs on port `8080` by default, unless configured otherwise.

### 5️⃣ Open Swagger UI

After starting the backend, visit:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

Use Swagger UI to explore the available REST endpoints and inspect their request and response formats.

## 📘 What This Project Demonstrates

- Building secure REST APIs with Spring Boot
- Implementing authentication and authorization with Spring Security
- Integrating MongoDB and Redis
- Scheduling background tasks and publishing events with Kafka
- Consuming Kafka events and sending email notifications through Brevo
- Integrating external AI and email APIs
- Implementing centralized exception handling
- Documenting APIs using Swagger UI

## ⭐ Why This Repository Is Useful

- Demonstrates practical Java backend development
- Combines authentication, persistence, caching, and messaging
- Shows a scheduled Kafka event-processing workflow
- Integrates real external services
- Provides a foundation for learning and extending Spring Boot backend applications
