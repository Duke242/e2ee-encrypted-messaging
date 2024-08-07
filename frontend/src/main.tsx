import React from 'react'
import ReactDOM from 'react-dom/client'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import App from './App'
import './index.css'
import ErrorPage from './pages/ErrorPage'
import Login from './pages/Login'
import Signup from './pages/SignUp'

const router = createBrowserRouter([{
  path: '/',
  element: <App/>,
  errorElement: <ErrorPage/>
}, {
  path: '/login',
  element: <Login/>
}, {
  path: '/signup',
  element: <Signup/>
}])

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <RouterProvider router={router}/>
  </React.StrictMode>,
)