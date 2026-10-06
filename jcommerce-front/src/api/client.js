// Every screen goes through this. Components never call fetch themselves.
export async function api(path, { method = 'GET', body, auth } = {}) {
  const headers = {}
  if (body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }
  if (auth?.username) {
    headers.Authorization = `Basic ${btoa(`${auth.username}:${auth.password}`)}`
  }

  const response = await fetch(path, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  })

  if (response.status === 204) {
    return null
  }

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    const error = new Error(data?.detail || response.statusText)
    error.status = response.status
    error.errors = data?.errors ?? null
    throw error
  }

  return data
}
