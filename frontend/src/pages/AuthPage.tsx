import { useMutation } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { post, type AuthResponse } from '../api'
import { useSession } from '../session'

export function AuthPage({ mode }: { mode: 'login' | 'register' }) {
  const isRegister = mode === 'register'
  const navigate = useNavigate()
  const { signIn } = useSession()
  const [form, setForm] = useState({ familyName: '', name: '', email: '', password: '' })
  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [k]: e.target.value })

  const mutation = useMutation({
    mutationFn: () =>
      post<AuthResponse>(
        isRegister ? '/auth/register' : '/auth/login',
        isRegister ? form : { email: form.email, password: form.password },
      ),
    onSuccess: (res) => {
      signIn(res)
      navigate('/')
    },
  })

  const submit = (e: FormEvent) => {
    e.preventDefault()
    mutation.mutate()
  }

  return (
    <form onSubmit={submit} style={{ maxWidth: 320, margin: '4rem auto', display: 'grid', gap: 8 }}>
      <h1>{isRegister ? 'Create your family' : 'Sign in'}</h1>
      {isRegister && (
        <>
          <input placeholder="Family name" value={form.familyName} onChange={set('familyName')} required />
          <input placeholder="Your name" value={form.name} onChange={set('name')} required />
        </>
      )}
      <input type="email" placeholder="Email" value={form.email} onChange={set('email')} required />
      <input
        type="password"
        placeholder="Password (min 8 chars)"
        value={form.password}
        onChange={set('password')}
        minLength={isRegister ? 8 : undefined}
        required
      />
      {mutation.error && <p role="alert">{mutation.error.message}</p>}
      <button disabled={mutation.isPending}>{isRegister ? 'Create family' : 'Sign in'}</button>
      <Link to={isRegister ? '/login' : '/register'}>
        {isRegister ? 'Already have an account? Sign in' : 'New here? Create a family'}
      </Link>
    </form>
  )
}
