# Backend Engineering Case Study

## Introduction

This project is a backend implementation for a case study provided by Dream Games. It includes functionality for user
management, partnership features, and balloon-related progress tracking, all built using Java, Spring Boot, and MySQL.
The application is containerized using Docker for seamless development and deployment.

## Table of Contents

- [Introduction](#introduction)
- [Getting Started](#getting-started)
  - [Prerequisites and Tech Stack](#prerequisites-and-tech-stack)
  - [Installation](#installation)
- [API Documentation](#api-documentation)
  - [Project Structure](#project-structure)
  - [Endpoints](#endpoints)
    - [UserController Endpoints](#usercontroller-endpoints)
    - [PartnershipController Endpoints](#partnershipcontroller-endpoints)
- [Testing](#testing)
- [Postman Collection](#postman-collection)
- [Design Decisions](#design-decisions)

## Getting Started

### Prerequisites and Tech Stack

- Java 17
- Spring Boot 3.3.2
- MySQL 8.0
- Maven
- Docker and Docker Compose
- JUnit 5, Mockito, Spring Boot Test and H2 database for testing
- Postman is recommended for testing the API, but any REST client should work

### Installation

The project can be run using Docker Compose after cloning the repository. To start the application, run the following
command in the root directory:

```bash
   docker buildx build .
```

This command will build the application and start the MySQL database and the application server. The application will be
available at `http://localhost:8080`.

Important: You must use your own credentials for the MySQL database. To do this, please create a `.env` file in the root
with the following content:

```bash
 SPRING_DATASOURCE_URL=jdbc:mysql://mysqldb:3306/DREAM_GAMES_PROJECT
 SPRING_DATASOURCE_USERNAME=root
 SPRING_DATASOURCE_PASSWORD=root1234
 MYSQL_DATABASE=DREAM_GAMES_PROJECT
 MYSQL_ROOT_PASSWORD=root1234

```

This is just an example. You can use your own credentials.

## API Documentation

### Project Structure

The project is structured as follows:

- `src/main/java/com/dream/backend`: Entry point for the application
  - `controller`: REST controllers
  - `model`: Entity classes
  - `repository`: JPA repositories
  - `service`: Service layer with business logic

### Endpoints

#### UserController Endpoints

| HTTP Method | Endpoint                                         | Description                                                     |
|-------------|--------------------------------------------------|-----------------------------------------------------------------|
| POST        | /users/create-user                               | Creates a new user    with a unique ID                          |
| POST        | /users/send-invitation/{senderId}                | Sends an invitation to one of the suggested users               |
| GET         | /users/claim-reward/{userId}                     | Claims a reward for a successful pop the balloon event          |
| GET         | /users/get-invitations/{userId}                  | Gets pending invitations                                        |
| GET         | /users/get-suggestions/{userId}                  | Gets partner suggestions                                        |
| GET         | /users/get/balloons-info/{userId}                | Gets balloon progress information for the pop the balloon event |
| GET         | /users/get-leaderboard                           | Get the leaderboard                                             |
| PUT         | /users/level-up/{userId}                         | Increases user level by 1                                       |
| PUT         | /users/update-coins/{userId}/{amount}            | Updates user coins                                              |
| PUT         | /users/update-helium-count/{userId}/{amount}     | Updates user helium count                                       |
| PUT         | /users/update-balloon-progress/{userId}/{amount} | Updates balloon progress                                        |
| PUT         | /users/set-level/{userId}/{newLevel}             | Sets user level (for testing purposes)                          |

#### PartnershipController Endpoints

| HTTP Method | Endpoint                                     | Description              |
|-------------|----------------------------------------------|--------------------------|
| POST        | /partnerships/accept/{senderId}/{receiverId} | Accept a partnership     |
| POST        | /partnerships/reject/{senderId}/{receiverId} | Reject a partnership     |
| POST        | /partnerships/cancel/{senderId}/{receiverId} | Cancel a partnership     |
| POST        | /partnerships/reset                          | Reset all partnerships   |
| GET         | /partnerships/pending/{receiverId}           | Get pending partnerships |

### Testing

The application includes unit tests for the service layer using JUnit 5 and Mockito. The tests can be run using the
following command:

```bash
  mvn test
```

### Postman Collection

You can find the Postman collection for testing the API in the root directory of the project. The collection includes
requests for all endpoints with sample data.
Important: Before running the requests, please be sure that the there is enough data in the database to test the
endpoints. Also some requests are dependent on each other. For example, to test the accept partnership endpoint, there
should be a pending partnership in the database. Otherwise, the request will return an internal server error.

### Design Decisions

- The application uses a MySQL database to store user and partnership information. The database schema is created
  automatically by Hibernate.
- There are 2 main entities: User and Partnership. The User entity includes fields for user ID, level, coins, helium
  count, and balloon progress. The Partnership entity includes fields for sender ID, receiver ID, and status.
- The application stores the information about the progress of the pop the balloon event in the Partnership entity.
  Users, who are partners, can pop the balloon together and claim rewards.
- The mechanism for sending invitations and getting partner suggestions is implemented using simple SQL queries that
  suggests users meeting the participation criteria. Reason for this is to maintain the efficiency of the application.
  Since the number of users can be very high, it is not feasible to fetch all users and filter them in the application
  layer.
- Similarly, the leaderboard is implemented using a simple SQL query that fetches the top 100 users based on their
  level.
- To validate the partnership status, the application uses a SQL query that checks if the partnership exists and is in
  the pending state. This is done to ensure that users can only accept or reject pending partnerships. If one of the
  users are already in a partnership, they cannot accept a new partnership. So, when a user accepts a partnership, all
  pending partnerships having the same sender or receiver ID are removed from the database.
- If one of the users have already sent an invitation to the other user and rejected before. In this case, the user can
  not send a new invitation to the same user. To do so, rejected partnerships are not removed from the database.
  Instead, they are updated with a rejected status.
- There is a reset endpoint to remove all partnerships from the database. This should be used when the pop the balloon
  event is over. The application does not remove users from the database, only partnerships.
 



