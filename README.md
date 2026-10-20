# Who Am I?

A multiplayer REST backend for a “Who Am I?” guessing game.

This project was developed as a team academic project during the ELEKS Engineering Academy. It was also used as the basis for a bachelor's thesis.

## Overview

Players can create and join games, suggest characters, ask and answer questions, and try to guess the hidden character. The backend exposes a REST API for game management and player interaction.

The project was developed to practise professional Java backend development, layered architecture, API design, persistence, validation and automated testing.

## Features

- Create and join multiplayer games
- Start and manage game sessions
- Suggest characters
- Ask and answer questions
- Submit character guesses
- Track player turns and game state
- View game history
- Leave a game
- Validate incoming requests
- Return structured error responses
- Document the API using OpenAPI

## Technology Stack

- Java 17
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- PostgreSQL
- Flyway
- Maven
- REST API
- OpenAPI
- JUnit
- WireMock
- JaCoCo
- GitHub Actions

## Architecture

The application follows a layered architecture:

- **Controllers** – expose REST endpoints and handle HTTP requests
- **Services** – contain application and game logic
- **Repositories** – provide data access
- **Domain layer** – represents games, players and turns
- **Configuration** – contains security and exception-handling configuration
- **Models** – define request and response objects

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/eleks/academy/whoami/
│   │       ├── configuration/
│   │       ├── controller/
│   │       ├── core/
│   │       ├── model/
│   │       ├── repository/
│   │       ├── service/
│   │       └── utils/
│   └── resources/
│       ├── db/migration/
│       ├── application.yml
│       └── openapi.yml
└── test/
    ├── java/
    └── resources/
