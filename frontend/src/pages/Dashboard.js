import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { authAPI, studentAPI, facultyAPI, courseAPI, gradeAPI, examinationAPI, attendanceAPI, feeAPI, examRegistrationAPI } from '../services/api';
import { useNavigate } from 'react-router-dom';

const Dashboard = () => {
  const { user } = useAuth();
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    // Core Metrics
    totalStudents: 0,
    totalFaculty: 0,
    totalCourses: 0,
    totalDepartments: 0,
    activeStudents: 0,
    recentEnrollments: 0,
    totalRevenue: 0,
    pendingFees: 0,
    
    // Academic Metrics
    averageGPA: 0,
    totalGrades: 0,
    upcomingExams: 0,
    completedExams: 0,
    averageAttendance: 0,
    teachingLoad: 0,
    
    // Financial Metrics
    monthlyRevenue: 0,
    pendingAmount: 0,
    collectedFees: 0,
    scholarshipAmount: 0,
    
    // System Metrics
    activeUsers: 0,
    systemUptime: 0,
    storageUsed: 0,
    apiCalls: 0,
    
    // Performance Metrics
    studentSatisfaction: 0,
    facultySatisfaction: 0,
    courseCompletion: 0,
    placementRate: 0,
    
    // Recent Data
    recentActivities: [],
    topPerformers: [],
    recentEnrollmentsList: [],
    upcomingEvents: [],
    
    // Department Analytics
    departmentStats: [],
    courseAnalytics: [],
    facultyPerformance: [],
    
    // View Mode
    viewMode: 'overview' // 'overview', 'academic', 'financial', 'analytics', 'users'
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [selectedPeriod, setSelectedPeriod] = useState('month');
  const [viewMode, setViewMode] = useState('overview');

  const handleAddStudent = () => {
    console.log('Add Student clicked');
    navigate('/students');
  };

  const handleAddFaculty = () => {
    console.log('Add Faculty clicked');
    navigate('/faculty');
  };

  const handleCreateCourse = () => {
    console.log('Create Course clicked');
    navigate('/courses');
  };

  // Fetch comprehensive dashboard data from backend
  const fetchDashboardStats = useCallback(async () => {
    // Check authentication before making API call
    const token = localStorage.getItem('token');
    const userStr = localStorage.getItem('user');
    
    if (!token || !userStr) {
      console.log('Dashboard - No authentication found, redirecting to login');
      setError('Please login to access dashboard');
      setLoading(false);
      return;
    }

    try {
      setLoading(true);
      setError(null);
      
      console.log('Admin Dashboard - Fetching comprehensive data...');
      
      // Fetch all data in parallel for comprehensive analytics
      const [
        authStatsRes,
        studentsRes,
        facultyRes,
        coursesRes,
        gradesRes,
        examsRes,
        attendanceRes,
        feesRes,
        examRegistrationsRes
      ] = await Promise.all([
        authAPI.getDashboardStats(),
        studentAPI.getAll(),
        facultyAPI.getAll(),
        courseAPI.getAll(),
        gradeAPI.getAll(),
        examinationAPI.getAll(),
        attendanceAPI.getAll(),
        feeAPI.getAll(),
        examRegistrationAPI.getAll()
      ]);

      const authStats = authStatsRes.data || {};
      const students = studentsRes.data || [];
      const faculty = facultyRes.data || [];
      const courses = coursesRes.data || [];
      const grades = gradesRes.data || [];
      const exams = examsRes.data || [];
      const attendance = attendanceRes.data || [];
      const fees = feesRes.data || [];
      const examRegistrations = examRegistrationsRes.data || [];

      console.log('Admin Dashboard - Data fetched:', {
        students: students.length,
        faculty: faculty.length,
        courses: courses.length,
        grades: grades.length,
        exams: exams.length,
        attendance: attendance.length,
        fees: fees.length,
        examRegistrations: examRegistrations.length
      });

      // Calculate comprehensive admin statistics
      const today = new Date();
      const thisMonth = new Date(today.getFullYear(), today.getMonth(), 1);
      
      // Academic Metrics
      const publishedGrades = grades.filter(g => g.status === 'PUBLISHED');
      const averageGPA = publishedGrades.length > 0 ? 
        publishedGrades.reduce((sum, g) => sum + (g.gradePoints || 0), 0) / publishedGrades.length : 0;
      
      const upcomingExams = exams.filter(exam => new Date(exam.examDate) > today);
      const completedExams = exams.filter(exam => exam.status === 'Completed');
      
      const todayAttendance = attendance.filter(a => 
        new Date(a.date).toDateString() === today.toDateString()
      );
      const averageAttendance = students.length > 0 ? 
        (todayAttendance.length / students.length) * 100 : 0;

      const teachingLoad = courses.reduce((sum, course) => sum + (course.credits || 0), 0);

      // Financial Metrics
      const totalFees = students.reduce((sum, student) => sum + (student.fees || 0), 0);
      const pendingFees = fees.filter(fee => fee.status === 'Pending' || fee.status === 'Partial');
      const pendingAmount = pendingFees.reduce((sum, fee) => sum + (fee.amount || 0), 0);
      const collectedFees = fees.filter(fee => fee.status === 'Paid');
      const monthlyRevenue = collectedFees
        .filter(fee => new Date(fee.paidDate) >= thisMonth)
        .reduce((sum, fee) => sum + (fee.amount || 0), 0);

      // Performance Metrics
      const topPerformers = publishedGrades
        .sort((a, b) => (b.gradePoints || 0) - (a.gradePoints || 0))
        .slice(0, 10)
        .map(grade => ({
          student: grade.student?.firstName + ' ' + grade.student?.lastName,
          course: grade.course?.name,
          grade: grade.gradeLetter,
          percentage: grade.percentage
        }));

      const recentEnrollmentsList = students
        .filter(student => new Date(student.enrollmentDate) >= thisMonth)
        .slice(0, 10)
        .map(student => ({
          name: student.firstName + ' ' + student.lastName,
          course: student.course,
          enrollmentDate: student.enrollmentDate,
          fees: student.fees
        }));

      // Department Analytics
      const departments = [...new Set(courses.map(course => course.department))];
      const departmentStats = departments.map(dept => {
        const deptCourses = courses.filter(course => course.department === dept);
        const deptStudents = students.filter(student => 
          deptCourses.some(course => course.id === student.courseId)
        );
        return {
          name: dept,
          courses: deptCourses.length,
          students: deptStudents.length,
          faculty: faculty.filter(f => f.department === dept).length,
          revenue: deptStudents.reduce((sum, student) => sum + (student.fees || 0), 0)
        };
      });

      // Course Analytics
      const courseAnalytics = courses.map(course => ({
        name: course.name,
        code: course.code,
        students: students.filter(s => s.courseId === course.id).length,
        faculty: course.faculty?.firstName + ' ' + course.faculty?.lastName,
        credits: course.credits,
        status: course.status
      }));

      // Faculty Performance
      const facultyPerformance = faculty.map(fac => ({
        name: fac.firstName + ' ' + fac.lastName,
        department: fac.department,
        courses: courses.filter(c => c.faculty?.id === fac.id).length,
        students: courses
          .filter(c => c.faculty?.id === fac.id)
          .reduce((sum, course) => sum + students.filter(s => s.courseId === course.id).length, 0),
        averageRating: 4.2 // Placeholder - would come from faculty evaluation system
      }));

      // Recent Activities
      const recentActivities = [
        ...students.slice(0, 3).map(student => ({
          type: 'enrollment',
          title: `New student enrolled: ${student.firstName} ${student.lastName}`,
          time: new Date(student.createdAt).toLocaleDateString(),
          icon: '👤',
          color: 'blue'
        })),
        ...grades.slice(0, 3).map(grade => ({
          type: 'grade',
          title: `Grade posted for ${grade.student?.firstName}`,
          time: new Date(grade.createdAt).toLocaleDateString(),
          icon: '📊',
          color: 'green'
        })),
        ...exams.slice(0, 3).map(exam => ({
          type: 'exam',
          title: `Exam created: ${exam.title}`,
          time: new Date(exam.createdAt).toLocaleDateString(),
          icon: '📝',
          color: 'purple'
        }))
      ].sort((a, b) => new Date(b.time) - new Date(a.time));

      // System Metrics (mock data for demonstration)
      const systemMetrics = {
        activeUsers: students.length + faculty.length,
        systemUptime: 99.9,
        storageUsed: 75.6,
        apiCalls: 15420
      };

      // Performance Metrics (mock data for demonstration)
      const performanceMetrics = {
        studentSatisfaction: 4.3,
        facultySatisfaction: 4.5,
        courseCompletion: 87.5,
        placementRate: 92.3
      };

      // Combine all statistics
      const comprehensiveStats = {
        // Core Metrics (from auth API)
        ...authStats,
        
        // Academic Metrics
        averageGPA: averageGPA.toFixed(2),
        totalGrades: publishedGrades.length,
        upcomingExams: upcomingExams.length,
        completedExams: completedExams.length,
        averageAttendance: Math.round(averageAttendance),
        teachingLoad,
        
        // Financial Metrics
        monthlyRevenue,
        pendingAmount,
        collectedFees: collectedFees.reduce((sum, fee) => sum + (fee.amount || 0), 0),
        scholarshipAmount: 250000, // Placeholder
        
        // System Metrics
        ...systemMetrics,
        
        // Performance Metrics
        ...performanceMetrics,
        
        // Recent Data
        recentActivities,
        topPerformers,
        recentEnrollmentsList,
        upcomingEvents: upcomingExams.slice(0, 5),
        
        // Department Analytics
        departmentStats,
        courseAnalytics,
        facultyPerformance
      };

      console.log('Admin Dashboard - Comprehensive stats calculated:', comprehensiveStats);
      setStats(prevStats => ({ ...prevStats, ...comprehensiveStats }));
      
    } catch (err) {
      console.error('Error fetching comprehensive dashboard stats:', err);
      console.error('Error details:', err.response?.status, err.response?.data);
      setError('Failed to load dashboard statistics');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchDashboardStats();
  }, [fetchDashboardStats]);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0
    }).format(amount);
  };

  const formatNumber = (num) => {
    if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
    if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
    return num.toString();
  };

  const getTrendIcon = (trend) => {
    if (trend > 0) return '📈';
    if (trend < 0) return '📉';
    return '➡️';
  };

  const getTrendColor = (trend) => {
    if (trend > 0) return 'text-green-600';
    if (trend < 0) return 'text-red-600';
    return 'text-gray-600';
  };

  const getGradeColor = (grade) => {
    if (grade.startsWith('A')) return 'text-green-600 bg-green-50';
    if (grade.startsWith('B')) return 'text-blue-600 bg-blue-50';
    if (grade.startsWith('C')) return 'text-yellow-600 bg-yellow-50';
    return 'text-red-600 bg-red-50';
  };

  const StatCard = ({ title, value, change, icon, color = 'blue', subtitle = '', trend = null }) => {
    const colorClasses = {
      blue: 'from-blue-500 to-blue-600',
      green: 'from-green-500 to-green-600',
      purple: 'from-purple-500 to-purple-600',
      orange: 'from-orange-500 to-orange-600',
      red: 'from-red-500 to-red-600',
      indigo: 'from-indigo-500 to-indigo-600',
      yellow: 'from-yellow-500 to-yellow-600'
    };

    return (
      <div className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300">
        <div className={`bg-gradient-to-r ${colorClasses[color]} p-4`}>
          <div className="flex items-center justify-between">
            <div>
              <p className="text-white text-sm font-medium opacity-90">{title}</p>
              <p className="text-white text-2xl font-bold">{value}</p>
              {subtitle && <p className="text-white text-xs opacity-75 mt-1">{subtitle}</p>}
              {trend !== null && (
                <p className={`text-white text-xs mt-1 flex items-center`}>
                  <span className="mr-1">{getTrendIcon(trend)}</span>
                  {trend >= 0 ? '+' : ''}{trend}% from last period
                </p>
              )}
            </div>
            <span className="text-3xl opacity-80">{icon}</span>
          </div>
        </div>
      </div>
    );
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
          <p className="mt-4 text-gray-600">Loading dashboard statistics...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h3 className="text-xl font-bold text-red-600 mb-2">Error Loading Dashboard</h3>
          <p className="text-gray-600">{error}</p>
          <button 
            onClick={() => window.location.reload()}
            className="mt-4 px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
          >
            Retry
          </button>
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
                <span className="mr-3">🏛️</span>
                Admin Dashboard
              </h1>
              <p className="text-gray-600 text-sm mt-1">
                Welcome back, {user?.firstName || 'Administrator'}!
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
                <option value="year">This Year</option>
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
                onClick={() => setViewMode('academic')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'academic' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                🎓 Academic
              </button>
              <button
                onClick={() => setViewMode('financial')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'financial' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                💰 Financial
              </button>
              <button
                onClick={() => setViewMode('analytics')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'analytics' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📈 Analytics
              </button>
              <button
                onClick={() => setViewMode('users')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'users' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                👥 Users
              </button>
              <button
                onClick={() => navigate('/ai-analytics')}
                className="px-4 py-2 rounded-lg bg-gradient-to-r from-purple-600 to-purple-700 text-white hover:from-purple-700 hover:to-purple-800 transition-all duration-200 shadow-md"
              >
                🤖 AI Analytics
              </button>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {viewMode === 'overview' && (
          <>
            {/* Key Performance Indicators */}
            <div className="mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📊</span>
                Key Performance Indicators
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <StatCard
                  title="Total Students"
                  value={formatNumber(stats.totalStudents)}
                  icon="👥‍🎓"
                  color="blue"
                  trend={12}
                  subtitle="Active enrollments"
                />
                <StatCard
                  title="Total Faculty"
                  value={formatNumber(stats.totalFaculty)}
                  icon="👨‍🏫"
                  color="green"
                  trend={5}
                  subtitle="Teaching staff"
                />
                <StatCard
                  title="Active Courses"
                  value={formatNumber(stats.totalCourses)}
                  icon="📚"
                  color="purple"
                  trend={8}
                  subtitle="Course offerings"
                />
                <StatCard
                  title="Monthly Revenue"
                  value={formatCurrency(stats.monthlyRevenue)}
                  icon="💰"
                  color="orange"
                  trend={15}
                  subtitle="This month"
                />
              </div>
              
              {/* AI Analytics Card */}
              <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
                <div className="bg-gradient-to-r from-indigo-600 to-purple-600 rounded-xl shadow-lg p-6 text-white">
                  <h2 className="text-xl font-semibold mb-4 flex items-center">
                    <span className="mr-2">🤖</span>
                    AI Analytics Dashboard
                  </h2>
                  <div className="grid grid-cols-2 gap-4 mb-6">
                    <div className="bg-white/20 rounded-lg p-4 text-center">
                      <span className="text-2xl mb-2 block">📈</span>
                      <p className="text-white font-bold text-xl">125</p>
                      <p className="text-sm text-indigo-100">Students Analyzed</p>
                    </div>
                    <div className="bg-white/20 rounded-lg p-4 text-center">
                      <span className="text-2xl mb-2 block">⚠️</span>
                      <p className="text-white font-bold text-xl">18</p>
                      <p className="text-sm text-indigo-100">At-Risk Identified</p>
                    </div>
                  </div>
                  <button 
                    onClick={() => navigate('/ai-analytics')}
                    className="w-full bg-white text-indigo-600 px-6 py-3 rounded-lg hover:bg-indigo-50 transition-colors font-semibold flex items-center justify-center"
                  >
                    <span className="mr-2">🚀</span>
                    Access AI Analytics
                  </button>
                </div>
                
                <div className="bg-white rounded-xl shadow-lg p-6">
                  <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                    <span className="mr-2">🎯</span>
                    AI Insights Summary
                  </h2>
                  <div className="space-y-3">
                    <div className="flex items-center justify-between p-3 bg-green-50 rounded-lg">
                      <div className="flex items-center">
                        <span className="text-green-600 mr-3">✅</span>
                        <span className="text-sm font-medium text-gray-900">Performance Predictions</span>
                      </div>
                      <span className="text-sm font-bold text-green-600">92% Accurate</span>
                    </div>
                    <div className="flex items-center justify-between p-3 bg-blue-50 rounded-lg">
                      <div className="flex items-center">
                        <span className="text-blue-600 mr-3">📊</span>
                        <span className="text-sm font-medium text-gray-900">Study Optimization</span>
                      </div>
                      <span className="text-sm font-bold text-blue-600">Active</span>
                    </div>
                    <div className="flex items-center justify-between p-3 bg-purple-50 rounded-lg">
                      <div className="flex items-center">
                        <span className="text-purple-600 mr-3">🔔</span>
                        <span className="text-sm font-medium text-gray-900">Smart Notifications</span>
                      </div>
                      <span className="text-sm font-bold text-purple-600">Real-time</span>
                    </div>
                    <div className="flex items-center justify-between p-3 bg-orange-50 rounded-lg">
                      <div className="flex items-center">
                        <span className="text-orange-600 mr-3">📈</span>
                        <span className="text-sm font-medium text-gray-900">Trend Analysis</span>
                      </div>
                      <span className="text-sm font-bold text-orange-600">Live</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Academic Overview */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8 mb-8">
              {/* Academic Metrics */}
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <span className="mr-2">🎓</span>
                  Academic Performance
                </h2>
                <div className="grid grid-cols-2 gap-4 mb-6">
                  <div className="bg-blue-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">📊</span>
                    <p className="text-blue-700 font-bold text-xl">{stats.averageGPA}</p>
                    <p className="text-sm text-blue-600">Average GPA</p>
                  </div>
                  <div className="bg-green-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">📈</span>
                    <p className="text-green-700 font-bold text-xl">{stats.totalGrades}</p>
                    <p className="text-sm text-green-600">Total Grades</p>
                  </div>
                  <div className="bg-purple-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">📝</span>
                    <p className="text-purple-700 font-bold text-xl">{stats.upcomingExams}</p>
                    <p className="text-sm text-purple-600">Upcoming Exams</p>
                  </div>
                  <div className="bg-orange-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">✅</span>
                    <p className="text-orange-700 font-bold text-xl">{stats.averageAttendance}%</p>
                    <p className="text-sm text-orange-600">Avg Attendance</p>
                  </div>
                </div>
                <div className="border-t pt-4">
                  <h3 className="font-semibold text-gray-900 mb-3">Top Performers</h3>
                  <div className="space-y-2">
                    {stats.topPerformers.slice(0, 5).map((student, index) => (
                      <div key={index} className="flex items-center justify-between p-2 bg-gray-50 rounded">
                        <div>
                          <p className="font-medium text-gray-900">{student.student}</p>
                          <p className="text-sm text-gray-600">{student.course}</p>
                        </div>
                        <div className="text-right">
                          <span className={`px-2 py-1 rounded-full text-xs font-semibold ${getGradeColor(student.grade)}`}>
                            {student.grade}
                          </span>
                          <p className="text-xs text-gray-500">{student.percentage}%</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              {/* System Overview */}
              <div className="bg-white rounded-xl shadow-lg p-6">
                <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                  <span className="mr-2">⚙️</span>
                  System Overview
                </h2>
                <div className="grid grid-cols-2 gap-4 mb-6">
                  <div className="bg-indigo-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">👥</span>
                    <p className="text-indigo-700 font-bold text-xl">{formatNumber(stats.activeUsers)}</p>
                    <p className="text-sm text-indigo-600">Active Users</p>
                  </div>
                  <div className="bg-green-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">⚡</span>
                    <p className="text-green-700 font-bold text-xl">{stats.systemUptime}%</p>
                    <p className="text-sm text-green-600">System Uptime</p>
                  </div>
                  <div className="bg-yellow-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">💾</span>
                    <p className="text-yellow-700 font-bold text-xl">{stats.storageUsed}%</p>
                    <p className="text-sm text-yellow-600">Storage Used</p>
                  </div>
                  <div className="bg-red-50 rounded-lg p-4 text-center">
                    <span className="text-2xl mb-2 block">📡</span>
                    <p className="text-red-700 font-bold text-xl">{formatNumber(stats.apiCalls)}</p>
                    <p className="text-sm text-red-600">API Calls Today</p>
                  </div>
                </div>
                <div className="border-t pt-4">
                  <h3 className="font-semibold text-gray-900 mb-3">Recent Activities</h3>
                  <div className="space-y-2">
                    {stats.recentActivities.slice(0, 5).map((activity, index) => (
                      <div key={index} className="flex items-center space-x-3 p-2 bg-gray-50 rounded">
                        <span className="text-lg">{activity.icon}</span>
                        <div className="flex-1">
                          <p className="text-sm font-medium text-gray-900">{activity.title}</p>
                          <p className="text-xs text-gray-500">{activity.time}</p>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Quick Actions */}
            <div className="mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">⚡</span>
                Quick Actions
              </h2>
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <button 
                  onClick={handleAddStudent}
                  className="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-4 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">➕</span>
                  <span className="font-medium">Add Student</span>
                </button>
                <button 
                  onClick={handleAddFaculty}
                  className="bg-gradient-to-r from-green-500 to-green-600 text-white p-4 rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">👨‍🏫</span>
                  <span className="font-medium">Add Faculty</span>
                </button>
                <button 
                  onClick={handleCreateCourse}
                  className="bg-gradient-to-r from-purple-500 to-purple-600 text-white p-4 rounded-lg hover:from-purple-600 hover:to-purple-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">📚</span>
                  <span className="font-medium">Create Course</span>
                </button>
                <button 
                  onClick={() => navigate('/examinations')}
                  className="bg-gradient-to-r from-orange-500 to-orange-600 text-white p-4 rounded-lg hover:from-orange-600 hover:to-orange-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">📝</span>
                  <span className="font-medium">Manage Exams</span>
                </button>
                <button 
                  onClick={() => navigate('/ai-analytics')}
                  className="bg-gradient-to-r from-indigo-500 to-indigo-600 text-white p-4 rounded-lg hover:from-indigo-600 hover:to-indigo-700 transition-all duration-200 flex items-center justify-center"
                >
                  <span className="mr-2">🤖</span>
                  <span className="font-medium">AI Analytics</span>
                </button>
              </div>
            </div>
          </>
        )}

        {/* Academic View */}
        {viewMode === 'academic' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">🎓</span>
                Academic Management
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                <div>
                  <h3 className="font-semibold text-gray-900 mb-3">Student Performance</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Average GPA</span>
                      <span className="font-bold text-blue-600">{stats.averageGPA}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Total Grades Posted</span>
                      <span className="font-bold text-gray-900">{stats.totalGrades}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Course Completion Rate</span>
                      <span className="font-bold text-green-600">{stats.courseCompletion}%</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Placement Rate</span>
                      <span className="font-bold text-purple-600">{stats.placementRate}%</span>
                    </div>
                  </div>
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 mb-3">Examination Overview</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Upcoming Exams</span>
                      <span className="font-bold text-orange-600">{stats.upcomingExams}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Completed Exams</span>
                      <span className="font-bold text-green-600">{stats.completedExams}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Average Attendance</span>
                      <span className="font-bold text-blue-600">{stats.averageAttendance}%</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-sm text-gray-600">Total Teaching Load</span>
                      <span className="font-bold text-purple-600">{stats.teachingLoad} credits</span>
                    </div>
                  </div>
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 mb-3">Department Analytics</h3>
                  <div className="space-y-2">
                    {stats.departmentStats.slice(0, 4).map((dept, index) => (
                      <div key={index} className="flex justify-between items-center p-2 bg-gray-50 rounded">
                        <span className="text-sm text-gray-600">{dept.name}</span>
                        <span className="text-sm font-bold text-gray-900">{dept.students} students</span>
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Course Analytics */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📚</span>
                Course Analytics
              </h2>
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Course</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Code</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Students</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Faculty</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Credits</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {stats.courseAnalytics.slice(0, 10).map((course, index) => (
                      <tr key={index}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{course.name}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{course.code}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{course.students}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{course.faculty}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{course.credits}</td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                            course.status === 'Active' ? 'bg-green-100 text-green-800' : 'bg-gray-100 text-gray-800'
                          }`}>
                            {course.status}
                          </span>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* Financial View */}
        {viewMode === 'financial' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">💰</span>
                Financial Overview
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-6">
                <div className="bg-green-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">💵</span>
                  <p className="text-green-700 font-bold text-xl">{formatCurrency(stats.monthlyRevenue)}</p>
                  <p className="text-sm text-green-600">Monthly Revenue</p>
                </div>
                <div className="bg-red-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">⏳</span>
                  <p className="text-red-700 font-bold text-xl">{formatCurrency(stats.pendingAmount)}</p>
                  <p className="text-sm text-red-600">Pending Amount</p>
                </div>
                <div className="bg-blue-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">✅</span>
                  <p className="text-blue-700 font-bold text-xl">{formatCurrency(stats.collectedFees)}</p>
                  <p className="text-sm text-blue-600">Collected Fees</p>
                </div>
                <div className="bg-purple-50 rounded-lg p-4 text-center">
                  <span className="text-2xl mb-2 block">🎓</span>
                  <p className="text-purple-700 font-bold text-xl">{formatCurrency(stats.scholarshipAmount)}</p>
                  <p className="text-sm text-purple-600">Scholarships</p>
                </div>
              </div>
              <div className="border-t pt-4">
                <h3 className="font-semibold text-gray-900 mb-3">Recent Enrollments</h3>
                <div className="space-y-2">
                  {stats.recentEnrollmentsList.slice(0, 5).map((enrollment, index) => (
                    <div key={index} className="flex items-center justify-between p-3 bg-gray-50 rounded">
                      <div>
                        <p className="font-medium text-gray-900">{enrollment.name}</p>
                        <p className="text-sm text-gray-600">{enrollment.course}</p>
                      </div>
                      <div className="text-right">
                        <p className="font-bold text-green-600">{formatCurrency(enrollment.fees)}</p>
                        <p className="text-xs text-gray-500">{new Date(enrollment.enrollmentDate).toLocaleDateString()}</p>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>

            {/* Department Revenue */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📊</span>
                Department Revenue
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {stats.departmentStats.map((dept, index) => (
                  <div key={index} className="border border-gray-200 rounded-lg p-4">
                    <div className="flex justify-between items-start mb-2">
                      <h3 className="font-semibold text-gray-900">{dept.name}</h3>
                      <span className="text-sm text-green-600 font-bold">{formatCurrency(dept.revenue)}</span>
                    </div>
                    <div className="text-sm text-gray-600 space-y-1">
                      <p>👥 {dept.students} students</p>
                      <p>📚 {dept.courses} courses</p>
                      <p>👨‍🏫 {dept.faculty} faculty</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Analytics View */}
        {viewMode === 'analytics' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📈</span>
                Performance Analytics
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-6">
                <div className="bg-gradient-to-r from-blue-500 to-blue-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">🎓</span>
                  <p className="text-2xl font-bold">{stats.studentSatisfaction}/5.0</p>
                  <p className="text-sm opacity-90">Student Satisfaction</p>
                </div>
                <div className="bg-gradient-to-r from-green-500 to-green-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">👨‍🏫</span>
                  <p className="text-2xl font-bold">{stats.facultySatisfaction}/5.0</p>
                  <p className="text-sm opacity-90">Faculty Satisfaction</p>
                </div>
                <div className="bg-gradient-to-r from-purple-500 to-purple-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">📚</span>
                  <p className="text-2xl font-bold">{stats.courseCompletion}%</p>
                  <p className="text-sm opacity-90">Course Completion</p>
                </div>
                <div className="bg-gradient-to-r from-orange-500 to-orange-600 text-white rounded-lg p-4">
                  <span className="text-2xl mb-2 block">🎯</span>
                  <p className="text-2xl font-bold">{stats.placementRate}%</p>
                  <p className="text-sm opacity-90">Placement Rate</p>
                </div>
              </div>
            </div>

            {/* Faculty Performance */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">👨‍🏫</span>
                Faculty Performance
              </h2>
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Faculty</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Department</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Courses</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Students</th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Rating</th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {stats.facultyPerformance.slice(0, 10).map((faculty, index) => (
                      <tr key={index}>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{faculty.name}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{faculty.department}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{faculty.courses}</td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{faculty.students}</td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <div className="flex items-center">
                            <span className="text-yellow-400">⭐</span>
                            <span className="ml-1 text-sm text-gray-900">{faculty.averageRating}</span>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {/* Users View */}
        {viewMode === 'users' && (
          <div className="space-y-6">
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">👥</span>
                User Management
              </h2>
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                <div>
                  <h3 className="font-semibold text-gray-900 mb-3">Student Statistics</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center p-3 bg-blue-50 rounded">
                      <span className="text-sm text-gray-600">Total Students</span>
                      <span className="font-bold text-blue-600">{formatNumber(stats.totalStudents)}</span>
                    </div>
                    <div className="flex justify-between items-center p-3 bg-green-50 rounded">
                      <span className="text-sm text-gray-600">Active Students</span>
                      <span className="font-bold text-green-600">{formatNumber(stats.activeStudents)}</span>
                    </div>
                    <div className="flex justify-between items-center p-3 bg-purple-50 rounded">
                      <span className="text-sm text-gray-600">Recent Enrollments</span>
                      <span className="font-bold text-purple-600">{stats.recentEnrollments}</span>
                    </div>
                    <div className="flex justify-between items-center p-3 bg-orange-50 rounded">
                      <span className="text-sm text-gray-600">Pending Fees</span>
                      <span className="font-bold text-orange-600">{stats.pendingFees}</span>
                    </div>
                  </div>
                </div>
                <div>
                  <h3 className="font-semibold text-gray-900 mb-3">Faculty Statistics</h3>
                  <div className="space-y-3">
                    <div className="flex justify-between items-center p-3 bg-blue-50 rounded">
                      <span className="text-sm text-gray-600">Total Faculty</span>
                      <span className="font-bold text-blue-600">{formatNumber(stats.totalFaculty)}</span>
                    </div>
                    <div className="flex justify-between items-center p-3 bg-green-50 rounded">
                      <span className="text-sm text-gray-600">Active Faculty</span>
                      <span className="font-bold text-green-600">{formatNumber(stats.totalFaculty)}</span>
                    </div>
                    <div className="flex justify-between items-center p-3 bg-purple-50 rounded">
                      <span className="text-sm text-gray-600">Departments</span>
                      <span className="font-bold text-purple-600">{stats.totalDepartments}</span>
                    </div>
                    <div className="flex justify-between items-center p-3 bg-orange-50 rounded">
                      <span className="text-sm text-gray-600">Avg Satisfaction</span>
                      <span className="font-bold text-orange-600">{stats.facultySatisfaction}/5.0</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            {/* Recent Activities */}
            <div className="bg-white rounded-xl shadow-lg p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
                <span className="mr-2">📋</span>
                Recent Activities
              </h2>
              <div className="space-y-3">
                {stats.recentActivities.map((activity, index) => (
                  <div key={index} className="flex items-center space-x-4 p-4 bg-gray-50 rounded-lg hover:bg-gray-100 transition-colors">
                    <span className="text-2xl">{activity.icon}</span>
                    <div className="flex-1">
                      <h4 className="font-semibold text-gray-900">{activity.title}</h4>
                      <p className="text-sm text-gray-600">{activity.time}</p>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${
                      activity.color === 'blue' ? 'bg-blue-100 text-blue-800' :
                      activity.color === 'green' ? 'bg-green-100 text-green-800' :
                      activity.color === 'purple' ? 'bg-purple-100 text-purple-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {activity.type}
                    </span>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;
