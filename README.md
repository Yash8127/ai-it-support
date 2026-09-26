<div align="center">

# 🤖 AI IT Support

### Intelligent IT Service Management with Spring Boot & Ollama

<p>
A full-stack AI-powered IT Service Management (ITSM) application designed to simplify IT support ticket management, secure user access, ticket tracking, notifications, analytics, and AI-assisted support operations.
</p>

<p>
  <img src="https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=openjdk" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring_Boot-4.1.0-6DB33F?style=for-the-badge&logo=springboot" alt="Spring Boot">
  <img src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity" alt="Spring Security JWT">
  <img src="https://img.shields.io/badge/Spring_AI-2.0.0-6DB33F?style=for-the-badge&logo=spring" alt="Spring AI">
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=for-the-badge&logo=mysql" alt="MySQL">
  <img src="https://img.shields.io/badge/Ollama-Local_AI-black?style=for-the-badge" alt="Ollama">
  <img src="https://img.shields.io/badge/Llama_3.2-Model-7A5AF8?style=for-the-badge" alt="Llama 3.2">
  <img src="https://img.shields.io/badge/Maven-Build-C71A36?style=for-the-badge&logo=apachemaven" alt="Maven">
  <img src="https://img.shields.io/badge/Git-Version_Control-F05032?style=for-the-badge&logo=git" alt="Git">
  <img src="https://img.shields.io/badge/GitHub-Repository-181717?style=for-the-badge" alt="GitHub">
</p>

------------------------------------------------------------------------

### 👨‍💻 Developed By

## **Gurindapalli Yaswanth Varma**

### Java Full Stack Developer

📧 **Email:** <a href="mailto:yashvarmagurindapalli@gmail.com">yashvarmagurindapalli@gmail.com</a>

🔗 **GitHub:** <https://github.com/Yash8127>

</div>

------------------------------------------------------------------------

# 📖 Project Overview

AI IT Support is a full-stack IT Service Management (ITSM) application
built to simulate a real-world IT support environment.

The application allows authenticated users to create and manage support
tickets while providing administrators with additional management
capabilities. It also integrates a locally hosted AI model through
Spring AI and Ollama to assist with supported ticket-related operations.

The project demonstrates practical implementation of:

- Java and Spring Boot backend development
- REST API design
- JWT authentication
- Role-based authorization
- BCrypt password hashing
- Ticket ownership and access control
- MySQL database persistence
- Ticket history and notifications
- Email-based password recovery
- Local AI integration using Spring AI and Ollama
- React frontend integration

------------------------------------------------------------------------

# ✨ Key Features

## 🔐 Authentication & Security

- User registration
- User login
- JWT-based authentication
- Stateless authentication using Spring Security
- BCrypt password hashing
- Role-based authorization
- USER and ADMIN roles
- Protected REST APIs
- Ticket ownership validation
- Secure password reset workflow

------------------------------------------------------------------------

## 🎫 Ticket Management

- Create support tickets
- View tickets
- Update ticket information
- Search tickets
- Filter tickets by status
- Filter tickets by priority
- Manage ticket status
- Manage ticket priority
- Delete tickets according to authorization rules
- Associate tickets with their owner

------------------------------------------------------------------------

## 📝 Ticket History

The application maintains ticket activity history.

The history can record operations such as:

- Ticket creation
- Ticket updates
- Status changes
- Ticket deletion

Ticket history is designed to remain available even after the associated
ticket is deleted.

------------------------------------------------------------------------

## 📊 Dashboard Analytics

The dashboard provides ticket-related analytics including:

- Total tickets
- Open tickets
- Closed tickets
- Ticket status statistics
- Ticket priority statistics

------------------------------------------------------------------------

## 🔔 Notifications

The notification module supports:

- Viewing notifications
- Checking unread notification count
- Marking notifications as read
- User-specific notifications

------------------------------------------------------------------------

## 🔑 Forgot Password & Password Reset

The application provides an email-based password recovery workflow.

``` text
                 Forgot Password
                        │
                        ▼
                  Enter Email
                        │
                        ▼
             Generate Secure Token
                        │
                        ▼
               Send Reset Email
                        │
                        ▼
                 Open Reset Link
                        │
                        ▼
                Validate Token
                        │
                        ▼
                 Set New Password
                        │
                        ▼
                Invalidate Token
```

Security measures include:

- Secure random reset tokens
- 15-minute token expiration
- Single-use reset tokens
- Protection against reusing the current password
- Generic response for unknown email addresses

------------------------------------------------------------------------

## 🤖 AI IT Support Assistant

The project integrates **Spring AI** with **Ollama** to provide
AI-assisted ticket operations.

The current local model is:

``` text
llama3.2:latest
```

Supported AI operations include:

- Searching tickets
- Finding tickets by status
- Finding tickets by priority
- Finding critical or urgent tickets
- Searching laptop-related tickets
- Performing supported ticket management operations

AI operations are routed through the application's business/service
layer so that existing authorization, ownership, and business rules are
respected.

------------------------------------------------------------------------

# 🛠️ Technology Stack

<table>
<thead>
<tr>
<th>Category</th>
<th>Technologies</th>
</tr>
</thead>
<tbody>
<tr><td>Programming Language</td><td>Java 17</td></tr>
<tr><td>Backend Framework</td><td>Spring Boot 4.1.0</td></tr>
<tr><td>REST API</td><td>Spring Web</td></tr>
<tr><td>Security</td><td>Spring Security + JWT</td></tr>
<tr><td>Password Security</td><td>BCrypt</td></tr>
<tr><td>ORM</td><td>Spring Data JPA / Hibernate</td></tr>
<tr><td>Validation</td><td>Spring Validation</td></tr>
<tr><td>Email</td><td>Spring Mail</td></tr>
<tr><td>AI Framework</td><td>Spring AI 2.0.0</td></tr>
<tr><td>AI Runtime</td><td>Ollama</td></tr>
<tr><td>AI Model</td><td>Llama 3.2</td></tr>
<tr><td>Database</td><td>MySQL</td></tr>
<tr><td>Build Tool</td><td>Maven</td></tr>
<tr><td>API Testing</td><td>Postman</td></tr>
<tr><td>IDE</td><td>Eclipse</td></tr>
<tr><td>Version Control</td><td>Git</td></tr>
<tr><td>Repository</td><td>GitHub</td></tr>
</tbody>
</table>


------------------------------------------------------------------------

# 🏗️ System Architecture

The backend follows a layered architecture.

``` text
                         React Frontend
                               │
                               │ REST API
                               ▼
                       ┌─────────────────┐
                       │   Controllers   │
                       └────────┬────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │    Services     │
                       └────────┬────────┘
                                │
                                ▼
                       ┌─────────────────┐
                       │   Repositories  │
                       └────────┬────────┘
                                │
                                ▼
                         MySQL Database
```

AI-related operations use the application service layer instead of
directly modifying database records.

``` text
AI Assistant
     │
     ▼
AI Service / Tools
     │
     ▼
Application Service Layer
     │
     ├── Authorization
     ├── Ownership Checks
     └── Business Rules
     │
     ▼
Repository
     │
     ▼
MySQL
```

------------------------------------------------------------------------

# 🔒 Security Architecture

The application uses JWT-based stateless authentication.

## Authentication Flow

``` text
User
 │
 ▼
Login Request
 │
 ▼
AuthController
 │
 ▼
AuthService
 │
 ▼
Validate Credentials
 │
 ▼
Generate JWT
 │
 ▼
Frontend
 │
 ▼
Authorization: Bearer <JWT>
 │
 ▼
JwtAuthenticationFilter
 │
 ▼
Spring Security
 │
 ▼
Protected Controller
```

## Authorization

The application supports two roles:

``` text
USER
 └── Access authorized user operations

ADMIN
 └── Additional administrative operations
```

Security checks are applied to protected operations such as
administrative ticket management and status changes.

------------------------------------------------------------------------

# 🗄️ Database Design

The application uses **MySQL** with **Spring Data JPA**.

## Main Entities

``` text
User
 │
 ├────────< Ticket
 │             │
 │             └────────< Ticket History
 │
 └────────< Notification
```

### User

Stores:

- Username
- Encrypted password
- Email
- Role
- Password reset token
- Reset token expiry

### Ticket

Stores support ticket information including:

- Title
- Description
- Category
- Priority
- Status
- Owner
- Creation information

### Ticket History

Stores ticket-related activity and changes.

### Notification

Stores user-specific notification information and read status.

------------------------------------------------------------------------

# 📦 Project Structure

``` text
ai-it-support/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── yaswanth/
│   │   │           └── itsupport/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── exception/
│   │   │               ├── repository/
│   │   │               └── service/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── pom.xml
├── README.md
└── .gitignore
```

------------------------------------------------------------------------

# 📂 Major Backend Layers

## 🎮 Controller Layer

The controller layer handles HTTP requests and exposes REST APIs.

Responsibilities include:

- Receiving client requests
- Request validation
- Calling service methods
- Returning API responses

------------------------------------------------------------------------

## ⚙️ Service Layer

The service layer contains the application's business logic.

Responsibilities include:

- Authentication
- User registration
- Ticket management
- Ticket ownership validation
- Ticket history
- Notifications
- Dashboard analytics
- Password recovery
- AI-assisted operations

------------------------------------------------------------------------

## 🗄 Repository Layer

Spring Data JPA repositories handle database interaction.

Responsibilities include:

- Saving entities
- Finding records
- Updating records
- Deleting records
- Querying ticket and user information

------------------------------------------------------------------------

## 📦 Entity Layer

JPA entities represent database tables and relationships.

Major entities include:

- User
- Ticket
- Ticket History
- Notification

------------------------------------------------------------------------

# 🌐 API Overview

## 🔐 Authentication APIs

<table>
<thead>
<tr>
<th>Method</th>
<th>Endpoint</th>
<th>Description</th>
</tr>
</thead>
<tbody>
<tr><td>POST</td><td><code>/api/auth/register</code></td><td>Register a new user</td></tr>
<tr><td>POST</td><td><code>/api/auth/login</code></td><td>Authenticate user</td></tr>
<tr><td>POST</td><td><code>/api/auth/forgot-password</code></td><td>Request password reset</td></tr>
<tr><td>GET</td><td><code>/api/auth/validate-reset-token</code></td><td>Validate reset token</td></tr>
<tr><td>POST</td><td><code>/api/auth/reset-password</code></td><td>Reset password</td></tr>
</tbody>
</table>


------------------------------------------------------------------------

## 🎫 Ticket APIs

Ticket endpoints are provided through the ticket controller and support
operations such as:

- Creating tickets
- Retrieving tickets
- Updating tickets
- Searching tickets
- Updating ticket status
- Deleting tickets according to authorization rules

------------------------------------------------------------------------

## 📊 Dashboard API

<table>
<thead>
<tr>
<th>Method</th>
<th>Endpoint</th>
<th>Description</th>
</tr>
</thead>
<tbody>
<tr><td>GET</td><td><code>/api/dashboard/analytics</code></td><td>Retrieve dashboard analytics</td></tr>
</tbody>
</table>


------------------------------------------------------------------------

## 🔔 Notification APIs

<table>
<thead>
<tr>
<th>Method</th>
<th>Endpoint</th>
<th>Description</th>
</tr>
</thead>
<tbody>
<tr><td>GET</td><td><code>/api/notifications</code></td><td>Retrieve notifications</td></tr>
<tr><td>GET</td><td><code>/api/notifications/unread/count</code></td><td>Retrieve unread count</td></tr>
<tr><td>PUT</td><td><code>/api/notifications/{id}/read</code></td><td>Mark notification as read</td></tr>
</tbody>
</table>


------------------------------------------------------------------------

# ⚙️ Configuration

Sensitive configuration values are provided through environment
variables.

Example:

``` properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

jwt.secret=${JWT_SECRET}

spring.mail.username=${MAIL_USERNAME}
spring.mail.password=${MAIL_PASSWORD}

app.frontend.url=${FRONTEND_URL:http://localhost:5173}
```

## Required Environment Variables

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
MAIL_USERNAME
MAIL_PASSWORD
FRONTEND_URL
```

> Never commit database passwords, JWT secrets, email passwords, API
> keys, or other sensitive credentials to GitHub.

------------------------------------------------------------------------

# 🚀 Installation & Setup

## Prerequisites

Install the following:

- Java 17
- Maven
- MySQL
- Ollama
- Git
- Postman

------------------------------------------------------------------------

## 1. Clone the Repository

``` bash
git clone <BACKEND_REPOSITORY_URL>
cd ai-it-support
```

------------------------------------------------------------------------

## 2. Configure MySQL

Create the required MySQL database.

Then configure the database connection using:

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
```

------------------------------------------------------------------------

## 3. Configure Environment Variables

Configure:

``` text
DB_URL
DB_USERNAME
DB_PASSWORD
JWT_SECRET
MAIL_USERNAME
MAIL_PASSWORD
FRONTEND_URL
```

For local development, the frontend URL is normally:

``` text
http://localhost:5173
```

------------------------------------------------------------------------

## 4. Configure Ollama

Make sure Ollama is installed and running.

Pull the required model:

``` bash
ollama pull llama3.2:latest
```

Verify the model:

``` bash
ollama list
```

The application expects Ollama to be available at:

``` text
http://localhost:11434
```

------------------------------------------------------------------------

## 5. Run the Application

Run the Spring Boot application from Eclipse or Maven.

Using Maven:

``` bash
mvn spring-boot:run
```

The backend runs by default at:

``` text
http://localhost:8080
```

------------------------------------------------------------------------

# 🧪 API Testing

The APIs can be tested using:

- Postman
- React frontend
- Other REST clients

Protected endpoints require a valid JWT.

Example:

``` http
Authorization: Bearer <JWT_TOKEN>
```

Typical workflow:

``` text
Register
   ↓
Login
   ↓
Receive JWT
   ↓
Send JWT with protected requests
   ↓
Create / View / Manage Tickets
```

------------------------------------------------------------------------

# 🖥️ Frontend Integration

The frontend is developed separately using **React and Vite**.

The frontend communicates with this Spring Boot backend through REST
APIs.

Frontend API configuration uses:

``` env
VITE_API_BASE=http://localhost:8080
```

Frontend repository:

``` text
<FRONTEND_REPOSITORY_URL>
```

------------------------------------------------------------------------

# 📊 Current Project Capabilities

The current implementation includes:

- JWT authentication
- BCrypt password hashing
- Role-based authorization
- USER and ADMIN roles
- Ticket ownership
- Ticket management
- Ticket history
- Dashboard analytics
- Notifications
- AI-assisted ticket operations
- Spring AI integration
- Ollama integration
- Llama 3.2 local model
- Forgot password
- Email-based password reset
- Secure reset tokens
- Single-use reset tokens
- MySQL persistence
- REST APIs
- React frontend integration

------------------------------------------------------------------------

# 🧠 Skills Demonstrated

## Backend

- Java
- Spring Boot
- Spring Web
- Spring Security
- JWT
- Spring Data JPA
- Hibernate
- Spring AI
- Spring Mail
- REST API Development

## Database

- MySQL
- JPA Entity Relationships
- CRUD Operations
- Database Queries

## AI

- Spring AI
- Ollama
- Llama 3.2
- AI Tool Integration
- AI-assisted Ticket Operations

## Frontend Integration

- React
- Vite
- REST API Integration
- JWT-based API communication

## Tools

- Eclipse
- Maven
- Git
- GitHub
- Postman

------------------------------------------------------------------------

# 🎯 Project Highlights

✅ Developed a real-world IT Service Management application using **Java
and Spring Boot**

✅ Implemented **JWT authentication and role-based authorization**

✅ Implemented **ticket ownership and access-control rules**

✅ Built **ticket history and notification systems**

✅ Implemented **secure forgot-password and email reset functionality**

✅ Integrated **Spring AI with Ollama and Llama 3.2**

✅ Designed a layered backend architecture using **Controllers,
Services, Repositories, and Entities**

✅ Integrated a **React frontend with REST APIs**

✅ Used **environment variables for sensitive configuration**

✅ Managed source code using **Git and GitHub**

------------------------------------------------------------------------

# 🚀 Future Enhancements

Possible future improvements include:

- Production deployment
- Automated unit and integration testing
- CI/CD pipeline
- Refresh token support
- File attachments for tickets
- Advanced dashboard analytics
- Improved AI response handling
- Production database configuration
- Docker support
- Enhanced audit logging

------------------------------------------------------------------------

# 📌 Learning Outcomes

This project provided practical experience in:

- Building REST APIs using Spring Boot
- Implementing JWT authentication
- Implementing role-based authorization
- Designing relational database relationships
- Using Spring Data JPA and Hibernate
- Implementing secure password management
- Sending transactional emails
- Integrating AI into a Spring Boot application
- Connecting Ollama with Spring AI
- Integrating React with Spring Boot
- Debugging full-stack applications
- Managing projects using Git and GitHub

------------------------------------------------------------------------

# 🤝 Contribution

This project is primarily developed as a personal portfolio and learning
project.

Suggestions and improvements are welcome.

If you would like to explore the project:

1.  Fork the repository.
2.  Create a feature branch.
3.  Make your changes.
4.  Commit your changes.
5.  Push the branch.
6.  Open a Pull Request.

------------------------------------------------------------------------

# 👨‍💻 About the Developer

## Gurindapalli Yaswanth Varma

**Java Full Stack Developer**

Interested in building secure, scalable, and user-friendly applications
using Java, Spring Boot, React, MySQL, and modern AI technologies.

### Technical Interests

- Java
- Spring Boot
- Full Stack Development
- REST APIs
- Database Design
- Spring Security
- AI Integration

------------------------------------------------------------------------

# 📬 Contact

### 📧 Email

<a href="mailto:yashvarmagurindapalli@gmail.com">yashvarmagurindapalli@gmail.com</a>

### 💻 GitHub

<a href="https://github.com/Yash8127">https://github.com/Yash8127</a>

### 💼 LinkedIn

<a href="https://www.linkedin.com/in/yaswanth-gurindapalli/">https://www.linkedin.com/in/yaswanth-gurindapalli/</a>

------------------------------------------------------------------------

# 📄 License

This project is created for <strong>learning, educational, and portfolio purposes</strong>.

------------------------------------------------------------------------

<div align="center">

## ⭐ Thank You for Visiting ⭐

### If you like this project, consider giving the repository a ⭐ Star.

<strong>Happy Coding! 🚀</strong>

Developed with ❤️ by <strong>Gurindapalli Yaswanth Varma</strong>

</div>
