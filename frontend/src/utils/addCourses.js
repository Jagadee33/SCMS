// Utility function to add Computer Science courses
// Run this in browser console or add to Courses.js component

const addComputerScienceCourses = async () => {
  const courses = [
    {
      name: "Data Structures and Algorithms",
      code: "CS201",
      description: "Study of fundamental data structures including arrays, linked lists, stacks, queues, trees, and graphs. Algorithm analysis and design techniques including sorting, searching, and dynamic programming.",
      department: "Computer Science",
      instructor: "Dr. Sarah Johnson",
      credits: 4,
      duration: "16 weeks",
      startDate: "2024-08-01",
      endDate: "2024-12-15",
      status: "Active"
    },
    {
      name: "Database Management Systems",
      code: "CS301",
      description: "Comprehensive study of database design, implementation, and management. Topics include relational database theory, SQL, normalization, transaction management, and NoSQL databases.",
      department: "Computer Science",
      instructor: "Prof. Michael Chen",
      credits: 4,
      duration: "16 weeks",
      startDate: "2024-08-01",
      endDate: "2024-12-15",
      status: "Active"
    },
    {
      name: "Web Development and Design",
      code: "CS202",
      description: "Modern web development covering HTML5, CSS3, JavaScript, React, and responsive design. Frontend and backend development principles including REST APIs and database integration.",
      department: "Computer Science",
      instructor: "Dr. Emily Rodriguez",
      credits: 3,
      duration: "16 weeks",
      startDate: "2024-08-01",
      endDate: "2024-12-15",
      status: "Active"
    },
    {
      name: "Machine Learning Fundamentals",
      code: "CS401",
      description: "Introduction to machine learning concepts including supervised and unsupervised learning, neural networks, deep learning, and practical applications using Python and TensorFlow.",
      department: "Computer Science",
      instructor: "Dr. Raj Patel",
      credits: 4,
      duration: "16 weeks",
      startDate: "2024-08-01",
      endDate: "2024-12-15",
      status: "Active"
    },
    {
      name: "Operating Systems",
      code: "CS302",
      description: "Study of operating system principles including process management, memory management, file systems, and security. Hands-on experience with Linux/Unix systems and kernel programming.",
      department: "Computer Science",
      instructor: "Prof. David Kim",
      credits: 4,
      duration: "16 weeks",
      startDate: "2024-08-01",
      endDate: "2024-12-15",
      status: "Active"
    },
    {
      name: "Software Engineering Principles",
      code: "CS351",
      description: "Comprehensive software engineering methodology covering requirements analysis, system design, testing, maintenance, and project management. Agile methodologies and version control with Git.",
      department: "Computer Science",
      instructor: "Dr. Lisa Anderson",
      credits: 3,
      duration: "16 weeks",
      startDate: "2024-08-01",
      endDate: "2024-12-15",
      status: "Active"
    }
  ];

  try {
    const responses = await Promise.all(
      courses.map(course => 
        fetch('http://localhost:8082/api/courses', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(course)
        })
      )
    );

    const results = await Promise.all(responses.map(r => r.json()));
    console.log('✅ Successfully added Computer Science courses:', results);
    return results;
  } catch (error) {
    console.error('❌ Error adding courses:', error);
    return error;
  }
};

// Export for use in components
export default addComputerScienceCourses;
