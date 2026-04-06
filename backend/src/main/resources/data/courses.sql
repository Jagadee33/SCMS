-- Computer Science Courses Data for Smart College Management System
-- These courses can be added to the courses table

INSERT INTO courses (name, code, description, department, instructor, credits, duration, start_date, end_date, status, created_at, updated_at) VALUES
('Data Structures and Algorithms', 'CS201', 'Study of fundamental data structures including arrays, linked lists, stacks, queues, trees, and graphs. Algorithm analysis and design techniques including sorting, searching, and dynamic programming.', 'Computer Science', 'Dr. Sarah Johnson', 4, '16 weeks', '2024-08-01', '2024-12-15', 'Active', NOW(), NOW()),

('Database Management Systems', 'CS301', 'Comprehensive study of database design, implementation, and management. Topics include relational database theory, SQL, normalization, transaction management, and NoSQL databases.', 'Computer Science', 'Prof. Michael Chen', 4, '16 weeks', '2024-08-01', '2024-12-15', 'Active', NOW(), NOW()),

('Web Development and Design', 'CS202', 'Modern web development covering HTML5, CSS3, JavaScript, React, and responsive design. Frontend and backend development principles including REST APIs and database integration.', 'Computer Science', 'Dr. Emily Rodriguez', 3, '16 weeks', '2024-08-01', '2024-12-15', 'Active', NOW(), NOW()),

('Machine Learning Fundamentals', 'CS401', 'Introduction to machine learning concepts including supervised and unsupervised learning, neural networks, deep learning, and practical applications using Python and TensorFlow.', 'Computer Science', 'Dr. Raj Patel', 4, '16 weeks', '2024-08-01', '2024-12-15', 'Active', NOW(), NOW()),

('Operating Systems', 'CS302', 'Study of operating system principles including process management, memory management, file systems, and security. Hands-on experience with Linux/Unix systems and kernel programming.', 'Computer Science', 'Prof. David Kim', 4, '16 weeks', '2024-08-01', '2024-12-15', 'Active', NOW(), NOW()),

('Software Engineering Principles', 'CS351', 'Comprehensive software engineering methodology covering requirements analysis, system design, testing, maintenance, and project management. Agile methodologies and version control with Git.', 'Computer Science', 'Dr. Lisa Anderson', 3, '16 weeks', '2024-08-01', '2024-12-15', 'Active', NOW(), NOW());
