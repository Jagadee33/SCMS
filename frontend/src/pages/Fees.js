import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { feeAPI, studentAPI } from '../services/api';
import PaymentGateway from '../components/PaymentGateway';

const Fees = () => {
  const { hasRole } = useAuth();
  const [fees, setFees] = useState([]);
  const [students, setStudents] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showDiscountModal, setShowDiscountModal] = useState(false);
  const [showPaymentGateway, setShowPaymentGateway] = useState(false);
  const [editingFee, setEditingFee] = useState(null);
  const [selectedFee, setSelectedFee] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [filter, setFilter] = useState({
    studentId: '',
    status: '',
    feeType: '',
    academicYear: ''
  });
  const [formData, setFormData] = useState({
    studentId: null,
    feeType: 'Tuition',
    description: '',
    amount: '',
    dueDate: '',
    academicYear: '2024-2025',
    semester: 'Fall',
    lateFee: '',
    discountAmount: '',
    discountReason: ''
  });
  const [paymentData, setPaymentData] = useState({
    paymentAmount: '',
    paymentMethod: 'Cash',
    transactionId: ''
  });
  const [discountData, setDiscountData] = useState({
    discountAmount: '',
    discountReason: ''
  });
  const [viewMode, setViewMode] = useState('cards');

  useEffect(() => {
    fetchFees();
    fetchStudents();
  }, []);

  const fetchFees = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await feeAPI.getAll();
      setFees(response.data);
    } catch (err) {
      console.error('Error fetching fees:', err);
      setError('Failed to fetch fees');
    } finally {
      setLoading(false);
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

  const filteredFees = fees.filter(fee => {
    if (filter.studentId && fee.student?.id !== filter.studentId) return false;
    if (filter.status && fee.status !== filter.status) return false;
    if (filter.feeType && fee.feeType !== filter.feeType) return false;
    if (filter.academicYear && fee.academicYear !== filter.academicYear) return false;
    return true;
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handlePaymentInputChange = (e) => {
    const { name, value } = e.target;
    setPaymentData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleDiscountInputChange = (e) => {
    const { name, value } = e.target;
    setDiscountData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setError(null);
      
      const feeData = {
        student: { id: parseInt(formData.studentId) },
        feeType: formData.feeType,
        description: formData.description,
        amount: parseFloat(formData.amount),
        dueDate: formData.dueDate ? new Date(formData.dueDate).toISOString() : null,
        academicYear: formData.academicYear,
        semester: formData.semester,
        lateFee: formData.lateFee ? parseFloat(formData.lateFee) : null,
        discountAmount: formData.discountAmount ? parseFloat(formData.discountAmount) : null,
        discountReason: formData.discountReason
      };

      if (editingFee) {
        await feeAPI.update(editingFee.id, feeData);
      } else {
        await feeAPI.create(feeData);
      }
      
      await fetchFees();
      resetForm();
    } catch (err) {
      console.error('Error saving fee:', err);
      setError('Failed to save fee');
    }
  };

  const handleEdit = (fee) => {
    setEditingFee(fee);
    setFormData({
      studentId: fee.student?.id,
      feeType: fee.feeType,
      description: fee.description,
      amount: fee.amount,
      dueDate: new Date(fee.dueDate).toISOString().split('T')[0],
      academicYear: fee.academicYear,
      semester: fee.semester,
      lateFee: fee.lateFee,
      discountAmount: fee.discountAmount,
      discountReason: fee.discountReason
    });
    setShowModal(true);
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this fee record?')) {
      try {
        setError(null);
        await feeAPI.delete(id);
        await fetchFees();
      } catch (err) {
        console.error('Error deleting fee:', err);
        setError('Failed to delete fee');
      }
    }
  };

  const handlePayment = async (e) => {
    e.preventDefault();
    try {
      setError(null);
      
      const paymentInfo = {
        paymentAmount: paymentData.paymentAmount,
        paymentMethod: paymentData.paymentMethod,
        transactionId: paymentData.transactionId
      };

      await feeAPI.processPayment(selectedFee.id, paymentInfo);
      await fetchFees();
      resetPaymentForm();
      setShowPaymentModal(false);
    } catch (err) {
      setError('Payment processing failed: ' + (err.message || 'Unknown error'));
    }
  };

  const handlePaymentGateway = async (paymentData) => {
    try {
      setError(null);
      
      // Process payment through the new payment gateway
      await feeAPI.processPayment(paymentData.feeId, {
        paymentAmount: paymentData.amount,
        paymentMethod: paymentData.paymentMethod,
        transactionId: paymentData.gatewayTransactionId
      });
      
      await fetchFees();
      setSelectedFee(null);
    } catch (err) {
      setError('Payment processing failed: ' + (err.message || 'Unknown error'));
      throw err; // Re-throw to let PaymentGateway handle the error
    }
  };

  const handleDiscount = async (e) => {
    e.preventDefault();
    try {
      setError(null);
      
      const discountInfo = {
        discountAmount: parseFloat(discountData.discountAmount),
        discountReason: discountData.discountReason
      };

      await feeAPI.applyDiscount(selectedFee.id, discountInfo);
      await fetchFees();
      resetDiscountForm();
    } catch (err) {
      console.error('Error applying discount:', err);
      setError('Failed to apply discount');
    }
  };

  const resetForm = () => {
    setFormData({
      studentId: null,
      feeType: 'Tuition',
      description: '',
      amount: '',
      dueDate: '',
      academicYear: '2024-2025',
      semester: 'Fall',
      lateFee: '',
      discountAmount: '',
      discountReason: ''
    });
    setEditingFee(null);
    setShowModal(false);
  };

  const resetPaymentForm = () => {
    setPaymentData({
      paymentAmount: '',
      paymentMethod: 'Cash',
      transactionId: ''
    });
    setSelectedFee(null);
    setShowPaymentModal(false);
  };

  const resetDiscountForm = () => {
    setDiscountData({
      discountAmount: '',
      discountReason: ''
    });
    setSelectedFee(null);
    setShowDiscountModal(false);
  };

  const getStatusBadge = (status) => {
    const colors = {
      Pending: 'bg-yellow-100 text-yellow-800 border-yellow-200',
      Paid: 'bg-green-100 text-green-800 border-green-200',
      Partial: 'bg-blue-100 text-blue-800 border-blue-200',
      Overdue: 'bg-red-100 text-red-800 border-red-200',
      Cancelled: 'bg-gray-100 text-gray-800 border-gray-200'
    };
    return colors[status] || 'bg-gray-100 text-gray-800 border-gray-200';
  };

  const getFeeTypeIcon = (feeType) => {
    const icons = {
      Tuition: '🎓',
      Library: '📚',
      Lab: '🔬',
      Hostel: '🏠',
      Transport: '🚌'
    };
    return icons[feeType] || '💰';
  };

  const getPaymentMethodIcon = (method) => {
    const icons = {
      Cash: '💵',
      Card: '💳',
      'Bank Transfer': '🏦',
      Online: '🌐'
    };
    return icons[method] || '💰';
  };

  const formatCurrency = (amount) => {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(amount || 0);
  };

  const calculateProgress = (fee) => {
    const paid = fee.paidAmount || 0;
    const total = fee.amount;
    return total > 0 ? (paid / total) * 100 : 0;
  };

  const getProgressColor = (progress) => {
    if (progress === 100) return 'bg-green-500';
    if (progress >= 75) return 'bg-blue-500';
    if (progress >= 50) return 'bg-yellow-500';
    if (progress >= 25) return 'bg-orange-500';
    return 'bg-red-500';
  };

  if (loading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg text-gray-600 animate-pulse">Loading fees...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="text-lg text-red-600 bg-red-50 p-4 rounded-lg">⚠️ Error: {error}</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        {/* Header */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <div className="flex justify-between items-center">
            <div>
              <h1 className="text-3xl font-bold text-gray-900 flex items-center">
                <span className="mr-3">💰</span>
                Fee Management
              </h1>
              <p className="text-gray-600 mt-2">Manage student fees, payments, and discounts</p>
            </div>
            <div className="flex space-x-4">
              <button
                onClick={() => setShowModal(true)}
                className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-3 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200 shadow-lg flex items-center"
              >
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                </svg>
                Add Fee
              </button>
              <button
                onClick={() => setViewMode(viewMode === 'cards' ? 'table' : 'cards')}
                className="bg-gray-100 text-gray-700 px-4 py-3 rounded-lg hover:bg-gray-200 transition-colors flex items-center"
              >
                <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 10h18M3 14h18m-9-4h9m-9 4h9" />
                </svg>
                {viewMode === 'cards' ? 'Table View' : 'Card View'}
              </button>
            </div>
          </div>
        </div>

        {/* Filters */}
        <div className="bg-white rounded-xl shadow-lg p-6 mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4 flex items-center">
            <span className="mr-2">🔍</span>
            Filters & Search
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Student</label>
              <select
                value={filter.studentId}
                onChange={(e) => setFilter(prev => ({ ...prev, studentId: e.target.value }))}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
              <label className="block text-sm font-medium text-gray-700 mb-2">Status</label>
              <select
                value={filter.status}
                onChange={(e) => setFilter(prev => ({ ...prev, status: e.target.value }))}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">All Status</option>
                <option value="Pending">⏳ Pending</option>
                <option value="Paid">✅ Paid</option>
                <option value="Partial">💰 Partial</option>
                <option value="Overdue">⚠️ Overdue</option>
                <option value="Cancelled">❌ Cancelled</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Fee Type</label>
              <select
                value={filter.feeType}
                onChange={(e) => setFilter(prev => ({ ...prev, feeType: e.target.value }))}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">All Types</option>
                <option value="Tuition">🎓 Tuition</option>
                <option value="Library">📚 Library</option>
                <option value="Lab">🔬 Lab</option>
                <option value="Hostel">🏠 Hostel</option>
                <option value="Transport">🚌 Transport</option>
              </select>
            </div>
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-2">Academic Year</label>
              <select
                value={filter.academicYear}
                onChange={(e) => setFilter(prev => ({ ...prev, academicYear: e.target.value }))}
                className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              >
                <option value="">All Years</option>
                <option value="2024-2025">2024-2025</option>
                <option value="2023-2024">2023-2024</option>
              </select>
            </div>
          </div>
        </div>

        {/* Statistics Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-gradient-to-br from-blue-500 to-blue-600 rounded-xl p-6 text-white">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold">Total Fees</h3>
              <span className="text-2xl">💰</span>
            </div>
            <p className="text-3xl font-bold mt-2">
              {formatCurrency(fees.reduce((sum, fee) => sum + fee.amount, 0))}
            </p>
            <p className="text-blue-100 text-sm mt-1">Across all students</p>
          </div>
          <div className="bg-gradient-to-br from-green-500 to-green-600 rounded-xl p-6 text-white">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold">Collected</h3>
              <span className="text-2xl">✅</span>
            </div>
            <p className="text-3xl font-bold mt-2">
              {formatCurrency(fees.reduce((sum, fee) => sum + (fee.paidAmount || 0), 0))}
            </p>
            <p className="text-green-100 text-sm mt-1">Successfully paid</p>
          </div>
          <div className="bg-gradient-to-br from-yellow-500 to-orange-600 rounded-xl p-6 text-white">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold">Pending</h3>
              <span className="text-2xl">⏳</span>
            </div>
            <p className="text-3xl font-bold mt-2">
              {fees.filter(fee => fee.status === 'Pending').length}
            </p>
            <p className="text-yellow-100 text-sm mt-1">Awaiting payment</p>
          </div>
          <div className="bg-gradient-to-br from-red-500 to-red-600 rounded-xl p-6 text-white">
            <div className="flex items-center justify-between">
              <h3 className="text-lg font-semibold">Overdue</h3>
              <span className="text-2xl">⚠️</span>
            </div>
            <p className="text-3xl font-bold mt-2">
              {fees.filter(fee => fee.status === 'Overdue').length}
            </p>
            <p className="text-red-100 text-sm mt-1">Require attention</p>
          </div>
        </div>

        {/* Fee Display */}
        {filteredFees.length === 0 ? (
          <div className="bg-white rounded-xl shadow-lg p-12 text-center">
            <div className="text-gray-500 text-lg">📭 No fees found matching your filters</div>
            <p className="text-gray-400 mt-2">Try adjusting your filter criteria</p>
          </div>
        ) : viewMode === 'cards' ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredFees.map((fee) => (
              <div key={fee.id} className="bg-white rounded-xl shadow-lg overflow-hidden hover:shadow-xl transition-shadow duration-300">
                {/* Header */}
                <div className={`p-4 ${getStatusBadge(fee.status)}`}>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      <span className="text-2xl mr-3">{getFeeTypeIcon(fee.feeType)}</span>
                      <div>
                        <h3 className="text-lg font-bold text-gray-900">{fee.feeType}</h3>
                        <p className="text-sm text-gray-600">{fee.student?.firstName} {fee.student?.lastName}</p>
                      </div>
                    </div>
                    <span className={`px-3 py-1 rounded-full text-xs font-semibold ${getStatusBadge(fee.status)}`}>
                      {fee.status}
                    </span>
                  </div>
                </div>

                {/* Amount and Progress */}
                <div className="p-4 border-t border-gray-100">
                  <div className="flex justify-between items-center mb-3">
                    <span className="text-gray-600">Total Amount:</span>
                    <span className="text-xl font-bold text-gray-900">{formatCurrency(fee.amount)}</span>
                  </div>
                  <div className="flex justify-between items-center mb-3">
                    <span className="text-gray-600">Paid Amount:</span>
                    <span className="text-xl font-bold text-green-600">{formatCurrency(fee.paidAmount || 0)}</span>
                  </div>
                  <div className="flex justify-between items-center mb-3">
                    <span className="text-gray-600">Remaining:</span>
                    <span className="text-xl font-bold text-orange-600">{formatCurrency(fee.getRemainingAmount)}</span>
                  </div>

                  {/* Progress Bar */}
                  <div className="mt-4">
                    <div className="flex justify-between text-sm text-gray-600 mb-1">
                      <span>Payment Progress</span>
                      <span>{Math.round(calculateProgress(fee))}%</span>
                    </div>
                    <div className="w-full bg-gray-200 rounded-full h-3">
                      <div 
                        className={`h-3 rounded-full transition-all duration-500 ${getProgressColor(calculateProgress(fee))}`}
                        style={{ width: `${calculateProgress(fee)}%` }}
                      ></div>
                    </div>
                  </div>
                </div>

                {/* Actions */}
                <div className="p-4 border-t border-gray-100 bg-gray-50">
                  <div className="grid grid-cols-3 gap-2">
                    <button
                      onClick={() => handleEdit(fee)}
                      className="bg-blue-100 text-blue-700 px-3 py-2 rounded-lg hover:bg-blue-200 transition-colors flex items-center justify-center"
                    >
                      <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h5a2 2 0 002-2V7a2 2 0 00-2-2z" />
                      </svg>
                      Edit
                    </button>
                    <button
                      onClick={() => {setSelectedFee(fee); setShowPaymentGateway(true)}}
                      className="bg-green-100 text-green-700 px-3 py-2 rounded-lg hover:bg-green-200 transition-colors flex items-center justify-center"
                      disabled={fee.status === 'Paid'}
                    >
                      <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 9V7a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h10a2 2 0 002-2v-4a2 2 0 002-2z" />
                      </svg>
                      Pay Now
                    </button>
                    <button
                      onClick={() => {setSelectedFee(fee); setShowDiscountModal(true)}}
                      className="bg-yellow-100 text-yellow-700 px-3 py-2 rounded-lg hover:bg-yellow-200 transition-colors flex items-center justify-center"
                      disabled={fee.status === 'Paid'}
                    >
                      <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2c.55 0 1.05-.45 1.5-1 1.5-.7.95-1.65-1.95-2.05-2.9-2.9-2.9z" />
                      </svg>
                      Discount
                    </button>
                  </div>
                  <button
                    onClick={() => handleDelete(fee.id)}
                    className="bg-red-100 text-red-700 px-3 py-2 rounded-lg hover:bg-red-200 transition-colors flex items-center justify-center mt-2 w-full"
                  >
                    <svg className="w-4 h-4 mr-1" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 011.138 2H9.862a2 2 0 01-1.139-2l-.867-12.142z" />
                    </svg>
                    Delete
                  </button>
                </div>

                {/* Due Date */}
                <div className="mt-3 pt-3 border-t border-gray-200">
                  <div className="flex items-center text-sm text-gray-600">
                    <svg className="w-4 h-4 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 4h18M5 21h14a2 2 0 002-2V8a2 2 0 00-2-2H5z" />
                    </svg>
                    Due: {new Date(fee.dueDate).toLocaleDateString()}
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          /* Table View */
          <div className="bg-white rounded-xl shadow-lg overflow-hidden">
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">ID</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Student</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Paid</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Remaining</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Due Date</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {filteredFees.map((fee) => (
                    <tr key={fee.id} className="hover:bg-gray-50">
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{fee.id}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        {fee.student?.firstName} {fee.student?.lastName}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                        <span className="mr-2">{getFeeTypeIcon(fee.feeType)}</span>
                        {fee.feeType}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{formatCurrency(fee.amount)}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{formatCurrency(fee.paidAmount || 0)}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{formatCurrency(fee.getRemainingAmount)}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{new Date(fee.dueDate).toLocaleDateString()}</td>
                      <td className="px-6 py-4 whitespace-nowrap">
                        <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusBadge(fee.status)}`}>
                          {fee.status}
                        </span>
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        <button
                          onClick={() => handleEdit(fee)}
                          className="text-indigo-600 hover:text-indigo-900 mr-2 transition-colors"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => {setSelectedFee(fee); setShowPaymentGateway(true)}}
                          className="text-green-600 hover:text-green-900 mr-2 transition-colors"
                          disabled={fee.status === 'Paid'}
                        >
                          Pay Now
                        </button>
                        <button
                          onClick={() => {setSelectedFee(fee); setShowDiscountModal(true)}}
                          className="text-yellow-600 hover:text-yellow-900 mr-2 transition-colors"
                          disabled={fee.status === 'Paid'}
                        >
                          Discount
                        </button>
                        <button
                          onClick={() => handleDelete(fee.id)}
                          className="text-red-600 hover:text-red-900 transition-colors"
                        >
                          Delete
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Add/Edit Fee Modal */}
        {showModal && (
          <div className="fixed inset-0 bg-black bg-opacity-50 overflow-y-auto h-full w-full z-50 flex items-center justify-center">
            <div className="relative bg-white rounded-xl shadow-2xl w-full max-w-2xl mx-4 my-8">
              <div className="bg-gradient-to-r from-blue-500 to-blue-600 px-6 py-4 text-white">
                <h3 className="text-xl font-bold flex items-center">
                  <span className="mr-3">💰</span>
                  {editingFee ? 'Edit Fee' : 'Add New Fee'}
                </h3>
              </div>
              <div className="p-6">
                <form onSubmit={handleSubmit}>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Student *</label>
                      <select
                        name="studentId"
                        value={formData.studentId}
                        onChange={handleInputChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
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
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Fee Type *</label>
                      <select
                        name="feeType"
                        value={formData.feeType}
                        onChange={handleInputChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      >
                        <option value="Tuition">🎓 Tuition</option>
                        <option value="Library">📚 Library</option>
                        <option value="Lab">🔬 Lab</option>
                        <option value="Hostel">🏠 Hostel</option>
                        <option value="Transport">🚌 Transport</option>
                      </select>
                    </div>
                  </div>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Description</label>
                    <textarea
                      name="description"
                      value={formData.description}
                      onChange={handleInputChange}
                      rows="3"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      placeholder="Enter fee description..."
                    />
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Amount *</label>
                      <input
                        type="number"
                        name="amount"
                        value={formData.amount}
                        onChange={handleInputChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        placeholder="0.00"
                        required
                      />
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Due Date *</label>
                      <input
                        type="date"
                        name="dueDate"
                        value={formData.dueDate}
                        onChange={handleInputChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                        required
                      />
                    </div>
                  </div>
                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Academic Year</label>
                      <select
                        name="academicYear"
                        value={formData.academicYear}
                        onChange={handleInputChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      >
                        <option value="2024-2025">2024-2025</option>
                        <option value="2023-2024">2023-2024</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-2">Semester</label>
                      <select
                        name="semester"
                        value={formData.semester}
                        onChange={handleInputChange}
                        className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                      >
                        <option value="Fall">Fall</option>
                        <option value="Spring">Spring</option>
                        <option value="Summer">Summer</option>
                      </select>
                    </div>
                  </div>
                  <div className="flex justify-end space-x-4 mt-8">
                    <button
                      type="button"
                      onClick={resetForm}
                      className="bg-gray-200 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="bg-gradient-to-r from-blue-500 to-blue-600 text-white px-6 py-3 rounded-lg hover:from-blue-600 hover:to-blue-700 transition-all duration-200"
                    >
                      {editingFee ? 'Update Fee' : 'Add Fee'}
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        )}

        {/* Payment Modal */}
        {showPaymentModal && selectedFee && (
          <div className="fixed inset-0 bg-black bg-opacity-50 overflow-y-auto h-full w-full z-50 flex items-center justify-center">
            <div className="relative bg-white rounded-xl shadow-2xl w-full max-w-lg mx-4 my-8">
              <div className="bg-gradient-to-r from-green-500 to-green-600 px-6 py-4 text-white">
                <h3 className="text-xl font-bold flex items-center">
                  <span className="mr-3">{getPaymentMethodIcon(paymentData.paymentMethod)}</span>
                  Process Payment
                </h3>
              </div>
              <div className="p-6">
                <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3">Fee Details</h4>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div><span className="text-gray-600">Student:</span> <span className="font-medium">{selectedFee.student?.firstName} {selectedFee.student?.lastName}</span></div>
                    <div><span className="text-gray-600">Type:</span> <span className="font-medium">{selectedFee.feeType}</span></div>
                    <div><span className="text-gray-600">Total:</span> <span className="font-medium">{formatCurrency(selectedFee.amount)}</span></div>
                    <div><span className="text-gray-600">Paid:</span> <span className="font-medium text-green-600">{formatCurrency(selectedFee.paidAmount || 0)}</span></div>
                    <div><span className="text-gray-600">Remaining:</span> <span className="font-medium text-orange-600">{formatCurrency(selectedFee.getRemainingAmount)}</span></div>
                  </div>
                </div>
                <form onSubmit={handlePayment}>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Payment Amount *</label>
                    <input
                      type="number"
                      name="paymentAmount"
                      value={paymentData.paymentAmount}
                      onChange={handlePaymentInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      max={selectedFee.getRemainingAmount()}
                      placeholder="0.00"
                      required
                    />
                  </div>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Payment Method *</label>
                    <select
                      name="paymentMethod"
                      value={paymentData.paymentMethod}
                      onChange={handlePaymentInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                    >
                      <option value="Cash">💵 Cash</option>
                      <option value="Card">💳 Card</option>
                      <option value="Bank Transfer">🏦 Bank Transfer</option>
                      <option value="Online">🌐 Online</option>
                    </select>
                  </div>
                  <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Transaction ID</label>
                    <input
                      type="text"
                      name="transactionId"
                      value={paymentData.transactionId}
                      onChange={handlePaymentInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-green-500 focus:border-transparent"
                      placeholder="Enter transaction ID..."
                    />
                  </div>
                  <div className="flex justify-end space-x-4">
                    <button
                      type="button"
                      onClick={resetPaymentForm}
                      className="bg-gray-200 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="bg-gradient-to-r from-green-500 to-green-600 text-white px-6 py-3 rounded-lg hover:from-green-600 hover:to-green-700 transition-all duration-200"
                    >
                      Process Payment
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        )}

        {/* Discount Modal */}
        {showDiscountModal && selectedFee && (
          <div className="fixed inset-0 bg-black bg-opacity-50 overflow-y-auto h-full w-full z-50 flex items-center justify-center">
            <div className="relative bg-white rounded-xl shadow-2xl w-full max-w-lg mx-4 my-8">
              <div className="bg-gradient-to-r from-yellow-500 to-orange-600 px-6 py-4 text-white">
                <h3 className="text-xl font-bold flex items-center">
                  <span className="mr-3">🎟</span>
                  Apply Discount
                </h3>
              </div>
              <div className="p-6">
                <div className="mb-6 p-4 bg-gray-50 rounded-lg">
                  <h4 className="font-semibold text-gray-900 mb-3">Fee Information</h4>
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div><span className="text-gray-600">Student:</span> <span className="font-medium">{selectedFee.student?.firstName} {selectedFee.student?.lastName}</span></div>
                    <div><span className="text-gray-600">Type:</span> <span className="font-medium">{selectedFee.feeType}</span></div>
                    <div><span className="text-gray-600">Total:</span> <span className="font-medium">{formatCurrency(selectedFee.amount)}</span></div>
                    <div><span className="text-gray-600">Remaining:</span> <span className="font-medium text-orange-600">{formatCurrency(selectedFee.getRemainingAmount)}</span></div>
                  </div>
                </div>
                <form onSubmit={handleDiscount}>
                  <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Discount Amount *</label>
                    <input
                      type="number"
                      name="discountAmount"
                      value={discountData.discountAmount}
                      onChange={handleDiscountInputChange}
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500 focus:border-transparent"
                      max={selectedFee.getRemainingAmount()}
                      placeholder="0.00"
                      required
                    />
                  </div>
                  <div className="mb-6">
                    <label className="block text-sm font-medium text-gray-700 mb-2">Discount Reason *</label>
                    <textarea
                      name="discountReason"
                      value={discountData.discountReason}
                      onChange={handleDiscountInputChange}
                      rows="3"
                      className="w-full px-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-yellow-500 focus:border-transparent"
                      placeholder="Enter reason for discount..."
                      required
                    />
                  </div>
                  <div className="flex justify-end space-x-4">
                    <button
                      type="button"
                      onClick={resetDiscountForm}
                      className="bg-gray-200 text-gray-700 px-6 py-3 rounded-lg hover:bg-gray-300 transition-colors"
                    >
                      Cancel
                    </button>
                    <button
                      type="submit"
                      className="bg-gradient-to-r from-yellow-500 to-orange-600 text-white px-6 py-3 rounded-lg hover:from-yellow-600 hover:to-orange-700 transition-all duration-200"
                    >
                      Apply Discount
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        )}

        {/* Payment Gateway Modal */}
        {showPaymentGateway && selectedFee && (
          <PaymentGateway
            fee={selectedFee}
            onPaymentSuccess={handlePaymentGateway}
            onClose={() => {
              setShowPaymentGateway(false);
              setSelectedFee(null);
            }}
          />
        )}
      </div>
    </div>
  );
};

export default Fees;
