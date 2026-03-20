import { RouterProvider } from 'react-router';
import { router } from './app/routes';
import { AuthProvider } from './contexts/AuthContext'; // 1. Import the Provider

export default function App() {
  return (
    // 2. Wrap the RouterProvider
    <AuthProvider> 
      <RouterProvider router={router} />
    </AuthProvider>
  );
}