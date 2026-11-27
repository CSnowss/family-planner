import { useQuery, useQueryClient } from '@tanstack/react-query'
import { Navigate, Outlet } from 'react-router-dom'
import { ApiError, api, tokenStore, type AuthResponse, type Member } from './api'

export function useSession() {
  const qc = useQueryClient()
  const query = useQuery({
    queryKey: ['me'],
    queryFn: () => api<Member>('/me'),
    enabled: !!tokenStore.get(),
    retry: (count, err) => !(err instanceof ApiError && err.status === 401) && count < 2,
  })
  return {
    me: query.data,
    loading: query.isLoading,
    signIn(res: AuthResponse) {
      tokenStore.set(res.token)
      qc.setQueryData(['me'], res.member)
    },
    signOut() {
      tokenStore.clear()
      qc.clear()
    },
  }
}

export function RequireAuth() {
  const { me, loading } = useSession()
  if (!tokenStore.get()) return <Navigate to="/login" replace />
  if (loading) return <p>Loading…</p>
  if (!me) {
    tokenStore.clear()
    return <Navigate to="/login" replace />
  }
  return <Outlet />
}
