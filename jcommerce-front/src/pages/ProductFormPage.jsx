import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ApiError } from '../components/ApiError'

const empty = {
  sku: '',
  name: '',
  description: '',
  price: '',
  stock: '',
  category: '',
}

export function ProductFormPage() {
  const { auth } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState(empty)
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function onSubmit(event) {
    event.preventDefault()
    setSaving(true)
    setError(null)
    try {
      const created = await api('/api/products', {
        method: 'POST',
        auth,
        body: {
          sku: form.sku,
          name: form.name,
          description: form.description || null,
          price: Number(form.price),
          stock: Number(form.stock),
          category: form.category || null,
        },
      })
      navigate(`/products/${created.id}`)
    } catch (err) {
      setError(err)
    } finally {
      setSaving(false)
    }
  }

  return (
    <section>
      <h1>New product</h1>
      {!auth && <p>Log in first. Writes require HTTP Basic.</p>}
      <ApiError error={error} />
      <form onSubmit={onSubmit} className="stack">
        <label>
          SKU
          <input value={form.sku} onChange={(event) => update('sku', event.target.value)} required />
        </label>
        <label>
          Name
          <input value={form.name} onChange={(event) => update('name', event.target.value)} required />
        </label>
        <label>
          Description
          <input
            value={form.description}
            onChange={(event) => update('description', event.target.value)}
          />
        </label>
        <label>
          Price
          <input
            type="number"
            min="0"
            step="0.01"
            value={form.price}
            onChange={(event) => update('price', event.target.value)}
            required
          />
        </label>
        <label>
          Stock
          <input
            type="number"
            min="0"
            step="1"
            value={form.stock}
            onChange={(event) => update('stock', event.target.value)}
            required
          />
        </label>
        <label>
          Category
          <input
            value={form.category}
            onChange={(event) => update('category', event.target.value)}
          />
        </label>
        <button type="submit" disabled={saving}>
          {saving ? 'Saving…' : 'Create'}
        </button>
      </form>
    </section>
  )
}
