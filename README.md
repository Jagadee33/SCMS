# SCMS

Smart College Management System

# Smart College Management System

A comprehensive college management system built with React.js frontend, Java Spring Boot backend, and MySQL database.

## Technology Stack

- **Frontend**: React.js with TailwindCSS and shadcn/ui components
- **Backend**: Java Spring Boot with JDBC for database connectivity
- **Database**: MySQL
- **Authentication**: JWT tokens
- **API**: RESTful APIs

## Features

### Core Modules

1. **User Authentication & Authorization**

   - Login/Logout functionality
   - Role-based access control (Admin, Faculty, Student)
   - JWT token management

2. **Student Management**

   - Student registration and profile management
   - Academic records tracking
   - Attendance management

3. **Faculty Management**

   - Faculty profile management
   - Course assignment
   - Performance evaluation

4. **Course Management**

   - Course creation and scheduling
   - Enrollment management
   - Grade management

5. **Department Management**

   - Department creation and management
   - Faculty assignment to departments

6. **Attendance System**

   - Mark and track attendance
   - Generate attendance reports
   - Attendance analytics

7. **Examination Management**

   - Exam scheduling
   - Grade entry and management
   - Result generation

8. **Library Management**

   - Book catalog management
   - Issue/return tracking
   - Fine management

9. **Fee Management**
   - Fee structure management
   - Payment tracking
   - Receipt generation

## Project Structure

```
smart-college-management-system/
├── frontend/                 # React.js frontend application
│   ├── public/
│   ├── src/
│   │   ├── components/       # Reusable components
│   │   ├── pages/           # Page components
│   │   ├── services/        # API services
│   │   ├── utils/           # Utility functions
│   │   ├── hooks/           # Custom hooks
│   │   └── styles/          # CSS and styles
│   ├── package.json
│   └── tailwind.config.js
├── backend/                 # Java Spring Boot backend
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/college/
│   │   │   │       ├── controller/
│   │   │   │       ├── model/
│   │   │   │       ├── repository/
│   │   │   │       ├── service/
│   │   │   │       ├── config/
│   │   │   │       └── security/
│   │   │   └── resources/
│   │   └── test/
│   ├── pom.xml
│   └── application.properties
├── database/                # Database scripts and migrations
│   ├── schema.sql
│   └── data.sql
└── docs/                   # Documentation
    ├── api.md
    └── setup.md
```

## Setup Instructions

### Prerequisites

- Node.js (v16 or higher)
- Java 11 or higher
- Maven
- MySQL Server

### Installation

1. **Clone the repository**

   ```bash
   git clone <repository-url>
   cd smart-college-management-system
   ```

2. **Database Setup**

   - Create MySQL database named `college_management`
   - Run the database scripts from `database/` directory

3. **Backend Setup**

   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

4. **Frontend Setup**
   ```bash
   cd frontend
   npm install
   npm start
   ```

### Default Credentials

- **Admin**: admin@college.com / admin123
- **Faculty**: faculty@college.com / faculty123
- **Student**: student@college.com / student123

## API Documentation

The API documentation is available at `http://localhost:8080/api-docs` when the backend is running.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License.
