import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { api } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ApiError } from '../components/ApiError'

export function ProductDetailPage() {
  const { id } = useParams()
  const { auth } = useAuth()
  const navigate = useNavigate()
  const [product, setProduct] = useState(null)
  const [form, setForm] = useState(null)
  const [error, setError] = useState(null)
  const [notice, setNotice] = useState(null)

  useEffect(() => {
    let cancelled = false
    api(`/api/products/${id}`)
      .then((data) => {
        if (!cancelled) {
          setProduct(data)
          setForm({
            name: data.name,
            description: data.description ?? '',
            price: String(data.price),
            stock: String(data.stock),
            category: data.category ?? '',
          })
          setError(null)
        }
      })
      .catch((err) => {
        if (!cancelled) {
          setError(err)
        }
      })
    return () => {
      cancelled = true
    }
  }, [id])

  function update(field, value) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function onSave(event) {
    event.preventDefault()
    setNotice(null)
    try {
      const updated = await api(`/api/products/${id}`, {
        method: 'PUT',
        auth,
        body: {
          name: form.name,
          description: form.description || null,
          price: Number(form.price),
          stock: Number(form.stock),
          category: form.category || null,
        },
      })
      setProduct(updated)
      setNotice('Saved')
      setError(null)
    } catch (err) {
      setError(err)
    }
  }

  async function onDelete() {
    setNotice(null)
    try {
      await api(`/api/products/${id}`, { method: 'DELETE', auth })
      navigate('/')
    } catch (err) {
      setError(err)
    }
  }

  if (!product || !form) {
    return (
      <section>
        <ApiError error={error} />
        {!error && <p>Loading…</p>}
      </section>
    )
  }

  return (
    <section>
      <h1>{product.name}</h1>
      <p>
        SKU {product.sku} · updated {product.updatedAt}
      </p>
      <ApiError error={error} />
      {notice && <p className="banner ok">{notice}</p>}
      <form onSubmit={onSave} className="stack">
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
        <div className="row">
          <button type="submit">Save</button>
          <button type="button" className="danger" onClick={onDelete}>
            Delete
          </button>
          <Link to="/orders/new">Order this</Link>
        </div>
      </form>
    </section>
  )
}
