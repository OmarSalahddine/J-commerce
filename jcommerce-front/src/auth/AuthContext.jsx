import { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')

  const auth = username ? { username, password } : null

  function login(nextUsername, nextPassword) {
    setUsername(nextUsername)
    setPassword(nextPassword)
  }

  function logout() {
    setUsername('')
    setPassword('')
  }

  return (
    <AuthContext.Provider value={{ auth, login, logout }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  return useContext(AuthContext)
}
