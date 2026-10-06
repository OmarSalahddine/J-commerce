import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ApiError } from '../components/ApiError'

export function OrderFormPage() {
  const { auth } = useAuth()
  const navigate = useNavigate()
  const [products, setProducts] = useState([])
  const [email, setEmail] = useState('omar@example.com')
  const [lines, setLines] = useState([{ productId: '', quantity: '1' }])
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    api('/api/products?size=100')
      .then((page) => {
        setProducts(page.content)
        setLines((current) =>
          current.map((line, index) =>
            index === 0 && !line.productId && page.content[0]
              ? { ...line, productId: String(page.content[0].id) }
              : line,
          ),
        )
      })
      .catch(setError)
  }, [])

  function updateLine(index, field, value) {
    setLines((current) =>
      current.map((line, i) => (i === index ? { ...line, [field]: value } : line)),
    )
  }

  async function onSubmit(event) {
    event.preventDefault()
    setSaving(true)
    setError(null)
    try {
      const created = await api('/api/orders', {
        method: 'POST',
        auth,
        body: {
          customerEmail: email,
          lines: lines.map((line) => ({
            productId: Number(line.productId),
            quantity: Number(line.quantity),
          })),
        },
      })
      navigate(`/orders/${created.id}`)
    } catch (err) {
      setError(err)
    } finally {
      setSaving(false)
    }
  }

  return (
    <section>
      <h1>New order</h1>
      {!auth && <p>Log in first. Placing an order requires HTTP Basic.</p>}
      <ApiError error={error} />
      <form onSubmit={onSubmit} className="stack">
        <label>
          Customer email
          <input
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
          />
        </label>
        {lines.map((line, index) => (
          <div className="row" key={index}>
            <label>
              Product
              <select
                value={line.productId}
                onChange={(event) => updateLine(index, 'productId', event.target.value)}
              >
                {products.map((product) => (
                  <option key={product.id} value={product.id}>
                    {product.sku} — {product.name} (${product.price}, stock {product.stock})
                  </option>
                ))}
              </select>
            </label>
            <label>
              Qty
              <input
                type="number"
                min="1"
                step="1"
                value={line.quantity}
                onChange={(event) => updateLine(index, 'quantity', event.target.value)}
              />
            </label>
            <button
              type="button"
              onClick={() => setLines((current) => current.filter((_, i) => i !== index))}
              disabled={lines.length === 1}
            >
              Remove
            </button>
          </div>
        ))}
        <div className="row">
          <button
            type="button"
            onClick={() =>
              setLines((current) => [
                ...current,
                { productId: products[0] ? String(products[0].id) : '', quantity: '1' },
              ])
            }
          >
            Add line
          </button>
          <button type="submit" disabled={saving || products.length === 0}>
            {saving ? 'Placing…' : 'Place order'}
          </button>
        </div>
      </form>
    </section>
  )
}
