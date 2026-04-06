import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { gradeAPI, studentAPI, courseAPI, facultyAPI } from '../services/api';

const Grades = () => {
  const { hasRole, user } = useAuth();
  const [grades, setGrades] = useState([]);
  const [students, setStudents] = useState([]);
  const [courses, setCourses] = useState([]);
  const [faculty, setFaculty] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [showBatchModal, setShowBatchModal] = useState(false);
  const [showStatsModal, setShowStatsModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [stats, setStats] = useState(null);
  const [selectedStudentReport, setSelectedStudentReport] = useState(null);
  const [filter, setFilter] = useState({
    studentId: '',
    courseId: '',
    facultyId: '',
    gradeType: '',
    status: ''
  });
  const [formData, setFormData] = useState({
    studentId: '',
    courseId: '',
    facultyId: '',
    gradeType: 'ASSIGNMENT',
    gradeTitle: '',
    maxMarks: 100,
    obtainedMarks: 0,
    weightage: 10,
    status: 'DRAFT',
    feedback: '',
    remarks: ''
  });

  useEffect(() => {
    fetchGrades();
    fetchStudents();
    fetchCourses();
    fetchFaculty();
    fetchStatistics();
  }, []);

  const fetchGrades = async () => {
    try {
      setLoading(true);
      setError(null);
      console.log('Fetching grades...');
      const response = await gradeAPI.getAll();
      console.log('Grades response:', response);
      console.log('Grades data:', response.data);
      setGrades(response.data);
      console.log('Grades set successfully');
    } catch (err) {
      console.error('Error fetching grades:', err);
      console.error('Error details:', err.response?.data || err.message);
      setError('Failed to fetch grades');
    } finally {
      setLoading(false);
      console.log('Fetch grades completed');
    }
  };

  const fetchStudents = async () => {
    try {
      const response = await studentAPI.getAll();
      setStudents(response.data);
    } catch (err) {
      console.error('Error fetching students:', err);
    }
  };

  const fetchCourses = async () => {
    try {
      const response = await courseAPI.getAll();
      setCourses(response.data);
    } catch (err) {
      console.error('Error fetching courses:', err);
    }
  };

  const fetchFaculty = async () => {
    try {
      const response = await facultyAPI.getAll();
      setFaculty(response.data);
    } catch (err) {
      console.error('Error fetching faculty:', err);
    }
  };

  const fetchStatistics = async () => {
    try {
      const response = await gradeAPI.getStatistics();
      setStats(response.data);
    } catch (err) {
      console.error('Error fetching statistics:', err);
    }
  };

  // Check if user has permission to access this page
  if (!hasRole('ADMIN') && !hasRole('FACULTY')) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h1>
          <p className="text-gray-600">You don't have permission to access this page.</p>
        </div>
      </div>
    );
  }

  const filteredGrades = grades.filter(grade => {
    if (filter.studentId && grade.student?.id !== parseInt(filter.studentId)) return false;
    if (filter.courseId && grade.course?.id !== parseInt(filter.courseId)) return false;
    if (filter.facultyId && grade.faculty?.id !== parseInt(filter.facultyId)) return false;
    if (filter.gradeType && grade.gradeType !== filter.gradeType) return false;
    if (filter.status && grade.status !== filter.status) return false;
    return true;
  });

  console.log('Grades component - filtered grades count:', filteredGrades.length);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setError(null);
      
      const gradeData = {
        student: { id: parseInt(formData.studentId) },
        course: { id: parseInt(formData.courseId) },
        faculty: { id: parseInt(formData.facultyId) },
        gradeType: formData.gradeType,
        gradeTitle: formData.gradeTitle,
        maxMarks: parseFloat(formData.maxMarks),
        obtainedMarks: parseFloat(formData.obtainedMarks),
        weightage: parseFloat(formData.weightage),
        status: formData.status,
        feedback: formData.feedback,
        remarks: formData.remarks
      };

      await gradeAPI.create(gradeData);
      
      // Refresh grades list
      await fetchGrades();
      await fetchStatistics();
      resetForm();
    } catch (err) {
      console.error('Error creating grade:', err);
      setError('Failed to create grade');
    }
  };

  const handlePublish = async (gradeIds) => {
    try {
      setError(null);
      await gradeAPI.publishGrades(gradeIds);
      
      // Refresh grades list
      await fetchGrades();
      await fetchStatistics();
    } catch (err) {
      console.error('Error publishing grades:', err);
      setError('Failed to publish grades');
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this grade?')) {
      try {
        setError(null);
        await gradeAPI.delete(id);
        
        // Refresh grades list
        await fetchGrades();
        await fetchStatistics();
      } catch (err) {
        console.error('Error deleting grade:', err);
        setError('Failed to delete grade');
      }
    }
  };

  const handleViewStudentReport = async (studentId) => {
    try {
      const response = await gradeAPI.getStudentReport(studentId);
      setSelectedStudentReport(response.data);
    } catch (err) {
      console.error('Error fetching student report:', err);
      setError('Failed to fetch student report');
    }
  };

  const resetForm = () => {
    setFormData({
      studentId: '',
      courseId: '',
      facultyId: '',
      gradeType: 'ASSIGNMENT',
      gradeTitle: '',
      maxMarks: 100,
      obtainedMarks: 0,
      weightage: 10,
      status: 'DRAFT',
      feedback: '',
      remarks: ''
    });
    setShowModal(false);
  };

  const getStatusBadge = (status) => {
    const colors = {
      PUBLISHED: 'bg-green-100 text-green-800',
      DRAFT: 'bg-yellow-100 text-yellow-800',
      PENDING: 'bg-blue-100 text-blue-800'
    };
    return colors[status] || 'bg-gray-100 text-gray-800';
  };

  const getGradeTypeBadge = (type) => {
    const colors = {
      ASSIGNMENT: 'bg-purple-100 text-purple-800',
      QUIZ: 'bg-indigo-100 text-indigo-800',
      MIDTERM: 'bg-orange-100 text-orange-800',
      FINAL: 'bg-red-100 text-red-800',
      PROJECT: 'bg-pink-100 text-pink-800',
      PRACTICAL: 'bg-teal-100 text-teal-800'
    };
    return colors[type] || 'bg-gray-100 text-gray-800';
  };

  const getGradeColor = (percentage) => {
    if (percentage >= 90) return 'text-green-600 font-bold';
    if (percentage >= 80) return 'text-blue-600 font-semibold';
    if (percentage >= 70) return 'text-yellow-600 font-semibold';
    if (percentage >= 60) return 'text-orange-600 font-semibold';
    return 'text-red-600 font-semibold';
  };

  if (loading) {
    console.log('Grades component - loading state, grades count:', grades.length);
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg text-gray-600">Loading grades...</div>
      </div>
    );
  }

  if (error) {
    console.log('Grades component - error state:', error);
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg text-red-600">Error: {error}</div>
      </div>
    );
  }

  console.log('Grades component - rendering with grades:', grades.length, 'grades');

  return (
    <div className="container mx-auto px-4 py-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Grade Management</h1>
        <div className="flex items-center space-x-4">
          <button
            onClick={() => setShowStatsModal(true)}
            className="bg-purple-600 text-white px-4 py-2 rounded-lg hover:bg-purple-700 transition-colors flex items-center"
          >
            <svg className="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
            </svg>
            Statistics
          </button>
          <button
            onClick={() => setShowModal(true)}
            className="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors flex items-center"
          >
            <svg className="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Add Grade
          </button>
          <button
            onClick={() => {
              console.log('Manual test - fetching grades...');
              fetchGrades();
            }}
            className="bg-orange-600 text-white px-4 py-2 rounded-lg hover:bg-orange-700 transition-colors flex items-center"
          >
            <svg className="h-5 w-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            Test API
          </button>
        </div>
      </div>

      {/* Statistics Cards */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
          <div className="bg-white shadow-lg rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0 bg-blue-100 rounded-lg p-3">
                <svg className="h-6 w-6 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                </svg>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900">Total Grades</h3>
                <p className="text-2xl font-bold text-gray-600">{stats.totalGrades}</p>
              </div>
            </div>
          </div>

          <div className="bg-white shadow-lg rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0 bg-green-100 rounded-lg p-3">
                <svg className="h-6 w-6 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900">Published</h3>
                <p className="text-2xl font-bold text-green-600">{stats.publishedGrades}</p>
              </div>
            </div>
          </div>

          <div className="bg-white shadow-lg rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0 bg-yellow-100 rounded-lg p-3">
                <svg className="h-6 w-6 text-yellow-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900">Draft</h3>
                <p className="text-2xl font-bold text-yellow-600">{stats.draftGrades}</p>
              </div>
            </div>
          </div>

          <div className="bg-white shadow-lg rounded-lg p-6">
            <div className="flex items-center">
              <div className="flex-shrink-0 bg-purple-100 rounded-lg p-3">
                <svg className="h-6 w-6 text-purple-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 7h8m0 0v8m0-8l-8 8-4-4-6 6" />
                </svg>
              </div>
              <div className="ml-4">
                <h3 className="text-lg font-semibold text-gray-900">Avg Percentage</h3>
                <p className="text-2xl font-bold text-purple-600">{stats.averagePercentage?.toFixed(1)}%</p>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Filters */}
      <div className="bg-white shadow-lg rounded-lg p-6 mb-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Filters</h3>
        <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
          <div>
            <label className="block text-sm font-medium text-gray-700">Student</label>
            <select
              value={filter.studentId}
              onChange={(e) => setFilter(prev => ({ ...prev, studentId: e.target.value }))}
              className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
            >
              <option value="">All Students</option>
              {students.map(student => (
                <option key={student.id} value={student.id}>
                  {student.firstName} {student.lastName}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Course</label>
            <select
              value={filter.courseId}
              onChange={(e) => setFilter(prev => ({ ...prev, courseId: e.target.value }))}
              className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
            >
              <option value="">All Courses</option>
              {courses.map(course => (
                <option key={course.id} value={course.id}>
                  {course.name}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Faculty</label>
            <select
              value={filter.facultyId}
              onChange={(e) => setFilter(prev => ({ ...prev, facultyId: e.target.value }))}
              className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
            >
              <option value="">All Faculty</option>
              {faculty.map(fac => (
                <option key={fac.id} value={fac.id}>
                  {fac.firstName} {fac.lastName}
                </option>
              ))}
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Grade Type</label>
            <select
              value={filter.gradeType}
              onChange={(e) => setFilter(prev => ({ ...prev, gradeType: e.target.value }))}
              className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
            >
              <option value="">All Types</option>
              <option value="ASSIGNMENT">Assignment</option>
              <option value="QUIZ">Quiz</option>
              <option value="MIDTERM">Midterm</option>
              <option value="FINAL">Final</option>
              <option value="PROJECT">Project</option>
              <option value="PRACTICAL">Practical</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-medium text-gray-700">Status</label>
            <select
              value={filter.status}
              onChange={(e) => setFilter(prev => ({ ...prev, status: e.target.value }))}
              className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
            >
              <option value="">All Status</option>
              <option value="PUBLISHED">Published</option>
              <option value="DRAFT">Draft</option>
              <option value="PENDING">Pending</option>
            </select>
          </div>
        </div>
      </div>

      {/* Grades Table */}
      <div className="bg-white shadow-lg rounded-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Title</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Marks</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Grade</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {filteredGrades.length === 0 ? (
                <tr>
                  <td colSpan="9" className="px-6 py-12 text-center text-gray-500">
                    No grades found matching your filters.
                  </td>
                </tr>
              ) : (
                filteredGrades.map((grade) => (
                  <tr key={grade.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {grade.id}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {grade.student?.firstName} {grade.student?.lastName}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {grade.course?.name}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getGradeTypeBadge(grade.gradeType)}`}>
                        {grade.gradeType}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {grade.gradeTitle}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {grade.obtainedMarks}/{grade.maxMarks}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm">
                        <span className={getGradeColor(grade.percentage)}>
                          {grade.percentage?.toFixed(1)}%
                        </span>
                        <div className="text-xs text-gray-500">
                          {grade.gradeLetter} ({grade.gradePoints})
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadge(grade.status)}`}>
                        {grade.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <div className="flex space-x-2">
                        {grade.status === 'DRAFT' && (
                          <button
                            onClick={() => handlePublish([grade.id])}
                            className="text-green-600 hover:text-green-900 transition-colors"
                          >
                            Publish
                          </button>
                        )}
                        <button
                          onClick={() => handleViewStudentReport(grade.student?.id)}
                          className="text-blue-600 hover:text-blue-900 transition-colors"
                        >
                          Report
                        </button>
                        <button
                          onClick={() => handleDelete(grade.id)}
                          className="text-red-600 hover:text-red-900 transition-colors"
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Add Grade Modal */}
      {showModal && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg leading-6 font-medium text-gray-900">Add Grade</h3>
              <div className="mt-2">
                <form onSubmit={handleSubmit}>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700">Student</label>
                      <select
                        name="studentId"
                        value={formData.studentId}
                        onChange={handleInputChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                        required
                      >
                        <option value="">Select Student</option>
                        {students.map(student => (
                          <option key={student.id} value={student.id}>
                            {student.firstName} {student.lastName}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700">Course</label>
                      <select
                        name="courseId"
                        value={formData.courseId}
                        onChange={handleInputChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                        required
                      >
                        <option value="">Select Course</option>
                        {courses.map(course => (
                          <option key={course.id} value={course.id}>
                            {course.name}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700">Faculty</label>
                      <select
                        name="facultyId"
                        value={formData.facultyId}
                        onChange={handleInputChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                        required
                      >
                        <option value="">Select Faculty</option>
                        {faculty.map(fac => (
                          <option key={fac.id} value={fac.id}>
                            {fac.firstName} {fac.lastName}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700">Grade Type</label>
                      <select
                        name="gradeType"
                        value={formData.gradeType}
                        onChange={handleInputChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                      >
                        <option value="ASSIGNMENT">Assignment</option>
                        <option value="QUIZ">Quiz</option>
                        <option value="MIDTERM">Midterm</option>
                        <option value="FINAL">Final</option>
                        <option value="PROJECT">Project</option>
                        <option value="PRACTICAL">Practical</option>
                      </select>
                    </div>
                  </div>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700">Grade Title</label>
                    <input
                      type="text"
                      name="gradeTitle"
                      value={formData.gradeTitle}
                      onChange={handleInputChange}
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                      placeholder="e.g., Assignment 1, Quiz 2"
                      required
                    />
                  </div>
                  <div className="grid grid-cols-3 gap-4">
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700">Max Marks</label>
                      <input
                        type="number"
                        name="maxMarks"
                        value={formData.maxMarks}
                        onChange={handleInputChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                        required
                      />
                    </div>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700">Obtained Marks</label>
                      <input
                        type="number"
                        name="obtainedMarks"
                        value={formData.obtainedMarks}
                        onChange={handleInputChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                        required
                      />
                    </div>
                    <div className="mb-4">
                      <label className="block text-sm font-medium text-gray-700">Weightage (%)</label>
                      <input
                        type="number"
                        name="weightage"
                        value={formData.weightage}
                        onChange={handleInputChange}
                        className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                        step="0.1"
                        min="0"
                        max="100"
                      />
                    </div>
                  </div>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700">Status</label>
                    <select
                      name="status"
                      value={formData.status}
                      onChange={handleInputChange}
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                    >
                      <option value="DRAFT">Draft</option>
                      <option value="PUBLISHED">Published</option>
                    </select>
                  </div>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700">Feedback</label>
                    <textarea
                      name="feedback"
                      value={formData.feedback}
                      onChange={handleInputChange}
                      rows="3"
                      className="mt-1 block w-full border border-gray-300 rounded-md px-3 py-2"
                      placeholder="Provide feedback to the student..."
                    />
                  </div>
                  <div className="flex justify-end space-x-2 mt-6">
                    <button
                      type="button"
                      onClick={resetForm}
                      className="bg-gray-300 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-400 transition-colors"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="bg-blue-600 text-white px-4 py-2 rounded-md hover:bg-blue-700 transition-colors"
                    >
                      Add Grade
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Statistics Modal */}
      {showStatsModal && stats && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg leading-6 font-medium text-gray-900">Grade Statistics</h3>
              <div className="mt-4">
                <div className="space-y-4">
                  <div>
                    <h4 className="font-semibold text-gray-700">Grade Distribution</h4>
                    <div className="mt-2">
                      {Object.entries(stats.gradeDistribution || {}).map(([grade, count]) => (
                        <div key={grade} className="flex justify-between py-1">
                          <span className="text-sm text-gray-600">Grade {grade}:</span>
                          <span className="text-sm font-semibold">{count}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-700">Grades by Type</h4>
                    <div className="mt-2">
                      {Object.entries(stats.gradesByType || {}).map(([type, count]) => (
                        <div key={type} className="flex justify-between py-1">
                          <span className="text-sm text-gray-600">{type}:</span>
                          <span className="text-sm font-semibold">{count}</span>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
              <div className="flex justify-end mt-6">
                <button
                  onClick={() => setShowStatsModal(false)}
                  className="bg-gray-300 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-400 transition-colors"
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Student Report Modal */}
      {selectedStudentReport && (
        <div className="fixed inset-0 bg-gray-600 bg-opacity-50 overflow-y-auto h-full w-full z-50">
          <div className="relative top-20 mx-auto p-5 border w-96 shadow-lg rounded-md bg-white">
            <div className="mt-3">
              <h3 className="text-lg leading-6 font-medium text-gray-900">
                Student Report - {selectedStudentReport.student?.firstName} {selectedStudentReport.student?.lastName}
              </h3>
              <div className="mt-4">
                <div className="space-y-4">
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <h4 className="font-semibold text-gray-700">Total Grades</h4>
                      <p className="text-2xl font-bold text-blue-600">{selectedStudentReport.totalGrades}</p>
                    </div>
                    <div>
                      <h4 className="font-semibold text-gray-700">GPA</h4>
                      <p className="text-2xl font-bold text-green-600">{selectedStudentReport.gpa?.toFixed(2)}</p>
                    </div>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-700">Average Percentage</h4>
                    <p className="text-xl font-semibold text-purple-600">
                      {selectedStudentReport.averagePercentage?.toFixed(1)}%
                    </p>
                  </div>
                  <div>
                    <h4 className="font-semibold text-gray-700">Grades by Course</h4>
                    <div className="mt-2">
                      {Object.entries(selectedStudentReport.gradesByCourse || {}).map(([course, grades]) => (
                        <div key={course} className="mb-2">
                          <p className="text-sm font-medium text-gray-700">{course}</p>
                          <p className="text-xs text-gray-500">{grades.length} grades</p>
                        </div>
                      ))}
                    </div>
                  </div>
                </div>
              </div>
              <div className="flex justify-end mt-6">
                <button
                  onClick={() => setSelectedStudentReport(null)}
                  className="bg-gray-300 text-gray-700 px-4 py-2 rounded-md hover:bg-gray-400 transition-colors"
                >
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Grades;
