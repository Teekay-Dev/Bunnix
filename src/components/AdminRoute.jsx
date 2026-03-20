// src/components/AdminRoute.jsx
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const AdminRoute = ({ children }) => {
  const { currentUser, isAdmin } = useAuth();

  // 1. If user is not logged in, send them to Sign In
  if (!currentUser) {
    return <Navigate to="/signin" />;
  }

  // 2. If user is logged in but NOT an admin, send them to Splash
  if (!isAdmin) {
    return <Navigate to="/" />;
  }

  // 3. If logged in AND admin, show the page
  return children;
};

export default AdminRoute;