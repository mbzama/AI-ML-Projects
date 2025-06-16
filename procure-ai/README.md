# Procure AI - Procurement Management System

A comprehensive procurement management system with auction capabilities, vendor management, and RFQ processing.

## Project Structure

This project consists of two main components:
- **procure-ai-api**: Spring Boot backend API
- **procure-ai-web**: React frontend application with Vite

## Prerequisites

### Backend Requirements
- Java 21 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

### Frontend Requirements
- Node.js 18 or higher
- npm or yarn package manager

### Database Setup
1. Install PostgreSQL and ensure it's running
2. Create a database named `procure_ai`
3. Update database credentials in `procure-ai-api/src/main/resources/application.properties` if needed:
   - Default host: localhost:5435
   - Default username: postgres
   - Default password: sillicon

## Running the Application

### Option 1: Run Backend and Frontend Separately

#### Backend (Spring Boot API)
1. Navigate to the backend directory:
   ```
   cd procure-ai-api
   ```

2. Run using Maven:
   ```
   mvn spring-boot:run
   ```
   
   Or build and run the JAR:
   ```
   mvn clean package -DskipTests
   java -jar target/procure-ai-api-1.0.0.jar
   ```

3. The backend API will be available at: **http://localhost:8080/api**

#### Frontend (React with Vite)
1. Navigate to the frontend directory:
   ```
   cd procure-ai-web
   ```

2. Install dependencies:
   ```
   npm install
   ```

3. Start the development server:
   ```
   npm run dev
   ```

4. The frontend application will be available at: **http://localhost:5173**

### Option 2: Production Build

#### Backend
```
cd procure-ai-api
mvn clean package -DskipTests
java -jar target/procure-ai-api-1.0.0.jar
```

#### Frontend
```
cd procure-ai-web
npm run build
npm run preview
```

## Application URLs

### Development Environment
- **Frontend Application**: http://localhost:5173
- **Backend API**: http://localhost:8080/api
- **API Documentation**: http://localhost:8080/api/swagger-ui.html (if Swagger is configured)

### Default Login Credentials
The application comes with pre-seeded data. Check the `data.sql` file for default user credentials.

## Available Features

### Admin Dashboard
- **URL**: http://localhost:5173/admin/dashboard
- View counts of Vendors, Auctions, and RFQs
- Comprehensive system overview

### Admin Vendor Management
- **URL**: http://localhost:5173/admin/vendors
- Add, remove, update, and list vendors
- Complete vendor lifecycle management

### Admin Auction Management
- **URL**: http://localhost:5173/admin/auctions
- Create, manage, and monitor auctions
- Auction configuration and bidding oversight

### Vendor Dashboard
- **URL**: http://localhost:5173/vendor/dashboard
- View auction summaries and opportunities
- Track bidding activities

### Vendor Auction Participation
- **URL**: http://localhost:5173/vendor/auctions
- Browse available auctions
- Submit bids and participate in bidding process

## API Endpoints

The backend provides RESTful APIs for:
- User authentication and authorization
- Vendor management (CRUD operations)
- Auction management (creation, bidding, monitoring)
- RFQ processing
- Dashboard analytics

## Technology Stack

### Backend
- Spring Boot 3.5.0
- Spring Security for authentication
- Spring Data JPA for database operations
- PostgreSQL database
- Maven for dependency management

### Frontend
- React 18 with TypeScript
- Vite for build tooling and development server
- Mantine UI components
- Tailwind CSS for styling
- Axios for API communication

## Development Notes

- The frontend is configured to proxy API requests to the backend during development
- CORS is configured in the backend to allow frontend requests
- The application uses JWT tokens for authentication
- Database schema is auto-generated using JPA annotations

## Troubleshooting

### Common Issues
1. **Database Connection**: Ensure PostgreSQL is running and credentials are correct
2. **Port Conflicts**: Backend runs on 8080, frontend on 5173 - ensure these ports are available
3. **CORS Issues**: Backend is configured for frontend origin, check CORS configuration if needed
4. **Build Issues**: Ensure all dependencies are installed and versions match requirements

### Database Reset
To reset the database with fresh data:
1. Stop the backend application
2. Set `spring.jpa.hibernate.ddl-auto=create-drop` in application.properties
3. Restart the backend - this will recreate tables and reload seed data