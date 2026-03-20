import React from 'react'
import { ReactDOM } from 'react-dom/client'
import '@/styles/index.css'
import App from './App'
import { createRoot } from 'react-dom/client';
import { AuthProvider } from './contexts/AuthContext';

const root = createRoot(document.getElementById('root'));
root.render(
  <React.StrictMode>
    <AuthProvider> {/* Wrap App here */}
      <App />
    </AuthProvider>
  </React.StrictMode>
);
