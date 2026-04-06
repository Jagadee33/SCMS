import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ThemeProvider } from './contexts/ThemeContext';
import Layout from './layouts/Layout';
import Login from './pages/Login';
import RoleBasedDashboard from './components/RoleBasedDashboard';
import Students from './pages/Students';
import Faculty from './pages/Faculty';
import Courses from './pages/Courses';
import Departments from './pages/Departments';
import Attendance from './pages/Attendance';
import Grades from './pages/Grades';
import Examinations from './pages/Examinations';
import Library from './pages/Library';
import LibraryDashboard from './pages/LibraryDashboard';
import Fees from './pages/Fees';
import PaymentDashboard from './pages/PaymentDashboard';
import AIDashboard from './pages/AIDashboard';
import Profile from './pages/Profile';
import ProtectedRoute from './components/ProtectedRoute';

function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <Router>
          <div className="min-h-screen bg-gray-50">
            <Routes>
              <Route path="/login" element={<Login />} />
              <Route
                path="/*"
                element={
                  <ProtectedRoute>
                    <Layout>
                      <Routes>
                        <Route path="/" element={<Navigate to="/dashboard" replace />} />
                        <Route path="/dashboard" element={<RoleBasedDashboard />} />
                        <Route path="/students" element={<Students requiredRole="ADMIN" />} />
                        <Route path="/faculty" element={<Faculty requiredRole={["ADMIN", "FACULTY"]} />} />
                        <Route path="/courses" element={<Courses requiredRole={["ADMIN", "FACULTY", "STUDENT"]} />} />
                        <Route path="/departments" element={<Departments requiredRole="ADMIN" />} />
                        <Route path="/attendance" element={<Attendance requiredRole={["ADMIN", "FACULTY"]} />} />
                        <Route path="/grades" element={<Grades requiredRole={["ADMIN", "FACULTY"]} />} />
                        <Route path="/examinations" element={<Examinations requiredRole={["ADMIN", "FACULTY", "STUDENT"]} />} />
                        <Route path="/library" element={<Library />} />
                        <Route path="/payments" element={<PaymentDashboard />} />
                        <Route path="/ai-analytics" element={<AIDashboard />} />
                        <Route path="/fees" element={<Fees requiredRole={["ADMIN", "STUDENT"]} />} />
                        <Route path="/profile" element={<Profile />} />
                      </Routes>
                    </Layout>
                  </ProtectedRoute>
                }
              />
            </Routes>
          </div>
        </Router>
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;
