import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8082/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    console.log('API Interceptor - token from localStorage:', token);
    console.log('API Interceptor - request URL:', config.url);
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
      console.log('API Interceptor - Authorization header set:', config.headers.Authorization);
    } else {
      console.log('API Interceptor - No token found, request will be anonymous');
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  getCurrentUser: () => api.get('/auth/me'),
  logout: () => api.post('/auth/logout'),
  updateProfile: (userId, data) => api.put(`/users/${userId}`, data),
  changePassword: (userId, data) => api.put(`/users/${userId}/change-password`, data),
  getDashboardStats: () => api.get('/dashboard/stats'),
  getRecentActivities: () => api.get('/dashboard/recent-activities'),
};

export const studentAPI = {
  getAll: (params) => api.get('/students', { params }),
  getById: (id) => api.get(`/students/${id}`),
  create: (data) => api.post('/students', data),
  update: (id, data) => api.put(`/students/${id}`, data),
  delete: (id) => api.delete(`/students/${id}`),
  getAttendance: (id) => api.get(`/students/${id}/attendance`),
  getGrades: (id) => api.get(`/students/${id}/grades`),
};

export const authStudentAPI = {
  getAll: (params) => api.get('/auth/students', { params }),
  getById: (id) => api.get(`/auth/students/${id}`),
};

export const facultyAPI = {
  getAll: (params) => api.get('/faculty', { params }),
  getById: (id) => api.get(`/faculty/${id}`),
  create: (data) => api.post('/faculty', data),
  update: (id, data) => api.put(`/faculty/${id}`, data),
  delete: (id) => api.delete(`/faculty/${id}`),
  getCourses: (id) => api.get(`/faculty/${id}/courses`),
};

export const attendanceAPI = {
  getAll: () => api.get('/attendance'),
  getById: (id) => api.get(`/attendance/${id}`),
  getByStudent: (studentId) => api.get(`/attendance/student/${studentId}`),
  getByCourse: (courseId) => api.get(`/attendance/course/${courseId}`),
  getByFaculty: (facultyId) => api.get(`/attendance/faculty/${facultyId}`),
  mark: (data) => api.post('/attendance/mark', data),
  markBatch: (data) => api.post('/attendance/mark-batch', data),
  update: (id, data) => api.put(`/attendance/${id}`, data),
  delete: (id) => api.delete(`/attendance/${id}`),
  getStudentReport: (studentId, params) => api.get(`/attendance/report/student/${studentId}`, { params }),
  getCourseReport: (courseId, params) => api.get(`/attendance/report/course/${courseId}`, { params }),
  getStats: () => api.get('/attendance/stats'),
};

export const courseAPI = {
  getAll: (params) => api.get('/courses', { params }),
  getById: (id) => api.get(`/courses/${id}`),
  create: (data) => api.post('/courses', data),
  update: (id, data) => api.put(`/courses/${id}`, data),
  delete: (id) => api.delete(`/courses/${id}`),
  getStudents: (id) => api.get(`/courses/${id}/students`),
  enrollStudent: (courseId, studentId) => api.post(`/courses/${courseId}/enroll`, { studentId }),
};

export const enrollmentAPI = {
  getAll: (params) => api.get('/enrollments', { params }),
  getById: (id) => api.get(`/enrollments/${id}`),
  enrollStudent: (data) => api.post('/enrollments/enroll', data),
  update: (id, data) => api.put(`/enrollments/${id}`, data),
  dropStudent: (id, remarks) => api.put(`/enrollments/${id}/drop`, { remarks }),
  completeCourse: (id, grade, remarks) => api.put(`/enrollments/${id}/complete`, { grade, remarks }),
  getStudentEnrollments: (studentId) => api.get(`/enrollments/student/${studentId}`),
  getStudentActiveEnrollments: (studentId) => api.get(`/enrollments/student/${studentId}/active`),
  getStudentCompletedCourses: (studentId) => api.get(`/enrollments/student/${studentId}/completed`),
  getCourseEnrollments: (courseId) => api.get(`/enrollments/course/${courseId}`),
  getCourseActiveEnrollments: (courseId) => api.get(`/enrollments/course/${courseId}/active`),
  getStudentGPA: (studentId) => api.get(`/enrollments/student/${studentId}/gpa`),
  getCourseStats: (courseId) => api.get(`/enrollments/course/${courseId}/stats`),
  delete: (id) => api.delete(`/enrollments/${id}`),
};

export const departmentAPI = {
  getAll: (params) => api.get('/departments', { params }),
  getById: (id) => api.get(`/departments/${id}`),
  create: (data) => api.post('/departments', data),
  update: (id, data) => api.put(`/departments/${id}`, data),
  delete: (id) => api.delete(`/departments/${id}`),
  getFaculty: (id) => api.get(`/departments/${id}/faculty`),
  getCourses: (id) => api.get(`/departments/${id}/courses`),
};

export const gradeAPI = {
  getAll: () => api.get('/grades'),
  getById: (id) => api.get(`/grades/${id}`),
  create: (data) => api.post('/grades', data),
  update: (id, data) => api.put(`/grades/${id}`, data),
  delete: (id) => api.delete(`/grades/${id}`),
  getByStudent: (studentId) => api.get(`/grades/student/${studentId}`),
  getByStudentAndCourse: (studentId, courseId) => api.get(`/grades/student/${studentId}/course/${courseId}`),
  getPublishedByStudent: (studentId) => api.get(`/grades/student/${studentId}/published`),
  getStudentGPA: (studentId) => api.get(`/grades/student/${studentId}/gpa`),
  getStudentReport: (studentId) => api.get(`/grades/student/${studentId}/report`),
  getByCourse: (courseId) => api.get(`/grades/course/${courseId}`),
  getCourseStatistics: (courseId) => api.get(`/grades/course/${courseId}/statistics`),
  getByFaculty: (facultyId) => api.get(`/grades/faculty/${facultyId}`),
  getFacultyPending: (facultyId) => api.get(`/grades/faculty/${facultyId}/pending`),
  getFacultyStatistics: (facultyId) => api.get(`/grades/faculty/${facultyId}/statistics`),
  createBatch: (data) => api.post('/grades/batch', data),
  publishGrades: (gradeIds) => api.put('/grades/publish', gradeIds),
  getByType: (gradeType) => api.get(`/grades/type/${gradeType}`),
  getCourseGPA: (studentId, courseId) => api.get(`/grades/student/${studentId}/course/${courseId}/gpa`),
  getRecentGrades: (studentId, limit = 10) => api.get(`/grades/student/${studentId}/recent?limit=${limit}`),
  getStatistics: () => api.get('/grades/statistics'),
};

export const examRegistrationAPI = {
  getAll: () => api.get('/exam-registrations'),
  getById: (id) => api.get(`/exam-registrations/${id}`),
  registerStudentForExam: (studentId, examinationId, specialRequirements) => 
    api.post(`/exam-registrations/register?studentId=${studentId}&examinationId=${examinationId}${specialRequirements ? '&specialRequirements=' + encodeURIComponent(specialRequirements) : ''}`),
  approveRegistration: (id, approvalNotes) => api.put(`/exam-registrations/${id}/approve?approvalNotes=${approvalNotes || ''}`),
  rejectRegistration: (id, approvalNotes) => api.put(`/exam-registrations/${id}/reject?approvalNotes=${approvalNotes || ''}`),
  markAttendance: (id, attended) => api.put(`/exam-registrations/${id}/attendance?attended=${attended}`),
  delete: (id) => api.delete(`/exam-registrations/${id}`),
  getStudentRegistrations: (studentId) => api.get(`/exam-registrations/student/${studentId}`),
  getUpcomingExamsForStudent: (studentId) => api.get(`/exam-registrations/student/${studentId}/upcoming`),
  getCompletedExamsForStudent: (studentId) => api.get(`/exam-registrations/student/${studentId}/completed`),
  getStudentExamReport: (studentId) => api.get(`/exam-registrations/student/${studentId}/report`),
  getExaminationRegistrations: (examinationId) => api.get(`/exam-registrations/examination/${examinationId}`),
  getExaminationStatistics: (examinationId) => api.get(`/exam-registrations/examination/${examinationId}/statistics`),
  getPendingApprovalsForFaculty: (facultyId) => api.get(`/exam-registrations/faculty/${facultyId}/pending`),
  approveMultipleRegistrations: (registrationIds, approvalNotes) => 
    api.put(`/exam-registrations/batch/approve?approvalNotes=${approvalNotes || ''}`, registrationIds),
  markMultipleAttendances: (attendanceData) => api.put(`/exam-registrations/batch/attendance`, attendanceData),
  checkRegistration: (studentId, examinationId) => api.get(`/exam-registrations/check-registration?studentId=${studentId}&examinationId=${examinationId}`),
  getStudentsWhoAttended: (examinationId) => api.get(`/exam-registrations/examination/${examinationId}/attended`),
  getStudentsWhoWereAbsent: (examinationId) => api.get(`/exam-registrations/examination/${examinationId}/absent`),
  getStatistics: () => api.get('/exam-registrations/statistics'),
};

export const examinationAPI = {
  getAll: (params) => api.get('/examinations', { params }),
  getById: (id) => api.get(`/examinations/${id}`),
  create: (data) => api.post('/examinations', data),
  update: (id, data) => api.put(`/examinations/${id}`, data),
  delete: (id) => api.delete(`/examinations/${id}`),
  submitGrade: (data) => api.post('/examinations/grade', data),
  getResults: (studentId) => api.get(`/examinations/results/${studentId}`),
};

export const libraryAPI = {
  getAllBooks: (params) => api.get('/library/books', { params }),
  getBookById: (id) => api.get(`/library/books/${id}`),
  addBook: (data) => api.post('/library/books', data),
  updateBook: (id, data) => api.put(`/library/books/${id}`, data),
  deleteBook: (id) => api.delete(`/library/books/${id}`),
  issueBook: (data) => api.post('/library/issue', data),
  returnBook: (data) => api.post('/library/return', data),
  getUserIssuedBooks: (userId) => api.get(`/library/issued/${userId}`),
};

export const feeAPI = {
  getAll: (params) => api.get('/fees', { params }),
  getById: (id) => api.get(`/fees/${id}`),
  create: (data) => api.post('/fees', data),
  update: (id, data) => api.put(`/fees/${id}`, data),
  delete: (id) => api.delete(`/fees/${id}`),
  getFeesByStudent: (studentId) => api.get(`/fees/student/${studentId}`),
  getFeesByStatus: (status) => api.get(`/fees/status/${status}`),
  getFeesByFeeType: (feeType) => api.get(`/fees/type/${feeType}`),
  getOverdueFees: () => api.get('/fees/overdue'),
  getOverdueFeesByStudent: (studentId) => api.get(`/fees/student/${studentId}/overdue`),
  getFeesByDateRange: (startDate, endDate) => api.get('/fees/date-range', { params: { startDate, endDate } }),
  getFeesByAcademicYear: (academicYear) => api.get(`/fees/academic-year/${academicYear}`),
  getFeesByAcademicYearAndSemester: (academicYear, semester) => api.get(`/fees/academic-year/${academicYear}/semester/${semester}`),
  getStudentFeesByAcademicYear: (studentId, academicYear) => api.get(`/fees/student/${studentId}/academic-year/${academicYear}`),
  processPayment: (feeId, paymentData) => api.post(`/fees/${feeId}/payment`, paymentData),
  applyDiscount: (feeId, discountData) => api.post(`/fees/${feeId}/discount`, discountData),
  getStudentFeeSummary: (studentId) => api.get(`/fees/student/${studentId}/summary`),
  getStats: () => api.get('/fees/stats'),
  payFee: (data) => api.post('/fees/pay', data),
  getReceipt: (paymentId) => api.get(`/fees/receipt/${paymentId}`)
};

export default api;
