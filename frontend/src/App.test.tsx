import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { cleanup, render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { afterEach, describe, expect, it } from 'vitest'
import { App } from './App'
import { tokenStore } from './api'

afterEach(() => {
  cleanup()
  localStorage.clear()
})

const renderAt = (path: string) =>
  render(
    <QueryClientProvider client={new QueryClient()}>
      <MemoryRouter initialEntries={[path]}>
        <App />
      </MemoryRouter>
    </QueryClientProvider>,
  )

describe('routing', () => {
  it('redirects anonymous users to the login page', () => {
    renderAt('/')
    expect(screen.getByRole('heading', { name: 'Sign in' })).toBeTruthy()
  })

  it('shows the register form', () => {
    renderAt('/register')
    expect(screen.getByPlaceholderText('Family name')).toBeTruthy()
  })

  it('stores and clears the token', () => {
    tokenStore.set('abc')
    expect(tokenStore.get()).toBe('abc')
    tokenStore.clear()
    expect(tokenStore.get()).toBeNull()
  })
})
