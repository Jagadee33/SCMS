import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { studentAPI, attendanceAPI, examinationAPI, courseAPI, feeAPI, gradeAPI, examRegistrationAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';

const FacultyDashboard = () => {
  const { hasRole, user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    totalStudents: 0,
    presentToday: 0,
    upcomingExams: 0,
    pendingFees: 0,
    averageAttendance: 0,
    coursesCount: 0,
    recentActivities: [],
    pendingGrades: 0,
    examRegistrations: 0,
    averageGrade: 0,
    totalGrades: 0,
    recentGrades: [],
    pendingExamApprovals: 0,
    teachingLoad: 0,
    studentPerformance: []
  });
  const [loading, setLoading] = useState(true);
  const [selectedPeriod, setSelectedPeriod] = useState('today');
  const [viewMode, setViewMode] = useState('overview'); // 'overview', 'courses', 'grades', 'exams', 'analytics'

  useEffect(() => {
    if (hasRole('FACULTY')) {
      fetchDashboardData();
    }
  }, [selectedPeriod, fetchDashboardData]);

  const fetchDashboardData = useCallback(async () => {
    try {
      setLoading(true);
      console.log('Fetching faculty dashboard data for user:', user?.id);
      
      // Fetch data for faculty-specific metrics
      const [studentsRes, attendanceRes, examsRes, coursesRes, feesRes, gradesRes, examRegistrationsRes] = await Promise.all([
        studentAPI.getAll(),
        attendanceAPI.getAll(),
        examinationAPI.getAll(),
        courseAPI.getAll(),
        feeAPI.getAll(),
        gradeAPI.getAll(),
        examRegistrationAPI.getAll()
      ]);

      const students = studentsRes.data || [];
      const attendance = attendanceRes.data || [];
      const exams = examsRes.data || [];
      const courses = coursesRes.data || [];
      const fees = feesRes.data || [];
      const grades = gradesRes.data || [];
      const examRegistrations = examRegistrationsRes.data || [];

      console.log('Faculty dashboard data fetched:', {
        students: students.length,
        attendance: attendance.length,
        exams: exams.length,
        courses: courses.length,
        grades: grades.length,
        examRegistrations: examRegistrations.length
      });

      // Filter data for current faculty
      const facultyCourses = courses.filter(course => course.faculty?.id === user?.id);
      const facultyExams = exams.filter(exam => exam.faculty?.id === user?.id);
      const facultyGrades = grades.filter(grade => grade.faculty?.id === user?.id);
      const facultyExamRegistrations = examRegistrations.filter(reg => 
        reg.examination?.faculty?.id === user?.id
      );

      // Calculate faculty-specific statistics
      const today = new Date();
      const todayAttendance = attendance.filter(a => 
        new Date(a.date).toDateString() === today.toDateString() && a.course?.faculty?.id === user?.id
      );

      const upcomingExams = facultyExams.filter(exam => 
        new Date(exam.examDate) > today
      );

      const pendingFees = fees.filter(fee => 
        (fee.status === 'Pending' || fee.status === 'Partial') && fee.student?.course?.faculty?.id === user?.id
      );

      const facultyStudents = students.filter(student => 
        facultyCourses.some(course => course.id === student.courseId)
      );

      const averageAttendance = facultyStudents.length > 0 ? 
        (todayAttendance.length / facultyStudents.length) * 100 : 0;

      // Grade statistics
      const pendingGrades = facultyGrades.filter(grade => grade.status === 'DRAFT');
      const publishedGrades = facultyGrades.filter(grade => grade.status === 'PUBLISHED');
      const averageGrade = publishedGrades.length > 0 ? 
        publishedGrades.reduce((sum, g) => sum + (g.gradePoints || 0), 0) / publishedGrades.length : 0;

      const recentGrades = publishedGrades.slice(0, 5).map(grade => ({
        course: grade.course?.name || 'Unknown Course',
        student: grade.student?.firstName + ' ' + grade.student?.lastName,
        grade: grade.gradeLetter || 'N/A',
        percentage: grade.percentage || 0,
        date: new Date(grade.createdAt).toLocaleDateString(),
        gradeType: grade.gradeType || 'Unknown'
      }));

      // Exam registration statistics
      const pendingExamApprovals = facultyExamRegistrations.filter(reg => reg.status === 'REGISTERED');
      const approvedExamRegistrations = facultyExamRegistrations.filter(reg => reg.status === 'APPROVED');

      // Teaching load (total credits for faculty courses)
      const teachingLoad = facultyCourses.reduce((sum, course) => sum + (course.credits || 0), 0);

      // Student performance metrics
      const studentPerformance = publishedGrades.slice(0, 10).map(grade => ({
        student: grade.student?.firstName + ' ' + grade.student?.lastName,
        course: grade.course?.name || 'Unknown Course',
        grade: grade.gradeLetter || 'N/A',
        percentage: grade.percentage || 0,
        trend: grade.percentage >= 80 ? 'up' : grade.percentage >= 60 ? 'stable' : 'down'
      }));

      // Recent activities for faculty
      const recentActivities = [
        ...attendance.slice(0, 3).map(a => ({
          type: 'attendance',
          title: `Attendance marked for ${a.student?.firstName || 'Student'}`,
          time: new Date(a.date).toLocaleTimeString(),
          icon: '📝',
          color: 'blue'
        })),
        ...facultyExams.slice(0, 2).map(e => ({
          type: 'exam',
          title: `Exam: ${e.title}`,
          time: new Date(e.examDate).toLocaleDateString(),
          icon: '📋',
          color: 'purple'
        })),
        ...recentGrades.slice(0, 2).map(g => ({
          type: 'grade',
          title: `Grade posted for ${g.student}`,
          time: new Date(g.date).toLocaleTimeString(),
          icon: '📈',
          color: 'green'
        }))
      ].sort((a, b) => new Date(b.time) - new Date(a.time));

      console.log('Calculated faculty stats:', {
        totalStudents: facultyStudents.length,
        presentToday: todayAttendance.length,
        upcomingExams: upcomingExams.length,
        pendingFees: pendingFees.length,
        averageAttendance: Math.round(averageAttendance),
        coursesCount: facultyCourses.length,
        pendingGrades: pendingGrades.length,
        examRegistrations: approvedExamRegistrations.length,
        averageGrade: averageGrade.toFixed(2),
        totalGrades: publishedGrades.length,
        pendingExamApprovals: pendingExamApprovals.length,
        teachingLoad
      });

      setStats({
        totalStudents: facultyStudents.length,
        presentToday: todayAttendance.length,
        upcomingExams: upcomingExams.length,
        pendingFees: pendingFees.length,
        averageAttendance: Math.round(averageAttendance),
        coursesCount: facultyCourses.length,
        recentActivities,
        pendingGrades: pendingGrades.length,
        examRegistrations: approvedExamRegistrations.length,
        averageGrade: averageGrade.toFixed(2),
        totalGrades: publishedGrades.length,
        recentGrades,
        pendingExamApprovals: pendingExamApprovals.length,
        teachingLoad,
        studentPerformance,
        facultyCourses,
        facultyExams,
        facultyGrades,
        facultyExamRegistrations
      });
    } catch (error) {
      console.error('Error fetching faculty dashboard data:', error);
    } finally {
      setLoading(false);
    }
  }, [user?.id]);

  // Check if user has faculty role
  if (!hasRole('FACULTY')) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-3xl font-bold text-gray-900 mb-4">🚫 Access Denied</h1>
          <p className="text-gray-600 text-lg">This dashboard is only available to faculty members.</p>
          <p className="text-gray-500 mt-2">Please contact your administrator if you need access.</p>
        </div>
      </div>
    );
  }

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0
    }).format(amount || 0);
  };

  // Button click handlers
  const handleMarkAttendance = () => {
    navigate('/attendance');
  };

  const handleCreateExam = () => {
    navigate('/examinations');
  };

  const handleViewStudents = () => {
    navigate('/students');
  };

  const handleFeeStatus = () => {
    navigate('/fees');
  };

  const getStatCard = (title, value, icon, color, subtitle = '') => {
    const colorClasses = {
      blue: 'from-blue-500 to-blue-600',
      green: 'from-green-500 to-green-600',
      purple: 'from-purple-500 to-purple-600',
      orange: 'from-orange-500 to-orange-600',
      red: 'from-red-500 to-red-600',
      indigo: 'from-indigo-500 to-indigo-600'
    };

    return (
      <div className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300">
        <div className={`bg-gradient-to-r ${colorClasses[color]} p-4`}>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-white text-sm font-medium opacity-90">{title}</p>
              <p className="text-white text-2xl font-bold">{value}</p>
              {subtitle && <p className="text-white text-xs opacity-75 mt-1">{subtitle}</p>}
            </div>
            <span className="text-3xl opacity-80">{icon}</span>
          </div>
        </div>
      </div>
    );
  };

  const getActivityItem = (activity) => {
    const colorClasses = {
      blue: 'bg-blue-50 border-blue-200 text-blue-700',
      purple: 'bg-purple-50 border-purple-200 text-purple-700',
      green: 'bg-green-50 border-green-200 text-green-700',
      orange: 'bg-orange-50 border-orange-200 text-orange-700'
    };

    return (
      <div className={`p-4 rounded-lg border ${colorClasses[activity.color]} hover:shadow-md transition-shadow`}>
        <div className="flex items-start space-x-3">
          <span className="text-2xl">{activity.icon}</span>
          <div className="flex-1">
            <h4 className="font-semibold text-sm">{activity.title}</h4>
            <p className="text-xs opacity-75 mt-1">{activity.time}</p>
          </div>
        </div>
      </div>
    );
  };

  const getGradeColor = (grade) => {
    if (grade.startsWith('A')) return 'text-green-600 bg-green-50';
    if (grade.startsWith('B')) return 'text-blue-600 bg-blue-50';
    if (grade.startsWith('C')) return 'text-yellow-600 bg-yellow-50';
    return 'text-red-600 bg-red-50';
  };

  const getPerformanceTrend = (trend) => {
    const trendConfig = {
      up: { icon: '📈', color: 'text-green-600', label: 'Improving' },
      stable: { icon: '➡️', color: 'text-yellow-600', label: 'Stable' },
      down: { icon: '📉', color: 'text-red-600', label: 'Needs Attention' }
    };
    return trendConfig[trend] || trendConfig.stable;
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50 flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-gray-600 text-lg">Loading faculty dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="container mx-auto px-4 py-4">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-2xl font-bold text-gray-900 flex items-center">
                <span className="mr-3">👨‍🏫</span>
                Faculty Dashboard
              </h1>
              <p className="text-gray-600 text-sm mt-1">
                Welcome back, {user?.firstName || 'Faculty Member'}!
              </p>
            </div>
            <div className="flex items-center space-x-4">
              <select
                value={selectedPeriod}
                onChange={(e) => setSelectedPeriod(e.target.value)}
                className="px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="today">Today</option>
                <option value="week">This Week</option>
                <option value="month">This Month</option>
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
                onClick={() => setViewMode('analytics')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'analytics' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📊 Analytics
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {viewMode === 'overview' && (
          <>
            {/* Quick Actions */}
            <div className="mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">⚡</span>
                Quick Actions
              </h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <button 
                  onClick={handleMarkAttendance}
                  className="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-4 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">📝</span>
                  <span className="font-medium">Mark Attendance</span>
                </button>
                <button 
                  onClick={handleCreateExam}
                  className="bg-gradient-to-r from-green-500 to-green-600 text-white p-4 rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">📋</span>
                  <span className="font-medium">Create Exam</span>
                </button>
                <button 
                  onClick={handleViewStudents}
                  className="bg-gradient-to-r from-purple-500 to-purple-600 text-white p-4 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">👥</span>
                  <span className="font-medium">View Students</span>
                </button>
                <button 
                  onClick={() => navigate('/grades')}
                  className="bg-gradient-to-r from-orange-500 to-orange-600 text-white p-4 rounded-lg hover:from-orange-600 hover:to-orange-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">�</span>
                  <span className="font-medium">Manage Grades</span>
                </button>
              </div>
            </div>

            {/* Recent Activities */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              {/* Activities Feed */}
              <div>
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <span className="mr-2">�</span>
                  Recent Activities
                </h2>
                <div className="space-y-3">
                  {stats.recentActivities.length === 0 ? (
                    <div className="bg-white rounded-lg p-6 text-center text-gray-500">
                      <span className="text-2xl mb-2 block">📭</span>
                      No recent activities
                    </div>
                  ) : (
                    stats.recentActivities.map((activity, index) => (
                      <div key={index}>
                        {getActivityItem(activity)}
                      </div>
                    ))
                  )}
                </div>
              </div>

              {/* Recent Grades */}
              <div>
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <span className="mr-2">📈</span>
                  Recent Grades Posted
                </h2>
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <div className="space-y-3">
                    {stats.recentGrades.length === 0 ? (
                      <div className="text-center text-gray-500 py-8">
                        <span className="text-2xl mb-2 block">📝</span>
                        <p>No grades posted yet</p>
                      </div>
                    ) : (
                      stats.recentGrades.map((grade, index) => (
                        <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                          <div>
                            <h4 className="font-semibold text-gray-900">{grade.student}</h4>
                            <p className="text-sm text-gray-600">{grade.course} • {grade.gradeType}</p>
                          </div>
                          <div className="text-right">
                            <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getGradeColor(grade.grade)}`}>
                              {grade.grade}
                            </span>
                            <p className="text-xs text-gray-500 mt-1">{grade.percentage.toFixed(1)}%</p>
                          </div>
                        </div>
                      ))
                    )}
                  </div>
                  <div className="mt-4 pt-4 border-t border-gray-200">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Class Average:</span>
                      <span className="text-lg font-bold text-blue-600">{stats.averageGrade}</span>
                    </div>
                    <div className="flex justify-between items-center mt-2">
                      <span className="text-sm text-gray-600">Total Graded:</span>
                      <span className="text-lg font-bold text-gray-900">{stats.totalGrades}</span>
                    </div>
                  </div>
                </div>
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
              {stats.facultyCourses && stats.facultyCourses.length > 0 ? (
                stats.facultyCourses.map((course, index) => (
                  <div key={index} className="border border-gray-200 rounded-lg p-4 hover:shadow-md transition-shadow">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="font-semibold text-gray-900">{course.name}</h3>
                      <span className="px-2 py-1 rounded-full text-xs font-semibold bg-green-100 text-green-800">
                        {course.status || 'Active'}
                      </span>
                    </div>
                    <p className="text-sm text-gray-600 mb-2">{course.code}</p>
                    <div className="text-sm text-gray-500 space-y-1">
                      <p>👥 {course.students?.length || 0} students</p>
                      <p>📊 {course.credits || 0} credits</p>
                      <p>📅 {course.duration || 'TBD'}</p>
                    </div>
                    <div className="mt-3 flex space-x-2">
                      <button className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors text-sm">
                        View Details
                      </button>
                      <button className="flex-1 bg-gray-100 text-gray-700 py-2 rounded-lg hover:bg-gray-200 transition-colors text-sm">
                        Manage
                      </button>
                    </div>
                  </div>
                ))
              ) : (
                <div className="col-span-full text-center text-gray-500 py-12">
                  <span className="text-4xl mb-2 block">📚</span>
                  <p>No courses assigned yet</p>
                </div>
              )}
            </div>
          </div>
        )}

        {/* Grades View */}
        {viewMode === 'grades' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">�</span>
                Grade Management
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-blue-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">📝</span>
                  <p className="text-blue-700 font-semibold">{stats.pendingGrades}</p>
                  <p className="text-sm text-blue-600">Pending Grades</p>
                </div>
                <div className="bg-green-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">✅</span>
                  <p className="text-green-700 font-semibold">{stats.totalGrades}</p>
                  <p className="text-sm text-green-600">Published Grades</p>
                </div>
                <div className="bg-purple-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">📊</span>
                  <p className="text-purple-700 font-semibold">{stats.averageGrade}</p>
                  <p className="text-sm text-purple-600">Class Average</p>
                </div>
              </div>
              <div className="text-center">
                <button 
                  onClick={() => navigate('/grades')}
                  className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 transition-colors"
                >
                  Go to Grade Management
                </button>
              </div>
            </div>
            
            {/* Student Performance */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">�</span>
                Student Performance
              </h2>
              <div className="space-y-3">
                {stats.studentPerformance && stats.studentPerformance.length > 0 ? (
                  stats.studentPerformance.map((student, index) => {
                    const trend = getPerformanceTrend(student.trend);
                    return (
                      <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                        <div className="flex items-center space-x-3">
                          <span className={`text-lg ${trend.color}`}>{trend.icon}</span>
                          <div>
                            <h4 className="font-semibold text-gray-900">{student.student}</h4>
                            <p className="text-sm text-gray-600">{student.course}</p>
                          </div>
                        </div>
                        <div className="text-right">
                          <span className={`px-3 py-1 rounded-full text-sm font-semibold ${getGradeColor(student.grade)}`}>
                            {student.grade}
                          </span>
                          <p className="text-xs text-gray-500 mt-1">{student.percentage.toFixed(1)}%</p>
                        </div>
                      </div>
                    );
                  })
                ) : (
                  <div className="text-center text-gray-500 py-12">
                    <span className="text-4xl mb-2 block">�</span>
                    <p>No student performance data available</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Exams View */}
        {viewMode === 'exams' && (
          <div className="space-y-6">
            {/* Exam Statistics */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📝</span>
                Examination Management
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <div className="bg-blue-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">📋</span>
                  <p className="text-blue-700 font-semibold">{stats.upcomingExams}</p>
                  <p className="text-sm text-blue-600">Upcoming Exams</p>
                </div>
                <div className="bg-green-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">✅</span>
                  <p className="text-green-700 font-semibold">{stats.examRegistrations}</p>
                  <p className="text-sm text-green-600">Approved Registrations</p>
                </div>
                <div className="bg-yellow-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">⏳</span>
                  <p className="text-yellow-700 font-semibold">{stats.pendingExamApprovals}</p>
                  <p className="text-sm text-yellow-600">Pending Approvals</p>
                </div>
              </div>
              <div className="text-center">
                <button 
                  onClick={handleCreateExam}
                  className="bg-green-600 text-white px-6 py-3 rounded-lg hover:bg-green-700 transition-colors"
                >
                  Create New Exam
                </button>
              </div>
            </div>

            {/* My Exams */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📋</span>
                My Examinations
              </h2>
              <div className="space-y-4">
                {stats.facultyExams && stats.facultyExams.length > 0 ? (
                  stats.facultyExams.map((exam, index) => (
                    <div key={index} className="border border-gray-200 rounded-lg p-4">
                      <div className="flex justify-between items-start mb-2">
                        <h3 className="font-semibold text-gray-900">{exam.title}</h3>
                        <span className={`px-2 py-1 rounded-full text-xs font-semibold ${
                          exam.status === 'Upcoming' ? 'bg-blue-100 text-blue-800' : 
                          exam.status === 'Completed' ? 'bg-green-100 text-green-800' : 
                          'bg-gray-100 text-gray-800'
                        }`}>
                          {exam.status}
                        </span>
                      </div>
                      <div className="text-sm text-gray-600 space-y-1">
                        <p>📚 {exam.course?.name || 'Unknown Course'}</p>
                        <p>📅 {new Date(exam.examDate).toLocaleDateString()}</p>
                        <p>⏰ {exam.duration}</p>
                        <p>📍 {exam.venue || 'TBD'}</p>
                        <p>📊 {exam.totalMarks} marks • {exam.passingMarks} passing</p>
                      </div>
                      <div className="mt-3 flex space-x-2">
                        <button className="flex-1 bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition-colors text-sm">
                          View Details
                        </button>
                        <button className="flex-1 bg-gray-100 text-gray-700 py-2 rounded-lg hover:bg-gray-200 transition-colors text-sm">
                          Manage
                        </button>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="text-center text-gray-500 py-12">
                    <span className="text-4xl mb-2 block">📝</span>
                    <p>No examinations created yet</p>
                  </div>
                )}
              </div>
            </div>
          </div>
        )}

        {/* Analytics View */}
        {viewMode === 'analytics' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">�</span>
                Teaching Analytics
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
                <div className="bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">👥</span>
                  <p className="text-2xl font-bold">{stats.totalStudents}</p>
                  <p className="text-sm opacity-90">Total Students</p>
                </div>
                <div className="bg-gradient-to-r from-green-500 to-green-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">📈</span>
                  <p className="text-2xl font-bold">{stats.averageGrade}</p>
                  <p className="text-sm opacity-90">Class Average</p>
                </div>
                <div className="bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">📚</span>
                  <p className="text-2xl font-bold">{stats.coursesCount}</p>
                  <p className="text-sm opacity-90">Active Courses</p>
                </div>
                <div className="bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">📊</span>
                  <p className="text-2xl font-bold">{stats.teachingLoad}</p>
                  <p className="text-sm opacity-90">Teaching Credits</p>
                </div>
              </div>
            </div>

            {/* Performance Overview */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📈</span>
                Performance Overview
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-3">Grade Distribution</h3>
                  <div className="bg-gray-50 rounded-lg p-4 h-32 flex items-center justify-center">
                    <div className="text-center">
                      <span className="text-4xl mb-2 block">📊</span>
                      <p className="text-gray-600">Grade distribution chart coming soon</p>
                    </div>
                  </div>
                </div>
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-3">Attendance Trends</h3>
                  <div className="bg-gray-50 rounded-lg p-4 h-32 flex items-center justify-center">
                    <div className="text-center">
                      <span className="text-4xl mb-2 block">📈</span>
                      <p className="text-gray-600">Attendance trends coming soon</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default FacultyDashboard;
