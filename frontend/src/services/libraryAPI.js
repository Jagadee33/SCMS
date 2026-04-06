import axios from 'axios';

const API_BASE_URL = 'http://localhost:8082/api';

// Create axios instance with default config
const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add request interceptor to include auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Book Management APIs
export const libraryAPI = {
  // Get all books
  getAllBooks: () => api.get('/library/books'),
  
  // Get available books
  getAvailableBooks: () => api.get('/library/books/available'),
  
  // Get book by ID
  getBookById: (id) => api.get(`/library/books/${id}`),
  
  // Search books
  searchBooks: (query) => api.get(`/library/books/search?query=${encodeURIComponent(query)}`),
  
  // Advanced search
  advancedSearch: (params) => {
    const queryParams = new URLSearchParams();
    Object.keys(params).forEach(key => {
      if (params[key] !== null && params[key] !== undefined) {
        queryParams.append(key, params[key]);
      }
    });
    return api.get(`/library/books/advanced-search?${queryParams.toString()}`);
  },
  
  // Add new book
  addBook: (book) => api.post('/library/books', book),
  
  // Update book
  updateBook: (id, book) => api.put(`/library/books/${id}`, book),
  
  // Delete book
  deleteBook: (id) => api.delete(`/library/books/${id}`),
  
  // Get low stock books
  getLowStockBooks: (threshold = 3) => api.get(`/library/books/low-stock?threshold=${threshold}`),
  
  // Get recently added books
  getRecentlyAddedBooks: () => api.get('/library/books/recent'),
  
  // Get most popular books
  getMostPopularBooks: () => api.get('/library/books/popular'),
};

// Book Issue Management APIs
export const bookIssueAPI = {
  // Issue book
  issueBook: (bookId, studentId, librarianId) => 
    api.post(`/library/books/${bookId}/issue`, null, {
      params: { studentId, librarianId }
    }),
  
  // Return book
  returnBook: (issueId, librarianId, conditionReturned) => 
    api.post(`/library/issues/${issueId}/return`, null, {
      params: { librarianId, conditionReturned }
    }),
  
  // Renew book
  renewBook: (issueId, librarianId) => 
    api.post(`/library/issues/${issueId}/renew`, null, {
      params: { librarianId }
    }),
  
  // Get student's current issues
  getStudentCurrentIssues: (studentId) => api.get(`/library/issues/student/${studentId}`),
  
  // Get student's issue history
  getStudentIssueHistory: (studentId) => api.get(`/library/issues/student/${studentId}/history`),
  
  // Get overdue books
  getOverdueBooks: () => api.get('/library/issues/overdue'),
};

// Book Reservation Management APIs
export const bookReservationAPI = {
  // Reserve book
  reserveBook: (bookId, studentId, notes) => 
    api.post(`/library/books/${bookId}/reserve`, null, {
      params: { studentId, notes }
    }),
  
  // Cancel reservation
  cancelReservation: (reservationId, studentId) => 
    api.post(`/library/reservations/${reservationId}/cancel`, null, {
      params: { studentId }
    }),
  
  // Get student's reservations
  getStudentReservations: (studentId) => api.get(`/library/reservations/student/${studentId}`),
};

// Library Statistics APIs
export const libraryStatsAPI = {
  // Get library statistics
  getLibraryStatistics: () => api.get('/library/statistics'),
  
  // Get student library dashboard
  getStudentLibraryDashboard: (studentId) => api.get(`/library/dashboard/student/${studentId}`),
};

export default {
  libraryAPI,
  bookIssueAPI,
  bookReservationAPI,
  libraryStatsAPI,
};
