import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import Dashboard from '../pages/Dashboard';
import FacultyDashboard from '../pages/FacultyDashboard';
import StudentDashboard from '../pages/StudentDashboard';
import LibraryDashboard from '../pages/LibraryDashboard';

const RoleBasedDashboard = () => {
  const { user, hasRole } = useAuth();
  
  console.log('RoleBasedDashboard - user:', user);
  console.log('RoleBasedDashboard - hasRole ADMIN:', hasRole('ADMIN'));
  console.log('RoleBasedDashboard - hasRole FACULTY:', hasRole('FACULTY'));
  console.log('RoleBasedDashboard - hasRole STUDENT:', hasRole('STUDENT'));

  if (hasRole('ADMIN')) {
    return <Dashboard />;
  }

  if (hasRole('FACULTY')) {
    return <FacultyDashboard />;
  }

  if (hasRole('STUDENT')) {
    return <StudentDashboard />;
  }

  // Add Library Dashboard for all authenticated users
  if (user) {
    return <LibraryDashboard />;
  }

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-center">
        <h1 className="text-2xl font-bold text-gray-900 mb-2">Access Denied</h1>
        <p className="text-gray-600">You don't have permission to access this page.</p>
      </div>
    </div>
  );
};

export default RoleBasedDashboard;
