import React from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate, useLocation } from 'react-router-dom';

const Layout = ({ children }) => {
  const { logout, user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const menuItems = [
    { path: '/dashboard', label: 'Dashboard', icon: '📊' },
    { path: '/students', label: 'Students', icon: '👨‍🎓', role: 'ADMIN' },
    { path: '/faculty', label: 'Faculty', icon: '👨‍🏫', role: 'ADMIN' },
    { path: '/courses', label: 'Courses', icon: '📚', role: ['ADMIN', 'FACULTY', 'STUDENT'] },
    { path: '/departments', label: 'Departments', icon: '🏢', role: 'ADMIN' },
    { path: '/attendance', label: 'Attendance', icon: '📝', role: ['ADMIN', 'FACULTY'] },
    { path: '/grades', label: 'Grades', icon: '📊', role: ['ADMIN', 'FACULTY'] },
    { path: '/examinations', label: 'Examinations', icon: '📋', role: ['ADMIN', 'FACULTY', 'STUDENT'] },
    { path: '/library', label: 'Library', icon: '📖' },
    { path: '/fees', label: 'Fees', icon: '💰', role: ['ADMIN', 'STUDENT'] },
    { path: '/profile', label: 'Profile', icon: '👤' },
  ];

  const isActivePath = (path) => {
    return location.pathname === path || location.pathname.startsWith(path);
  };

  const hasPermission = (role) => {
    if (!user) return false;
    if (Array.isArray(role)) {
      return role.includes(user.role);
    }
    return user.role === role;
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Sidebar */}
      <div className="w-64 bg-gray-900 text-white flex flex-col">
        <div className="p-4">
          <h1 className="text-xl font-bold">College Management</h1>
        </div>
        <nav className="mt-4 flex-1">
          <ul className="space-y-2">
            {menuItems.map((item) => {
              if (item.role && !hasPermission(item.role)) {
                return null;
              }
              return (
                <li key={item.path}>
                  <button
                    onClick={() => navigate(item.path)}
                    className={`w-full text-left px-4 py-3 rounded-lg transition-colors ${
                      isActivePath(item.path)
                        ? 'bg-blue-600 text-white'
                        : 'text-gray-300 hover:bg-gray-800 hover:text-white'
                    }`}
                  >
                    <span className="mr-3">{item.icon}</span>
                    {item.label}
                  </button>
                </li>
              );
            })}
          </ul>
        </nav>
        
        {/* User Info */}
        <div className="p-4 border-t border-gray-700">
          <div className="text-sm text-gray-400 mb-3">
            <div className="font-medium text-white">{user?.email}</div>
            <div className="text-xs">Role: {user?.role}</div>
          </div>
          <button 
            onClick={handleLogout}
            className="w-full px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
          >
            Logout
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 flex flex-col">
        <header className="bg-white shadow-sm border-b">
          <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
            <div className="flex justify-between h-16">
              <div className="flex items-center">
                <h1 className="text-xl font-semibold text-gray-900">
                  {menuItems.find(item => isActivePath(item.path))?.label || 'College Management'}
                </h1>
              </div>
              <div className="flex items-center">
                <span className="text-sm text-gray-600">
                  Welcome, {user?.email}
                </span>
              </div>
            </div>
          </div>
        </header>
        
        <main className="flex-1 overflow-y-auto">
          <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
            {children}
          </div>
        </main>
      </div>
    </div>
  );
};

export default Layout;
