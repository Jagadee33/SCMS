import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { courseAPI, attendanceAPI, examinationAPI, feeAPI, gradeAPI, examRegistrationAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';
import PaymentHistory from '../components/PaymentHistory';

const StudentDashboard = () => {
  const { hasRole, user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    enrolledCourses: 0,
    attendanceRate: 0,
    upcomingExams: 0,
    pendingFees: 0,
    averageGrade: 0,
    totalCredits: 0,
    recentGrades: [],
    recentAttendance: []
  });
  const [loading, setLoading] = useState(true);
  const [selectedSemester, setSelectedSemester] = useState('current');
  const [viewMode, setViewMode] = useState('overview'); // 'overview', 'courses', 'grades', 'exams', 'fees'

  const fetchDashboardData = useCallback(async () => {
    try {
      setLoading(true);
      console.log('Fetching student dashboard data for user:', user?.id);
      
      // Fetch data for student-specific metrics
      const [coursesRes, attendanceRes, examsRes, feesRes, gradesRes, examRegistrationsRes] = await Promise.all([
        courseAPI.getAll(),
        attendanceAPI.getAll(),
        examinationAPI.getAll(),
        feeAPI.getAll(),
        gradeAPI.getAll(),
        examRegistrationAPI.getAll()
      ]);

      const courses = coursesRes.data || [];
      const attendance = attendanceRes.data || [];
      const exams = examsRes.data || [];
      const fees = feesRes.data || [];
      const grades = gradesRes.data || [];
      const examRegistrations = examRegistrationsRes.data || [];

      console.log('Dashboard data fetched:', {
        courses: courses.length,
        attendance: attendance.length,
        exams: exams.length,
        grades: grades.length,
        examRegistrations: examRegistrations.length
      });

      // Calculate student-specific statistics
      const studentGrades = grades.filter(g => g.student?.id === user?.id);
      const studentAttendance = attendance.filter(a => a.student?.id === user?.id);
      const studentExamRegistrations = examRegistrations.filter(er => er.student?.id === user?.id);
      
      // Calculate attendance rate
      const attendanceRate = studentAttendance.length > 0 ? 
        (studentAttendance.filter(a => a.status === 'Present').length / studentAttendance.length) * 100 : 0;
      
      // Calculate upcoming exams (registered exams that are upcoming)
      const upcomingExams = studentExamRegistrations.filter(er => 
        er.status === 'APPROVED' && 
        er.examination && 
        new Date(er.examination.examDate) > new Date()
      ).length;

      // Calculate pending fees
      const pendingFees = fees.filter(fee => 
        fee.student?.id === user?.id && 
        (fee.status === 'Pending' || fee.status === 'Partial')
      ).length;

      // Process real grade data
      const recentGrades = studentGrades.slice(0, 5).map(grade => ({
        course: grade.course?.name || 'Unknown Course',
        grade: grade.gradeLetter || 'N/A',
        percentage: grade.percentage || 0,
        date: new Date(grade.createdAt).toLocaleDateString(),
        credits: grade.course?.credits || 3,
        gradeType: grade.gradeType || 'Unknown'
      }));

      // Calculate GPA from real grades
      const averageGrade = studentGrades.length > 0 ? 
        studentGrades.reduce((sum, g) => sum + (g.gradePoints || 0), 0) / studentGrades.length : 0;

      const totalCredits = studentGrades.reduce((sum, g) => sum + (g.course?.credits || 0), 0);

      // Get recent attendance
      const recentAttendance = studentAttendance.slice(0, 5);

      // Get available courses for registration
      const availableExams = exams.filter(exam => 
        new Date(exam.examDate) > new Date() && 
        !studentExamRegistrations.some(er => er.examination?.id === exam.id)
      );

      console.log('Calculated stats:', {
        enrolledCourses: courses.length,
        attendanceRate,
        upcomingExams,
        pendingFees,
        averageGrade,
        totalCredits,
        recentGradesCount: recentGrades.length,
        recentAttendanceCount: recentAttendance.length,
        availableExamsCount: availableExams.length
      });

      setStats({
        enrolledCourses: courses.length,
        attendanceRate: Math.round(attendanceRate),
        upcomingExams,
        pendingFees,
        averageGrade: averageGrade.toFixed(2),
        totalCredits,
        recentGrades,
        recentAttendance,
        availableExams,
        studentGrades,
        studentExamRegistrations
      });
    } catch (error) {
      console.error('Error fetching dashboard data:', error);
    } finally {
      setLoading(false);
    }
  }, [user?.id]);

  useEffect(() => {
    if (hasRole('STUDENT')) {
      fetchDashboardData();
    }
  }, [selectedSemester, hasRole, fetchDashboardData]);

  // Check if user has student role
  if (!hasRole('STUDENT')) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">🚫 Access Denied</h1>
          <p className="text-gray-600 text-lg">This dashboard is only available to students.</p>
          <p className="text-gray-500 mt-2">Please contact your administrator if you need access.</p>
        </div>
      </div>
    );
  }


  const getGradeColor = (grade) => {
    if (grade.startsWith('A')) return 'text-green-600 bg-green-50';
    if (grade.startsWith('B')) return 'text-blue-600 bg-blue-50';
    if (grade.startsWith('C')) return 'text-yellow-600 bg-yellow-50';
    return 'text-red-600 bg-red-50';
  };

  const getAttendanceIcon = (status) => {
    return status === 'Present' ? '✅' : status === 'Absent' ? '❌' : '⏰';
  };

  const handleExamRegistration = async (examId) => {
    try {
      console.log('Registering for exam:', examId);
      const response = await examRegistrationAPI.registerStudentForExam(user?.id, examId);
      
      if (response.data) {
        alert('Successfully registered for exam!');
        // Refresh dashboard data
        fetchDashboardData();
      }
    } catch (error) {
      console.error('Error registering for exam:', error);
      alert('Failed to register for exam. Please try again.');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600 text-lg">Loading your academic dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-blue-50 to-indigo-100">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="container mx-auto px-4 py-4">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 flex items-center">
                <span className="mr-3">🎓</span>
                Student Dashboard
              </h1>
              <p className="text-gray-600 text-sm mt-1">
                Welcome back, {user?.firstName || 'Student'}!
              </p>
            </div>
            <div className="flex items-center space-x-4">
              <select
                value={selectedSemester}
                onChange={(e) => setSelectedSemester(e.target.value)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="current">Current Semester</option>
                <option value="previous">Previous Semester</option>
                <option value="all">All Semesters</option>
              </select>
              <button
                onClick={() => setViewMode('overview')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'overview' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📊 Overview
              </button>
              <button
                onClick={() => setViewMode('courses')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'courses' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📚 Courses
              </button>
              <button
                onClick={() => setViewMode('grades')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'grades' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📈 Grades
              </button>
              <button
                onClick={() => setViewMode('exams')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'exams' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📝 Exams
              </button>
              <button
                onClick={() => setViewMode('fees')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'fees' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                💰 Fees
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {viewMode === 'overview' && (
          <>
            {/* Academic Performance Cards */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="bg-blue-100 rounded-full p-3">
                    <span className="text-2xl">📚</span>
                  </div>
                  <span className="text-3xl font-bold text-blue-600">{stats.enrolledCourses}</span>
                </div>
                <h3 className="text-gray-900 font-semibold">Enrolled Courses</h3>
                <p className="text-gray-600 text-sm mt-1">Active this semester</p>
              </div>

              <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="bg-green-100 rounded-full p-3">
                    <span className="text-2xl">📊</span>
                  </div>
                  <span className="text-3xl font-bold text-green-600">{stats.attendanceRate}%</span>
                </div>
                <h3 className="text-gray-900 font-semibold">Attendance Rate</h3>
                <p className="text-gray-600 text-sm mt-1">This semester</p>
              </div>

              <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="bg-purple-100 rounded-full p-3">
                    <span className="text-2xl">📝</span>
                  </div>
                  <span className="text-3xl font-bold text-purple-600">{stats.upcomingExams}</span>
                </div>
                <h3 className="text-gray-900 font-semibold">Upcoming Exams</h3>
                <p className="text-gray-600 text-sm mt-1">Next 30 days</p>
              </div>

              <div className="bg-white rounded-xl shadow-lg p-6 hover:shadow-xl transition-shadow">
                <div className="flex items-center justify-between mb-4">
                  <div className="bg-orange-100 rounded-full p-3">
                    <span className="text-2xl">💰</span>
                  </div>
                  <span className="text-3xl font-bold text-orange-600">{stats.pendingFees}</span>
                </div>
                <h3 className="text-gray-900 font-semibold">Pending Fees</h3>
                <p className="text-gray-600 text-sm mt-1">Payment required</p>
              </div>
            </div>

            {/* Academic Summary */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
              {/* Recent Grades */}
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <span className="mr-2">📈</span>
                  Recent Grades
                </h2>
                <div className="space-y-3">
                  {stats.recentGrades.length === 0 ? (
                    <div className="text-center text-gray-500 py-8">
                      <span className="text-2xl mb-2 block">📚</span>
                      <p>No grades available yet</p>
                    </div>
                  ) : (
                    stats.recentGrades.map((grade, index) => (
                      <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                        <div>
                          <h4 className="font-semibold text-gray-900">{grade.course}</h4>
                          <p className="text-sm text-gray-600">{grade.gradeType} • {grade.date}</p>
                        </div>
                        <div className="text-right">
                          <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getGradeColor(grade.grade)}`}>
                            {grade.grade}
                          </span>
                          <p className="text-xs text-gray-500 mt-1">{grade.percentage.toFixed(1)}% • {grade.credits} credits</p>
                        </div>
                      </div>
                    ))
                  )}
                </div>
                <div className="mt-4 pt-4 border-t border-gray-200">
                  <div className="flex justify-between items-center">
                    <span className="text-sm text-gray-600">Average GPA:</span>
                    <span className="text-lg font-bold text-blue-600">{stats.averageGrade}</span>
                  </div>
                  <div className="flex justify-between items-center mt-2">
                    <span className="text-sm text-gray-600">Total Credits:</span>
                    <span className="text-lg font-bold text-gray-900">{stats.totalCredits}</span>
                  </div>
                </div>
              </div>

              {/* Recent Attendance */}
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <span className="mr-2">📅</span>
                  Recent Attendance
                </h2>
                <div className="space-y-3">
                  {stats.recentAttendance.length === 0 ? (
                    <div className="text-center text-gray-500 py-8">
                      <span className="text-2xl mb-2 block">📅</span>
                      <p>No attendance records yet</p>
                    </div>
                  ) : (
                    stats.recentAttendance.map((attendance, index) => (
                      <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                        <div className="flex items-center">
                          <span className="text-2xl mr-3">{getAttendanceIcon(attendance.status)}</span>
                          <div>
                            <h4 className="font-semibold text-gray-900">
                              {attendance.course?.name || 'Unknown Course'}
                            </h4>
                            <p className="text-sm text-gray-600">
                              {new Date(attendance.date).toLocaleDateString()}
                            </p>
                          </div>
                        </div>
                        <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                          attendance.status === 'Present' 
                            ? 'bg-green-100 text-green-800' 
                            : attendance.status === 'Absent'
                            ? 'bg-red-100 text-red-800'
                            : 'bg-yellow-100 text-yellow-800'
                        }`}>
                          {attendance.status}
                        </span>
                      </div>
                    ))
                  )}
                </div>
                <div className="mt-4 pt-4 border-t border-gray-200">
                  <div className="flex justify-between items-center">
                    <span className="text-sm text-gray-600">This Month's Attendance:</span>
                    <span className="text-lg font-bold text-green-600">{stats.attendanceRate}%</span>
                  </div>
                </div>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">⚡</span>
                Quick Actions
              </h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <button
                  onClick={() => navigate('/courses')}
                  className="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-4 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">📚</span>
                  <span className="font-medium">View Courses</span>
                </button>
                <button
                  onClick={() => navigate('/attendance')}
                  className="bg-gradient-to-r from-green-500 to-green-600 text-white p-4 rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">📅</span>
                  <span className="font-medium">Attendance</span>
                </button>
                <button
                  onClick={() => navigate('/examinations')}
                  className="bg-gradient-to-r from-purple-500 to-purple-600 text-white p-4 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">📝</span>
                  <span className="font-medium">Exams</span>
                </button>
                <button
                  onClick={() => navigate('/fees')}
                  className="bg-gradient-to-r from-orange-500 to-orange-600 text-white p-4 rounded-lg hover:from-orange-600 hover:to-orange-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">💰</span>
                  <span className="font-medium">Pay Fees</span>
                </button>
              </div>
            </div>
          </>
        )}

        {/* Courses View */}
        {viewMode === 'courses' && (
          <div className="bg-white rounded-xl shadow-lg p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
              <span className="mr-2">📚</span>
              My Courses
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              {/* Course cards would be populated here */}
              <div className="text-center text-gray-500 py-12">
                <span className="text-4xl mb-2 block">📚</span>
                <p>Your enrolled courses will appear here</p>
              </div>
            </div>
          </div>
        )}

        {/* Grades View */}
        {viewMode === 'grades' && (
          <div className="bg-white rounded-xl shadow-lg p-6">
            <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
              <span className="mr-2">📈</span>
              Academic Performance
            </h2>
            <div className="space-y-4">
              {/* Grade details would be populated here */}
              <div className="text-center text-gray-500 py-12">
                <span className="text-4xl mb-2 block">📈</span>
                <p>Your grades and academic performance will appear here</p>
              </div>
            </div>
          </div>
        )}

        {/* Exams View */}
        {viewMode === 'exams' && (
          <div className="space-y-6">
            {/* Exam Registration */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📝</span>
                Exam Registration
              </h2>
              <div className="space-y-4">
                {stats.availableExams && stats.availableExams.length > 0 ? (
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {stats.availableExams.slice(0, 6).map((exam, index) => (
                      <div key={index} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                        <div className="flex justify-between items-start mb-2">
                          <h3 className="font-semibold text-gray-900">{exam.title}</h3>
                          <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                            exam.status === 'Upcoming' ? 'bg-blue-100 text-blue-800' : 'bg-gray-100 text-gray-800'
                          }`}>
                            {exam.status}
                          </span>
                        </div>
                        <p className="text-sm text-gray-600 mb-2">{exam.course?.name || 'Unknown Course'}</p>
                        <div className="text-sm text-gray-500 space-y-1">
                          <p>📅 {new Date(exam.examDate).toLocaleDateString()}</p>
                          <p>⏰ {exam.duration}</p>
                          <p>📍 {exam.venue || 'TBD'}</p>
                          <p>📊 {exam.totalMarks} marks</p>
                        </div>
                        <button
                          onClick={() => handleExamRegistration(exam.id)}
                          className="mt-3 w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors"
                        >
                          Register Now
                        </button>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center text-gray-500 py-12">
                    <span className="text-4xl mb-2 block">📝</span>
                    <p>No available exams for registration</p>
                  </div>
                )}
              </div>
            </div>

            {/* Registered Exams */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📋</span>
                My Registered Exams
              </h2>
              <div className="space-y-4">
                {stats.studentExamRegistrations && stats.studentExamRegistrations.length > 0 ? (
                  <div className="space-y-3">
                    {stats.studentExamRegistrations.map((registration, index) => (
                      <div key={index} className="border border-gray-200 rounded-lg p-4">
                        <div className="flex justify-between items-start mb-2">
                          <h3 className="font-semibold text-gray-900">{registration.examination?.title}</h3>
                          <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                            registration.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                            registration.status === 'REGISTERED' ? 'bg-yellow-100 text-yellow-800' :
                            'bg-gray-100 text-gray-800'
                          }`}>
                            {registration.status}
                          </span>
                        </div>
                        <div className="text-sm text-gray-600 space-y-1">
                          <p>📅 {registration.examination ? new Date(registration.examination.examDate).toLocaleDateString() : 'TBD'}</p>
                          <p>⏰ {registration.examination?.duration || 'TBD'}</p>
                          <p>📍 {registration.examination?.venue || 'TBD'}</p>
                          <p>📊 {registration.examination?.totalMarks || 0} marks</p>
                        </div>
                        {registration.status === 'APPROVED' && (
                          <div className="mt-3 p-2 bg-green-50 rounded text-sm text-green-800">
                            ✅ Registration approved! You can attend this exam.
                          </div>
                        )}
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center text-gray-500 py-12">
                    <span className="text-4xl mb-2 block">📋</span>
                    <p>You haven't registered for any exams yet</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Fees View */}
        {viewMode === 'fees' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">💰</span>
                Fee Status
              </h2>
              <div className="space-y-4">
                <div className="text-center text-gray-500 py-12">
                  <span className="text-4xl mb-2 block">💰</span>
                  <p>Your fee status and payment history will appear here</p>
                </div>
              </div>
            </div>
            
            <PaymentHistory studentId={user?.id} />
          </div>
        )}
      </div>
    </div>
  );
};

export default StudentDashboard;
