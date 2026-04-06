import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { Spinner } from './Spinner';

const ProtectedRoute = ({ children, requiredRole }) => {
  const { isAuthenticated, loading, hasRole, user } = useAuth();
  
  console.log('ProtectedRoute - isAuthenticated:', isAuthenticated);
  console.log('ProtectedRoute - requiredRole:', requiredRole);
  console.log('ProtectedRoute - hasRole result:', requiredRole ? hasRole(requiredRole) : 'no role required');
  console.log('ProtectedRoute - user:', user);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <Spinner size="lg" />
      </div>
    );
  }

  if (!isAuthenticated) {
    console.log('ProtectedRoute - not authenticated, redirecting to login');
    return <Navigate to="/login" replace />;
  }

  if (requiredRole && !hasRole(requiredRole)) {
    console.log('ProtectedRoute - access denied, user does not have required role');
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h1>
          <p className="text-gray-600">You don't have permission to access this page.</p>
        </div>
      </div>
    );
  }

  return children;
};

export default ProtectedRoute;
