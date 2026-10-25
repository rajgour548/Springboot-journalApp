# JournalApp — Spring Boot Backend

A backend-focused journal management application built with **Java and Spring Boot**, providing secure REST APIs for authentication, journal entry management, email verification, and AI-powered sentiment analysis.

The application integrates a React frontend with Spring Security, MongoDB, Redis, Kafka, and external services.

## Features

- **Authentication and Authorization** — JWT-based authentication and Google OAuth2 login using Spring Security.
- **Journal Management** — REST APIs to create, retrieve, update, and delete journal entries.
- **Email Verification** — Transactional emails and verification workflows using the Brevo API.
- **Verification Code Management** — Redis-backed temporary code storage with expiration.
- **Sentiment Analysis** — Gemini API integration for journal sentiment analysis.
- **Scheduled Sentiment Workflow** — A weekly scheduler publishes sentiment-analysis events to Kafka, which are consumed by a consumer service that sends email notifications through Brevo.
- **Scheduled Tasks** — Automated background tasks for maintenance and scheduled processing.
- **Centralized Exception Handling** — Consistent error handling through Spring's controller advice mechanism.
- **API Documentation** — Interactive API exploration using Swagger UI.

## Tech Stack

| Category | Technologies |
|---|---|
| Language | Java |
| Backend Framework | Spring Boot |
| Security | Spring Security, JWT, Google OAuth2 |
| Database | MongoDB |
| Caching | Redis |
| Messaging | Apache Kafka |
| AI Integration | Google Gemini API |
| Email Service | Brevo Transactional Email API |
| API Documentation | Swagger UI / OpenAPI |
| Build Tool | Maven |
| Frontend Integration | React, Vite, TypeScript |
| Deployment | Railway |

## Architecture Overview

```text

React Frontend

\&#x20;     |

\&#x20;     | HTTP / REST API

\&#x20;     v

Spring Boot Backend

\&#x20;     |

\&#x20;     +---- Spring Security

\&#x20;     |       +---- JWT Authentication

\&#x20;     |       +---- Google OAuth2

\&#x20;     |

\&#x20;     +---- MongoDB

\&#x20;     |       +---- Users

\&#x20;     |       +---- Journal Entries

\&#x20;     |

\&#x20;     +---- Redis

\&#x20;     |       +---- Temporary Verification Codes

\&#x20;     |

\&#x20;     +---- Gemini API

\&#x20;     |       +---- Sentiment Analysis

\&#x20;     |

\&#x20;     +---- Weekly Scheduler

\&#x20;             |

\&#x20;             v

\&#x20;         Kafka Topic

\&#x20;             |

\&#x20;             v

\&#x20;       Consumer Service

\&#x20;             |

\&#x20;             v

\&#x20;        Brevo Email API

```

## Live Links

- **Backend:** [JournalApp on Railway](https://springboot-journalapp-production.up.railway.app/)
- **Swagger UI:** [Explore the API](https://springboot-journalapp-production.up.railway.app/swagger-ui/index.html)

The deployment links are retained from the existing README. Availability and endpoint functionality may change over time.

## Run Locally

### Prerequisites

- Java version compatible with the project configuration
- Maven
- MongoDB
- Redis
- Apache Kafka
- Credentials for the external services you intend to use

### 1. Clone the Repository

Replace the placeholder URL with your actual GitHub repository URL.

```bash

git clone https://github.com/<your-username>/<your-repository>.git

cd <your-repository>

```

### 2. Configure Environment Variables

Configure the environment variables referenced in `src/main/resources/application.properties`, including settings for:

- MongoDB connection and database
- JWT signing secret
- Redis connection
- Brevo API and email sender
- Google OAuth2 client credentials
- Gemini API key
- Kafka broker address and consumer group

Use your own credentials. **Never commit API keys, passwords, JWT secrets, or OAuth2 client secrets to GitHub.**

### 3. Start the Required Services

Start MongoDB, Redis, and Kafka, and ensure the application can connect to them.

### 4. Build and Run

```bash

mvn clean install

mvn spring-boot:run

```

The application defaults to port `8080` unless configured otherwise.

## API Documentation

After starting the backend, open:

`http://localhost:8080/swagger-ui/index.html`

Swagger UI lets you explore available REST endpoints and inspect their request and response formats.

## Project Goals

This project demonstrates practical backend development concepts, including REST API design, authentication and authorization, database integration, caching, Kafka-based event processing, scheduled tasks, external API integration, and centralized exception handling.
