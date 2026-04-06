# Quick Start Guide

## 🚀 Setup MySQL Database (Required First)

### Option 1: Using Homebrew (macOS)
```bash
# Install MySQL
brew install mysql

# Start MySQL service
brew services start mysql

# Create database
mysql -u root -p
```
When prompted for password, just press Enter (default is no password).

Then run:
```sql
CREATE DATABASE college_management;
EXIT;
```

### Option 2: Manual Setup
1. Install MySQL from https://dev.mysql.com/downloads/mysql/
2. Start MySQL service
3. Open MySQL command line:
   ```bash
   mysql -u root -p
   ```
4. Create database:
   ```sql
   CREATE DATABASE college_management;
   ```

## 🔧 Update Database Credentials

Edit `backend/src/main/resources/application.properties`:

```properties
# Update these with your MySQL credentials
spring.datasource.url=jdbc:mysql://localhost:3306/college_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=your_mysql_password  # or leave empty if no password
```

## 🏃‍♂️ Run the Application

### Backend
```bash
cd backend
mvn spring-boot:run
```

### Frontend (in another terminal)
```bash
cd frontend
npm install
npm start
```

## 🔑 Test Login

Once both are running, test the API:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@college.com","password":"admin123"}'
```

## 🌐 Access the Application

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api

## 🔍 Troubleshooting

### "Access denied for user 'root'@'localhost'"
- Your MySQL password is different from what's configured
- Reset MySQL password or update application.properties

### "Database doesn't exist"
- Run: `CREATE DATABASE college_management;` in MySQL

### Port already in use
- Change port in application.properties: `server.port=8081`

### Frontend won't connect
- Ensure backend is running on port 8080
- Check CORS configuration in application.properties
