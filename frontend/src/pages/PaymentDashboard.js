import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { paymentAPI } from '../services/paymentAPI';

const PaymentDashboard = () => {
  const { user } = useAuth();
  const [viewMode, setViewMode] = useState('overview');
  const [transactions, setTransactions] = useState([]);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [statistics, setStatistics] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchFilters, setSearchFilters] = useState({
    startDate: '',
    endDate: '',
    status: '',
    method: '',
    transactionId: ''
  });

  // Fetch payment data
  const fetchPaymentData = useCallback(async () => {
    try {
      setLoading(true);
      
      if (user?.id) {
        // Mock data for now
        setTransactions([
          {
            id: 1,
            transactionId: 'TXN123456',
            paymentDate: '2024-01-15',
            paymentMethod: 'CREDIT_CARD',
            amount: 5000,
            paymentStatus: 'COMPLETED'
          },
          {
            id: 2,
            transactionId: 'TXN123457',
            paymentDate: '2024-01-16',
            paymentMethod: 'UPI',
            amount: 2500,
            paymentStatus: 'PENDING'
          }
        ]);
        
        setPaymentMethods([
          { id: 1, gatewayName: 'Razorpay', providerName: 'Razorpay', isActive: true },
          { id: 2, gatewayName: 'PayTM', providerName: 'PayTM', isActive: true },
          { id: 3, gatewayName: 'PhonePe', providerName: 'PhonePe', isActive: false }
        ]);
        
        setStatistics({
          totalRevenue: 75000,
          successfulPayments: 15,
          pendingPayments: 3,
          failedPayments: 2,
          paymentMethods: [
            ['CREDIT_CARD', 30000],
            ['UPI', 25000],
            ['NET_BANKING', 20000]
          ],
          transactionStatuses: [
            ['COMPLETED', 15],
            ['PENDING', 3],
            ['FAILED', 2]
          ]
        });
      }
    } catch (error) {
      console.error('Error fetching payment data:', error);
    } finally {
      setLoading(false);
    }
  }, [user?.id]);

  useEffect(() => {
    fetchPaymentData();
  }, [fetchPaymentData]);

  // Handle search and filtering
  const handleSearch = async () => {
    try {
      setLoading(true);
      const filteredTransactions = await paymentAPI.searchTransactions(searchFilters);
      setTransactions(filteredTransactions.data || []);
    } catch (error) {
      console.error('Error searching transactions:', error);
    } finally {
      setLoading(false);
    }
  };

  // Process new payment
  const handleProcessPayment = async (paymentData) => {
    try {
      const response = await paymentAPI.processPayment(paymentData);
      if (response.data) {
        alert('Payment processed successfully!');
        fetchPaymentData(); // Refresh data
      }
    } catch (error) {
      console.error('Payment processing error:', error);
      alert('Payment failed: ' + (error.response?.data?.error || error.message));
    }
  };

  // Format date
  const formatDate = (dateString) => {
    return new Date(dateString).toLocaleDateString();
  };

  // Format currency
  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR'
    }).format(amount);
  };

  // Get status color
  const getStatusColor = (status) => {
    const colors = {
      'COMPLETED': 'text-green-600 bg-green-50',
      'PENDING': 'text-yellow-600 bg-yellow-50',
      'FAILED': 'text-red-600 bg-red-50',
      'REFUNDED': 'text-blue-600 bg-blue-50',
      'PARTIALLY_REFUNDED': 'text-purple-600 bg-purple-50'
    };
    return colors[status] || 'text-gray-600 bg-gray-50';
  };

  // Get method icon
  const getMethodIcon = (method) => {
    const icons = {
      'CREDIT_CARD': '💳',
      'DEBIT_CARD': '💳',
      'NET_BANKING': '🏦',
      'UPI': '📱',
      'CASH': '💵',
      'CHEQUE': '📄',
      'WALLET': '👛'
    };
    return icons[method] || '💳';
  };

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
          <p className="mt-4 text-gray-600">Loading payment data...</p>
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
                <span className="mr-3">💳</span>
                Payment Dashboard
              </h1>
              <p className="text-gray-600 text-sm mt-1">
                Manage payments, transactions, and financial analytics
              </p>
            </div>
            <div className="flex items-center space-x-4">
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
                onClick={() => setViewMode('transactions')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'transactions' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                📋 Transactions
              </button>
              <button
                onClick={() => setViewMode('methods')}
                className={`px-4 py-2 rounded-lg transition-colors ${
                  viewMode === 'methods' 
                    ? 'bg-blue-600 text-white' 
                    : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
                }`}
              >
                💳 Payment Methods
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
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        {viewMode === 'overview' && (
          <div>
            {/* Quick Stats */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
              <div className="bg-white rounded-lg shadow p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-blue-100 rounded-full">
                    <span className="text-2xl font-bold text-blue-600">💰</span>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-semibold text-gray-900">Total Revenue</h3>
                    <p className="text-3xl font-bold text-gray-900">
                      {formatCurrency(statistics?.totalRevenue || 0)}
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-green-100 rounded-full">
                    <span className="text-2xl font-bold text-green-600">✓</span>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-semibold text-gray-900">Successful Payments</h3>
                    <p className="text-3xl font-bold text-gray-900">
                      {statistics?.successfulPayments || 0}
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-yellow-100 rounded-full">
                    <span className="text-2xl font-bold text-yellow-600">⏱</span>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-semibold text-gray-900">Pending Payments</h3>
                    <p className="text-3xl font-bold text-gray-900">
                      {statistics?.pendingPayments || 0}
                    </p>
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-lg shadow p-6">
                <div className="flex items-center">
                  <div className="p-3 bg-red-100 rounded-full">
                    <span className="text-2xl font-bold text-red-600">✗</span>
                  </div>
                  <div className="ml-4">
                    <h3 className="text-lg font-semibold text-gray-900">Failed Payments</h3>
                    <p className="text-3xl font-bold text-gray-900">
                      {statistics?.failedPayments || 0}
                    </p>
                  </div>
                </div>
              </div>
            </div>

            {/* Payment Methods */}
            <div className="bg-white rounded-lg shadow p-6 mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Payment Methods</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {paymentMethods.map((method) => (
                  <div key={method.id} className="bg-white border rounded-lg p-4">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center">
                        <span className="text-2xl mr-3">{getMethodIcon(method.gatewayName)}</span>
                        <div>
                          <h3 className="font-semibold text-gray-900">{method.gatewayName}</h3>
                          <p className="text-sm text-gray-600">Provider: {method.providerName}</p>
                          <p className="text-sm text-gray-600">Status: 
                            <span className={`px-2 py-1 rounded-full text-xs font-semibold ${method.isActive ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'}`}>
                              {method.isActive ? 'Active' : 'Inactive'}
                            </span>
                          </p>
                        </div>
                      </div>
                      <div className="flex space-x-2">
                        {method.isActive && (
                          <button className="px-3 py-1 bg-green-600 text-white text-sm rounded hover:bg-green-700">
                            Enable
                          </button>
                        )}
                        <button className="px-3 py-1 bg-red-600 text-white text-sm rounded hover:bg-red-700">
                            Disable
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {viewMode === 'transactions' && (
          <div>
            {/* Search and Filters */}
            <div className="bg-white rounded-lg shadow p-6 mb-8">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Transaction History</h2>
              
              <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6">
                <input
                  type="date"
                  placeholder="Start Date"
                  value={searchFilters.startDate}
                  onChange={(e) => setSearchFilters({...searchFilters, startDate: e.target.value})}
                  className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <input
                  type="date"
                  placeholder="End Date"
                  value={searchFilters.endDate}
                  onChange={(e) => setSearchFilters({...searchFilters, endDate: e.target.value})}
                  className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <select
                  value={searchFilters.status}
                  onChange={(e) => setSearchFilters({...searchFilters, status: e.target.value})}
                  className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="">All Status</option>
                  <option value="COMPLETED">Completed</option>
                  <option value="PENDING">Pending</option>
                  <option value="FAILED">Failed</option>
                  <option value="REFUNDED">Refunded</option>
                </select>
                <select
                  value={searchFilters.method}
                  onChange={(e) => setSearchFilters({...searchFilters, method: e.target.value})}
                  className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                >
                  <option value="">All Methods</option>
                  <option value="CREDIT_CARD">Credit Card</option>
                  <option value="DEBIT_CARD">Debit Card</option>
                  <option value="NET_BANKING">Net Banking</option>
                  <option value="UPI">UPI</option>
                  <option value="CASH">Cash</option>
                </select>
                <input
                  type="text"
                  placeholder="Transaction ID"
                  value={searchFilters.transactionId}
                  onChange={(e) => setSearchFilters({...searchFilters, transactionId: e.target.value})}
                  className="px-3 py-2 border border-gray-300 rounded focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <button
                  onClick={handleSearch}
                  className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                  Search
                </button>
              </div>
            </div>

            {/* Transactions List */}
            <div className="bg-white rounded-lg shadow overflow-hidden">
              <div className="px-6 py-4">
                <div className="flex justify-between items-center mb-4">
                  <h3 className="text-lg font-semibold text-gray-900">Recent Transactions</h3>
                  <span className="text-sm text-gray-600">
                    {transactions.length} transactions found
                  </span>
                </div>
              </div>
              
              <div className="overflow-x-auto">
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Transaction ID
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Date
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Method
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Amount
                      </th>
                      <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                        Status
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {transactions.map((transaction) => (
                      <tr key={transaction.id} className="hover:bg-gray-50">
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                          {transaction.transactionId}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                          {formatDate(transaction.paymentDate)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                          <div className="flex items-center">
                            <span className="mr-2">{getMethodIcon(transaction.paymentMethod)}</span>
                            {transaction.paymentMethod}
                          </div>
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                          {formatCurrency(transaction.amount)}
                        </td>
                        <td className="px-6 py-4 whitespace-nowrap">
                          <span className={`px-2 py-1 rounded-full text-xs font-semibold ${getStatusColor(transaction.paymentStatus)}`}>
                            {transaction.paymentStatus}
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

        {viewMode === 'methods' && (
          <div className="space-y-6">
            {/* Payment Methods Management */}
            <div className="bg-white rounded-lg shadow p-6">
              <h2 className="text-xl font-semibold text-gray-900 mb-4">Payment Methods Management</h2>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                {paymentMethods.map((method) => (
                  <div key={method.id} className="bg-white border rounded-lg p-4">
                    <div className="flex items-center justify-between mb-3">
                      <div className="flex items-center">
                        <span className="text-2xl mr-3">{getMethodIcon(method.gatewayName)}</span>
                        <div>
                          <h3 className="font-semibold text-gray-900">{method.gatewayName}</h3>
                          <p className="text-sm text-gray-600">Provider: {method.providerName}</p>
                          <p className="text-sm text-gray-600">Status: 
                            <span className={`px-2 py-1 rounded-full text-xs font-semibold ${method.isActive ? 'bg-green-100 text-green-600' : 'bg-red-100 text-red-600'}`}>
                              {method.isActive ? 'Active' : 'Inactive'}
                            </span>
                          </p>
                        </div>
                      </div>
                    </div>
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

export default PaymentDashboard;
