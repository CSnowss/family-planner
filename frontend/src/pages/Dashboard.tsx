import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query'
import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { api, post, type Family, type Member, type Role } from '../api'
import { useSession } from '../session'

export function Dashboard() {
  const { me, signOut } = useSession()
  const navigate = useNavigate()
  const qc = useQueryClient()
  const { data: family } = useQuery({ queryKey: ['family'], queryFn: () => api<Family>('/family') })
  const [form, setForm] = useState({ name: '', email: '', password: '', role: 'CHILD' as Role, color: '#10b981' })

  const addMember = useMutation({
    mutationFn: () => post<Member>('/family/members', form),
    onSuccess: () => {
      setForm({ ...form, name: '', email: '', password: '' })
      qc.invalidateQueries({ queryKey: ['family'] })
    },
  })

  const submit = (e: FormEvent) => {
    e.preventDefault()
    addMember.mutate()
  }

  return (
    <main style={{ maxWidth: 640, margin: '2rem auto' }}>
      <header style={{ display: 'flex', justifyContent: 'space-between' }}>
        <h1>{family?.name ?? 'Family'}</h1>
        <button
          onClick={() => {
            signOut()
            navigate('/login')
          }}
        >
          Sign out ({me?.name})
        </button>
      </header>

      <h2>Members</h2>
      <ul>
        {family?.members.map((m) => (
          <li key={m.id}>
            <span style={{ background: m.color, display: 'inline-block', width: 12, height: 12, borderRadius: 6 }} />{' '}
            {m.name} — {m.role.toLowerCase()}
          </li>
        ))}
      </ul>

      {me?.role === 'PARENT' && (
        <form onSubmit={submit} style={{ display: 'grid', gap: 8 }}>
          <h2>Add a member</h2>
          <input placeholder="Name" value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} required />
          <input type="email" placeholder="Email" value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} required />
          <input type="password" placeholder="Password (min 8 chars)" minLength={8} value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} required />
          <select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value as Role })}>
            <option value="CHILD">Child</option>
            <option value="PARENT">Parent</option>
          </select>
          <input type="color" value={form.color} onChange={(e) => setForm({ ...form, color: e.target.value })} />
          {addMember.error && <p role="alert">{addMember.error.message}</p>}
          <button disabled={addMember.isPending}>Add member</button>
        </form>
      )}
    </main>
  )
}
