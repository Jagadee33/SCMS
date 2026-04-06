import React from 'react';
import { useNavigate } from 'react-router-dom';

const Library = () => {
  const navigate = useNavigate();
  
  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Library Management</h1>
      <div className="bg-white shadow rounded-lg p-6">
        <button 
          onClick={() => navigate('/library')}
          className="btn btn-primary mb-4"
        >
          📚 Go to Enhanced Library Dashboard
        </button>
        <p className="text-gray-600">The library has been enhanced with a comprehensive digital library management system!</p>
        <div className="mt-4 space-y-2 text-sm text-gray-600">
          <p>✨ <strong>Advanced Features:</strong></p>
          <ul className="list-disc list-inside space-y-1 ml-4">
            <li>📚 Complete Book Catalog Management</li>
            <li>📋 Advanced Search & Filtering</li>
            <li>📊 Real-time Availability Tracking</li>
            <li>🔖 Smart Reservation System</li>
            <li>📅 Issue & Return Management</li>
            <li>💰 Automated Fine Calculation</li>
            <li>📱 Multi-device Responsive Design</li>
          </ul>
          <p className="mt-4">🎯 <strong>Click the button above to access the new enhanced Library Dashboard!</strong></p>
        </div>
      </div>
    </div>
  );
};

export default Library;
