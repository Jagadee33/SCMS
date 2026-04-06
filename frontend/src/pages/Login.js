import React, { useState } from 'react';
import { authAPI } from '../services/api';
import { EyeIcon, EyeSlashIcon } from '@heroicons/react/24/outline';

const Login = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
  });
  const [showPassword, setShowPassword] = useState(false);
  const [showContactInfo, setShowContactInfo] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await authAPI.login(formData);
      console.log('Login response:', response.data);
      
      if (response.data && response.data.token) {
        console.log('Login successful, storing token and user');
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('user', JSON.stringify(response.data.user));
        
        // Force immediate redirect to ensure fresh AuthContext load
        console.log('Redirecting to dashboard with fresh token...');
        window.location.href = '/dashboard';
      } else {
        console.log('Login failed - no token in response');
        setError('Invalid credentials');
      }
    } catch (error) {
      setError('Login failed: ' + (error.message || 'Unknown error'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <div className="mx-auto h-12 w-12 flex items-center justify-center rounded-full bg-primary-100">
            <svg className="h-8 w-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
            </svg>
          </div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-gray-900">
            Sign in to College Portal
          </h2>
          <p className="mt-2 text-center text-sm text-gray-600">
            Need an account?{' '}
            <button 
              onClick={() => setShowContactInfo(!showContactInfo)}
              className="font-medium text-primary-600 hover:text-primary-500"
            >
              Contact Administration
            </button>
          </p>
        </div>

        {/* Contact Administration Section */}
        {showContactInfo && (
          <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 mb-6">
            <h3 className="text-lg font-semibold text-blue-900 mb-4 flex items-center">
              <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 8l7.89 5.26a2 2 0 002.22 0L21 8M5 19h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v10a2 2 0 002 2z" />
              </svg>
              Contact Administration for Account
            </h3>
            
            <div className="space-y-3">
              <div className="bg-white rounded-lg p-4">
                <h4 className="font-semibold text-gray-900 mb-2">📧 Email Support</h4>
                <p className="text-sm text-gray-600">
                  <strong>Primary:</strong> admin@college.edu<br />
                  <strong>IT Support:</strong> itsupport@college.edu<br />
                  <strong>Academic Affairs:</strong> academics@college.edu
                </p>
              </div>
              
              <div className="bg-white rounded-lg p-4">
                <h4 className="font-semibold text-gray-900 mb-2">📞 Phone Support</h4>
                <p className="text-sm text-gray-600">
                  <strong>Admin Office:</strong> +91-123-456-7890<br />
                  <strong>IT Helpdesk:</strong> +91-123-456-7891<br />
                  <strong>Emergency:</strong> +91-123-456-7892
                </p>
              </div>
              
              <div className="bg-white rounded-lg p-4">
                <h4 className="font-semibold text-gray-900 mb-2">🏢 Office Location</h4>
                <p className="text-sm text-gray-600">
                  <strong>Admin Block, Room 101</strong><br />
                  Main Campus, College Road<br />
                  <strong>Working Hours:</strong> 9:00 AM - 5:00 PM<br />
                  <strong>Monday - Friday</strong>
                </p>
              </div>
              
              <div className="bg-white rounded-lg p-4">
                <h4 className="font-semibold text-gray-900 mb-2">👥 Available Staff</h4>
                <p className="text-sm text-gray-600">
                  <strong>System Administrator:</strong> John Smith<br />
                  <strong>Academic Coordinator:</strong> Sarah Johnson<br />
                  <strong>IT Support Lead:</strong> Michael Chen
                </p>
              </div>
              
              <div className="bg-yellow-50 border border-yellow-200 rounded-lg p-4">
                <h4 className="font-semibold text-yellow-900 mb-2">⚡ Quick Account Request</h4>
                <p className="text-sm text-yellow-800">
                  Please provide your full name, department, student/faculty ID, and contact number when requesting an account. 
                  Account creation typically takes 24-48 hours.
                </p>
              </div>
            </div>
          </div>
        )}
        
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {error && (
            <div className="rounded-md bg-red-50 p-4">
              <div className="text-sm text-red-800">{error}</div>
            </div>
          )}
          
          <div className="space-y-4">
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700">
                Email address
              </label>
              <input
                id="email"
                name="email"
                type="email"
                autoComplete="email"
                required
                className="mt-1 input"
                placeholder="Enter your email"
                value={formData.email}
                onChange={handleChange}
              />
            </div>
            
            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700">
                Password
              </label>
              <div className="mt-1 relative">
                <input
                  id="password"
                  name="password"
                  type={showPassword ? 'text' : 'password'}
                  autoComplete="current-password"
                  required
                  className="input pr-10"
                  placeholder="Enter your password"
                  value={formData.password}
                  onChange={handleChange}
                />
                <button
                  type="button"
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeSlashIcon className="h-5 w-5 text-gray-400" />
                  ) : (
                    <EyeIcon className="h-5 w-5 text-gray-400" />
                  )}
                </button>
              </div>
            </div>
          </div>

          <div>
            <button
              type="submit"
              disabled={loading}
              className="group relative w-full btn-primary py-2 px-4"
            >
              {loading ? 'Signing in...' : 'Sign in'}
            </button>
          </div>
          
          {/* Additional Help Section */}
          <div className="text-center">
            <p className="text-sm text-gray-600">
              Forgot your password?{' '}
              <button 
                onClick={() => setShowContactInfo(true)}
                className="font-medium text-primary-600 hover:text-primary-500"
              >
                Contact IT Support
              </button>
            </p>
          </div>
        </form>
      </div>
    </div>
  );
};

export default Login;
