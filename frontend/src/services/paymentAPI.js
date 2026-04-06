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

// Payment Gateway APIs
export const paymentGatewayAPI = {
  // Get all payment gateways
  getAllPaymentGateways: () => api.get('/payments/gateways'),
  
  // Get payment gateway by ID
  getPaymentGatewayById: (id) => api.get(`/payments/gateways/${id}`),
  
  // Create payment gateway
  createPaymentGateway: (gateway) => api.post('/payments/gateways', gateway),
};

// Payment Plan APIs
export const paymentPlanAPI = {
  // Get all payment plans
  getAllPaymentPlans: () => api.get('/payments/plans'),
  
  // Get payment plan by ID
  getPaymentPlanById: (id) => api.get(`/payments/plans/${id}`),
  
  // Create payment plan
  createPaymentPlan: (plan) => api.post('/payments/plans', plan),
};

// Payment Processing APIs
export const paymentAPI = {
  // Process payment
  processPayment: (paymentData) => api.post('/payments/process', paymentData),
  
  // Get student payment history
  getStudentPaymentHistory: (studentId) => api.get(`/payments/history/student/${studentId}`),
  
  // Search transactions
  searchTransactions: (filters) => api.get('/payments/search', { params: filters }),
  
  // Get payments by date range
  getPaymentsByDateRange: (startDate, endDate) => api.get('/payments/date-range', { 
    params: { startDate, endDate }
  }),
  
  // Get payments by status
  getPaymentsByStatus: (status) => api.get(`/payments/status/${status}`),
  
  // Get payments by method
  getPaymentsByMethod: (method) => api.get(`/payments/method/${method}`),
  
  // Find transaction by ID
  findByTransactionId: (transactionId) => api.get(`/payments/transaction/${transactionId}`),
  
  // Find transaction by receipt
  findByReceiptNumber: (receiptNumber) => api.get(`/payments/receipt/${receiptNumber}`),
  
  // Find transaction by gateway transaction ID
  findByGatewayTransactionId: (gatewayTransactionId) => api.get(`/payments/gateway/${gatewayTransactionId}`),
  
  // Refund payment
  refundPayment: (refundData) => api.post('/payments/refund', refundData),
  
  // Get payment statistics
  getPaymentStatistics: () => api.get('/payments/statistics'),
  
  // Get recent transactions
  getRecentTransactions: () => api.get('/payments/recent'),
  
  // Get failed transactions
  getFailedTransactions: () => api.get('/payments/failed'),
  
  // Get refunded transactions
  getRefundedTransactions: () => api.get('/payments/refunded'),
};

export default {
  paymentGatewayAPI,
  paymentPlanAPI,
  paymentAPI,
};
