import React, { useState, useEffect, useCallback } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { feeAPI } from '../services/api';

const PaymentHistory = ({ studentId }) => {
  const { user } = useAuth();
  const [payments, setPayments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [filter] = useState({
    status: 'all',
    paymentMethod: 'all',
    dateRange: 'all'
  });

  useEffect(() => {
    fetchPaymentHistory();
  }, [fetchPaymentHistory]);

  const fetchPaymentHistory = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Get fees with payment information
      const feesResponse = await feeAPI.getAll();
      const allFees = feesResponse.data || [];
      
      // Filter fees for the specific student
      const studentFees = allFees.filter(fee => 
        fee.student?.id === (studentId || user?.id)
      );
      
      // Transform fees into payment history
      const paymentHistory = studentFees
        .filter(fee => fee.paidAmount > 0) // Only show fees with payments
        .map(fee => ({
          id: fee.id,
          feeType: fee.feeType,
          description: fee.description,
          amount: fee.amount,
          paidAmount: fee.paidAmount,
          remainingAmount: fee.getRemainingAmount,
          paymentDate: fee.paymentDate,
          paymentMethod: fee.paymentMethod,
          transactionId: fee.transactionId,
          receiptNumber: fee.receiptNumber,
          status: fee.status,
          academicYear: fee.academicYear,
          semester: fee.semester
        }))
        .sort((a, b) => new Date(b.paymentDate) - new Date(a.paymentDate));
      
      setPayments(paymentHistory);
    } catch (err) {
      setError('Failed to fetch payment history');
    } finally {
      setLoading(false);
    }
  }, [studentId, user?.id]);

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0
    }).format(amount || 0);
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    return new Date(dateString).toLocaleDateString('en-IN', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'Paid':
        return 'bg-green-100 text-green-800';
      case 'Partial':
        return 'bg-yellow-100 text-yellow-800';
      case 'Pending':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getPaymentMethodIcon = (method) => {
    const icons = {
      'Card': '💳',
      'Cash': '💵',
      'Bank Transfer': '🏦',
      'Online': '🌐',
      'UPI': '📱',
      'NetBanking': '🏦',
      'Wallet': '👛'
    };
    return icons[method] || '💰';
  };

  const getTotalPaid = () => {
    return payments.reduce((sum, payment) => sum + payment.paidAmount, 0);
  };

  const getTotalRemaining = () => {
    return payments.reduce((sum, payment) => sum + payment.remainingAmount, 0);
  };

  const downloadReceipt = (payment) => {
    // Generate receipt content
    const receiptContent = `
      <html>
        <head>
          <title>Payment Receipt - ${payment.receiptNumber}</title>
          <style>
            body { font-family: Arial, sans-serif; padding: 20px; }
            .header { text-align: center; border-bottom: 2px solid #333; padding-bottom: 20px; }
            .content { margin: 20px 0; }
            .footer { margin-top: 30px; border-top: 1px solid #ccc; padding-top: 20px; }
          </style>
        </head>
        <body>
          <div class="header">
            <h1>Payment Receipt</h1>
            <h2>College Management System</h2>
          </div>
          <div class="content">
            <p><strong>Receipt Number:</strong> ${payment.receiptNumber}</p>
            <p><strong>Transaction ID:</strong> ${payment.transactionId}</p>
            <p><strong>Payment Date:</strong> ${formatDate(payment.paymentDate)}</p>
            <p><strong>Fee Type:</strong> ${payment.feeType}</p>
            <p><strong>Description:</strong> ${payment.description}</p>
            <p><strong>Amount Paid:</strong> ${formatCurrency(payment.paidAmount)}</p>
            <p><strong>Payment Method:</strong> ${payment.paymentMethod}</p>
            <p><strong>Status:</strong> ${payment.status}</p>
          </div>
          <div class="footer">
            <p>Thank you for your payment!</p>
          </div>
        </body>
      </html>
    `;
    
    // Create and download the receipt
    const blob = new Blob([receiptContent], { type: 'text/html' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `receipt_${payment.receiptNumber}.html`;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
        <span className="ml-2 text-gray-600">Loading payment history...</span>
      </div>
    );
  }

  if (error) {
    return (
      <div className="bg-red-50 border border-red-200 rounded-lg p-4">
        <p className="text-red-800">{error}</p>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Paid</p>
              <p className="text-2xl font-bold text-green-600">
                {formatCurrency(getTotalPaid())}
              </p>
            </div>
            <div className="bg-green-100 rounded-full p-3">
              <span className="text-2xl">💰</span>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Remaining</p>
              <p className="text-2xl font-bold text-orange-600">
                {formatCurrency(getTotalRemaining())}
              </p>
            </div>
            <div className="bg-orange-100 rounded-full p-3">
              <span className="text-2xl">📋</span>
            </div>
          </div>
        </div>
        
        <div className="bg-white rounded-lg shadow p-6">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Transactions</p>
              <p className="text-2xl font-bold text-blue-600">
                {payments.length}
              </p>
            </div>
            <div className="bg-blue-100 rounded-full p-3">
              <span className="text-2xl">📊</span>
            </div>
          </div>
        </div>
      </div>

      {/* Payment History Table */}
      <div className="bg-white rounded-lg shadow overflow-hidden">
        <div className="px-6 py-4 border-b border-gray-200">
          <h3 className="text-lg font-semibold text-gray-900">Payment History</h3>
        </div>
        
        {payments.length === 0 ? (
          <div className="text-center py-8">
            <span className="text-4xl mb-4 block">💳</span>
            <p className="text-gray-500">No payment history available</p>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Receipt
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Fee Type
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Amount
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Method
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Date
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {payments.map((payment) => (
                  <tr key={payment.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {payment.receiptNumber}
                        </div>
                        <div className="text-sm text-gray-500">
                          {payment.transactionId}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {payment.feeType}
                        </div>
                        <div className="text-sm text-gray-500">
                          {payment.academicYear} - {payment.semester}
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div>
                        <div className="text-sm font-medium text-gray-900">
                          {formatCurrency(payment.paidAmount)}
                        </div>
                        {payment.remainingAmount > 0 && (
                          <div className="text-sm text-orange-600">
                            Remaining: {formatCurrency(payment.remainingAmount)}
                          </div>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <span className="mr-2">{getPaymentMethodIcon(payment.paymentMethod)}</span>
                        <span className="text-sm text-gray-900">{payment.paymentMethod}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900">
                      {formatDate(payment.paymentDate)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusColor(payment.status)}`}>
                        {payment.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => downloadReceipt(payment)}
                        className="text-blue-600 hover:text-blue-900 mr-2"
                      >
                        📄 Receipt
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default PaymentHistory;
