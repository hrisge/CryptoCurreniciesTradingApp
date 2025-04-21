# Crypto Trading Application

A full-stack cryptocurrency trading platform built with a Java Spring Boot backend and a modern React-powered frontend.

---

## Project Structure

```
crypto-trading/
├── backend/      # Java Spring Boot backend (API server)
└── frontend/     # React + JavaScript frontend (UI)
```

---

## Prerequisites

Before running the application, ensure you have the following installed:

- [Node.js](https://nodejs.org/) (v16 or newer)
- [Java JDK](https://www.oracle.com/java/technologies/javase-downloads.html) (v17+ recommended)
- [Maven](https://maven.apache.org/) (or use the included Maven wrapper)
- [PostgreSQL](https://www.postgresql.org/)

---

## PostgreSQL Setup

1. **Install PostgreSQL**  
   Download and install from [https://www.postgresql.org/download/](https://www.postgresql.org/download/)

2. **Create the database and schema**

```sql
CREATE DATABASE cryptotrading;
\c cryptotrading
CREATE SCHEMA cryptotrading;
```

3. **Configure credentials**

Ensure the following values are correct (or update them) in `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cryptotrading?currentSchema=cryptotrading
spring.datasource.username=postgres
spring.datasource.password= ***
```


## Backend Setup (Spring Boot)

1. Open a terminal and navigate to the backend directory:

```bash
cd backend
```

2. Build and run the Spring Boot application:

#### Option 1: Using Maven (installed globally)

```bash
mvn clean install
mvn spring-boot:run
```

#### Option 2: Using Maven Wrapper (included in project)

```bash
./mvnw clean install      # Unix/macOS
./mvnw.cmd clean install  # Windows

./mvnw spring-boot:run
```

The backend server will start at:  
 `http://localhost:8081`

---

### Frontend Setup

1. Open a new terminal window/tab and navigate to the frontend directory:

```bash
cd frontend
```

2. Install the required packages:

```bash
npm install
```

The frontend will be accessible at:  
 `http://localhost:5173`


---


