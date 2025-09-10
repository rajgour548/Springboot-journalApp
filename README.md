
# Journal App Backend

This is the backend of the **Journal App**, built using **Spring Boot**.  
It provides secure REST APIs for user authentication, journal management, email verification, and notifications. It connects with the frontend (React + Vite + TypeScript) to serve user requests and manage data efficiently.

## 🚀 Features
- User authentication with JWT and Google OAuth2
- Email verification using Brevo API
- Create, update, and delete journal entries
- Caching verification codes with Redis
- Scheduled tasks for automated maintenance
- Integrated with MongoDB for data storage

## 🛠️ Tech Stack
- **Backend**: Spring Boot, MongoDB, Redis, Spring Security, Kafka, Swagger
- **Email Service**: Brevo (Transactional email API)
- **Deployment**: Railway

## 🌐 Deployment
- Backend: [Railway Deployment](https://springboot-journalapp-production.up.railway.app)

## 📖 API Documentation

This backend includes interactive API docs powered by **Swagger UI**.

👉 [View Live Swagger UI](https://springboot-journalapp-production.up.railway.app/swagger-ui/index.html)

Use this to explore available endpoints, test APIs, and understand request/response formats.


## 📦 Setup Locally
```bash
# clone repo
git clone https://github.com/your-username/journal-backend.git

cd journal-backend

# install dependencies
mvn clean install

# run application
mvn spring-boot:run

