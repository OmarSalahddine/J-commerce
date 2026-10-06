import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ApiError } from '../components/ApiError'

export function OrderDetailPage() {
  const { id } = useParams()
  const { auth } = useAuth()
  const [order, setOrder] = useState(null)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!auth) {
      return
    }
    let cancelled = false
    api(`/api/orders/${id}`, { auth })
      .then((data) => {
        if (!cancelled) {
          setOrder(data)
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
  }, [id, auth])

  async function onCancel() {
    try {
      const updated = await api(`/api/orders/${id}/cancel`, { method: 'POST', auth })
      setOrder(updated)
      setError(null)
    } catch (err) {
      setError(err)
    }
  }

  return (
    <section>
      <h1>Order {id}</h1>
      {!auth && <p>Log in to view this order.</p>}
      <ApiError error={error} />
      {auth && !order && !error && <p>Loading…</p>}
      {order && (
        <>
          <p>
            {order.customerEmail} · {order.status} · total {order.totalAmount}
          </p>
          <table>
            <thead>
              <tr>
                <th>SKU</th>
                <th>Name</th>
                <th>Unit price</th>
                <th>Qty</th>
                <th>Line total</th>
              </tr>
            </thead>
            <tbody>
              {order.lines.map((line, index) => (
                <tr key={`${line.productId}-${index}`}>
                  <td>
                    <Link to={`/products/${line.productId}`}>{line.productSku}</Link>
                  </td>
                  <td>{line.productName}</td>
                  <td>{line.unitPrice}</td>
                  <td>{line.quantity}</td>
                  <td>{line.lineTotal}</td>
                </tr>
              ))}
            </tbody>
          </table>
          {order.status === 'PENDING' && (
            <button type="button" onClick={onCancel}>
              Cancel order
            </button>
          )}
        </>
      )}
    </section>
  )
}
