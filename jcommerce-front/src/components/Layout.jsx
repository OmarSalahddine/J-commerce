import { useState } from 'react'
import { Link, Outlet } from 'react-router-dom'
import { useAuth } from '../auth/AuthContext'

export function Layout() {
  const { auth, login, logout } = useAuth()
  const [username, setUsername] = useState('admin')
  const [password, setPassword] = useState('admin')

  function onSubmit(event) {
    event.preventDefault()
    login(username, password)
  }

  return (
    <div className="app">
      <header>
        <Link to="/" className="brand">
          jcommerce
        </Link>
        <nav>
          <Link to="/">Products</Link>
          <Link to="/products/new">New product</Link>
          <Link to="/orders/new">New order</Link>
        </nav>
        {auth ? (
          <div className="session">
            <span>{auth.username}</span>
            <button type="button" onClick={logout}>
              Log out
            </button>
          </div>
        ) : (
          <form className="session" onSubmit={onSubmit}>
            <input
              aria-label="Username"
              value={username}
              onChange={(event) => setUsername(event.target.value)}
            />
            <input
              aria-label="Password"
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
            />
            <button type="submit">Log in</button>
          </form>
        )}
      </header>
      <main>
        <Outlet />
      </main>
    </div>
  )
}
