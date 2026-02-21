import './App.css'
import { RouterProvider } from 'react-router';
import { router } from '../src/app/routes';

export default function App() {
  return <RouterProvider router={router} />;
}


