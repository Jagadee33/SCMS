# Setup Guide for Smart College Management System

## Prerequisites

- **Java 11** or higher
- **Maven** 3.6 or higher
- **MySQL** 8.0 or higher
- **Node.js** 16 or higher
- **npm** or **yarn**

## Database Setup

1. **Install MySQL** if not already installed
2. **Start MySQL service**
   ```bash
   # On macOS with Homebrew
   brew services start mysql
   
   # On Ubuntu/Debian
   sudo systemctl start mysql
   ```

3. **Create database and run schema**
   ```bash
   mysql -u root -p
   ```
   Enter your MySQL password when prompted.

4. **Execute the database schema**
   ```sql
   source /path/to/project/database/schema.sql;
   ```
   Or run directly:
   ```bash
   mysql -u root -p < database/schema.sql
   ```

## Backend Setup

1. **Navigate to backend directory**
   ```bash
   cd backend
   ```

2. **Configure database connection**
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/college_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   spring.datasource.username=your_mysql_username
   spring.datasource.password=your_mysql_password
   ```

3. **Build and run the application**
   ```bash
   # Clean and compile
   mvn clean install
   
   # Run the application
   mvn spring-boot:run
   ```
   
   The backend will start on `http://localhost:8080`

## Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   # or
   yarn install
   ```

3. **Start the development server**
   ```bash
   npm start
   # or
   yarn start
   ```
   
   The frontend will start on `http://localhost:3000`

## Default Credentials

The system comes with pre-configured demo accounts:

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@college.com | admin123 |
| Faculty | faculty@college.com | faculty123 |
| Student | student@college.com | student123 |

## Verification

1. **Backend Verification**
   - Open `http://localhost:8080/api/auth/me` in your browser
   - You should see a 401 Unauthorized response (this is expected)
   - Test login endpoint using Postman or curl:
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"email":"admin@college.com","password":"admin123"}'
   ```

2. **Frontend Verification**
   - Open `http://localhost:3000` in your browser
   - You should see the login page
   - Try logging in with any of the demo credentials

## Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Ensure MySQL is running
   - Check database credentials in `application.properties`
   - Verify the database `college_management` exists

2. **Port Already in Use**
   - Backend: Change port in `application.properties` (`server.port=8080`)
   - Frontend: The React dev server will automatically find an available port

3. **CORS Issues**
   - Ensure the frontend URL is in the allowed origins list in `application.properties`
   - Default configuration allows `http://localhost:3000`

4. **Build Failures**
   - Clear Maven cache: `mvn clean`
   - Clear npm cache: `npm cache clean --force`
   - Delete `node_modules` and reinstall: `rm -rf node_modules && npm install`

### Logs

- **Backend logs**: Check console output for Spring Boot application
- **Frontend logs**: Check browser developer console

## Development Tips

1. **Hot Reload**: Both frontend and backend support hot reload during development
2. **API Testing**: Use tools like Postman or Insomnia to test backend APIs
3. **Database Management**: Use MySQL Workbench or phpMyAdmin for database management
4. **Code Quality**: The project includes linting configurations for both frontend and backend

## Production Deployment

For production deployment, consider:

1. **Database**: Use a production MySQL instance
2. **Backend**: Build JAR file and deploy to a server
3. **Frontend**: Build static files and serve via Nginx or similar
4. **Security**: Update JWT secrets and use HTTPS
5. **Environment Variables**: Use environment variables for sensitive configuration

## Support

If you encounter any issues not covered in this guide:

1. Check the application logs for detailed error messages
2. Verify all prerequisites are installed and running
3. Ensure database schema is properly imported
4. Check network connectivity between frontend and backend
