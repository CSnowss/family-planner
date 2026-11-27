import { Route, Routes } from 'react-router-dom'
import { AuthPage } from './pages/AuthPage'
import { Dashboard } from './pages/Dashboard'
import { RequireAuth } from './session'

export function App() {
  return (
    <Routes>
      <Route path="/login" element={<AuthPage mode="login" />} />
      <Route path="/register" element={<AuthPage mode="register" />} />
      <Route element={<RequireAuth />}>
        <Route path="/" element={<Dashboard />} />
      </Route>
    </Routes>
  )
}
