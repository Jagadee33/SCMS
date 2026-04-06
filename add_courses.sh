#!/bin/bash

# Script to add 6 Computer Science courses to the Smart College Management System
# Make sure the backend server is running on localhost:8082 before executing this script

BASE_URL="http://localhost:8082/api"

echo "🚀 Adding Computer Science courses to the database..."

# Course 1: Data Structures and Algorithms
curl -X POST "$BASE_URL/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Data Structures and Algorithms",
    "code": "CS201",
    "description": "Study of fundamental data structures including arrays, linked lists, stacks, queues, trees, and graphs. Algorithm analysis and design techniques including sorting, searching, and dynamic programming.",
    "department": "Computer Science",
    "instructor": "Dr. Sarah Johnson",
    "credits": 4,
    "duration": "16 weeks",
    "level": "Undergraduate",
    "startDate": "2024-08-01",
    "endDate": "2024-12-15",
    "status": "Active"
  }'

echo -e "\n✅ Added: Data Structures and Algorithms"

# Course 2: Database Management Systems
curl -X POST "$BASE_URL/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Database Management Systems",
    "code": "CS301",
    "description": "Comprehensive study of database design, implementation, and management. Topics include relational database theory, SQL, normalization, transaction management, and NoSQL databases.",
    "department": "Computer Science",
    "instructor": "Prof. Michael Chen",
    "credits": 4,
    "duration": "16 weeks",
    "level": "Undergraduate",
    "startDate": "2024-08-01",
    "endDate": "2024-12-15",
    "status": "Active"
  }'

echo -e "\n✅ Added: Database Management Systems"

# Course 3: Web Development and Design
curl -X POST "$BASE_URL/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Web Development and Design",
    "code": "CS202",
    "description": "Modern web development covering HTML5, CSS3, JavaScript, React, and responsive design. Frontend and backend development principles including REST APIs and database integration.",
    "department": "Computer Science",
    "instructor": "Dr. Emily Rodriguez",
    "credits": 3,
    "duration": "16 weeks",
    "level": "Undergraduate",
    "startDate": "2024-08-01",
    "endDate": "2024-12-15",
    "status": "Active"
  }'

echo -e "\n✅ Added: Web Development and Design"

# Course 4: Machine Learning Fundamentals
curl -X POST "$BASE_URL/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Machine Learning Fundamentals",
    "code": "CS401",
    "description": "Introduction to machine learning concepts including supervised and unsupervised learning, neural networks, deep learning, and practical applications using Python and TensorFlow.",
    "department": "Computer Science",
    "instructor": "Dr. Raj Patel",
    "credits": 4,
    "duration": "16 weeks",
    "level": "Undergraduate",
    "startDate": "2024-08-01",
    "endDate": "2024-12-15",
    "status": "Active"
  }'

echo -e "\n✅ Added: Machine Learning Fundamentals"

# Course 5: Operating Systems
curl -X POST "$BASE_URL/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Operating Systems",
    "code": "CS302",
    "description": "Study of operating system principles including process management, memory management, file systems, and security. Hands-on experience with Linux/Unix systems and kernel programming.",
    "department": "Computer Science",
    "instructor": "Prof. David Kim",
    "credits": 4,
    "duration": "16 weeks",
    "level": "Undergraduate",
    "startDate": "2024-08-01",
    "endDate": "2024-12-15",
    "status": "Active"
  }'

echo -e "\n✅ Added: Operating Systems"

# Course 6: Software Engineering Principles
curl -X POST "$BASE_URL/courses" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Software Engineering Principles",
    "code": "CS351",
    "description": "Comprehensive software engineering methodology covering requirements analysis, system design, testing, maintenance, and project management. Agile methodologies and version control with Git.",
    "department": "Computer Science",
    "instructor": "Dr. Lisa Anderson",
    "credits": 3,
    "duration": "16 weeks",
    "level": "Undergraduate",
    "startDate": "2024-08-01",
    "endDate": "2024-12-15",
    "status": "Active"
  }'

echo -e "\n✅ Added: Software Engineering Principles"

echo -e "\n🎉 Successfully added all 6 Computer Science courses!"
echo "📚 You can now view them in the Courses Management page."
