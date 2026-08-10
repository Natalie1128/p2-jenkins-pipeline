import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router'
import './style.css'
import Login from './components/Login.tsx'
import ManagerDashboard from './components/ManagerDashboard.tsx'
import EmployeeDashboard from './components/EmployeeDashboard.tsx'
import Register from './components/Register.tsx'

const router = createBrowserRouter([
  {
    path: '/',
    element: <Login />,
  },
  {
    path: '/manager',
    element: <ManagerDashboard />,
  },
  {
    path: '/employee',
    element: <EmployeeDashboard />,
  },
  {
    path: 'register',
    element: <Register />,
  },
  {
    path: '*',
    element: <Navigate to="/" replace />,
  }
])

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <RouterProvider router={router} />
  </StrictMode>,
)
