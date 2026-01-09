# Bidding System Application - University Project

A RESTful bidding API built with Spring Boot that provides automated auction management with real-time bid validation and secure JWT authentication.

[![Java](https://img.shields.io/badge/Java-17-007396?style=flat-square&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-6DB33F?style=flat-square&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-4169E1?style=flat-square&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Swagger](https://img.shields.io/badge/Swagger-3.0-85EA2D?style=flat-square&logo=swagger&logoColor=black)](https://swagger.io/)

## 📋 Table of Contents
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Application Usage](#application-usage)

## ✨ Features

### Core Functionality
* **JWT-based user authentication**
* **Auction management**
* **Real-time bidding system**
* **Inventory & items tracking**

### Business Logic
* **Bid validation via minimum increments**
* **Highest bidder tracker**
* **Auction expiration & status management**
* **Reserve price verification for ownership transfer**

---

## 🛠 Technology Stack

| Layer | Technology |
| :--- | :--- |
| **Framework** | Spring Boot 3.5.7 |
| **Security** | Spring Security + JWT (jjwt 0.13.0) |
| **Database** | PostgreSQL |
| **ORM** | Spring Data JPA (Hibernate) |
| **API Docs** | SpringDoc OpenAPI 2.8.14 |
| **Utilities** | Lombok |
| **Build Tool** | Maven |

---

## Prerequisites

1. **Docker**
   - Install [Docker](https://hub.docker.com/).
2. **Docker Compose**
   - Pre-installed with Docker Desktop (Windows/Mac).
   - [Linux Installation Guide](https://docs.docker.com/compose/install/linux/).
3. **JDK (Java Development Kit)**
   - Version 17 or higher is recommended.
4. **Maven** (Optional)
   - For building without Docker. [Download Maven](https://maven.apache.org/download.cgi).


## Installation

### 1. Clone the Repository
```bash
git clone [https://github.com/extraxo/bidding-system.git](https://github.com/extraxo/bidding-system.git)
cd bidding-system

2. Build and Run with Docker Compose
```bash
# Build Docker images and start containers
docker-compose up --build
```

3. Run Application Locally (Optional - without Docker)
```bash
# Build the applicaiton
mvn clean install

# Run the application
mvn spring-boot:run
```

## Application usage

After Docker containers have started, both the application and the PostgreSQL server should be running on ports 8080 and 5432 respectfully.<br>

For Swagger UI access use the following link: [Swagger BiddingSystemApplication](http://localhost:8080/swagger-ui/index.html#/)
