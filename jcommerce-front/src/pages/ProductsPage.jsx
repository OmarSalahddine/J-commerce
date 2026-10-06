import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api/client'
import { ApiError } from '../components/ApiError'

export function ProductsPage() {
  const [page, setPage] = useState(null)
  const [category, setCategory] = useState('')
  const [error, setError] = useState(null)

  useEffect(() => {
    const params = new URLSearchParams({ size: '20', sort: 'name,asc' })
    if (category) {
      params.set('category', category)
    }
    let cancelled = false
    api(`/api/products?${params}`)
      .then((data) => {
        if (!cancelled) {
          setPage(data)
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
  }, [category])

  return (
    <section>
      <h1>Products</h1>
      <label>
        Category
        <input
          value={category}
          placeholder="peripherals"
          onChange={(event) => setCategory(event.target.value.trim())}
        />
      </label>
      <ApiError error={error} />
      {!page && !error && <p>Loading…</p>}
      {page && (
        <>
          <p>{page.totalElements} product(s)</p>
          <table>
            <thead>
              <tr>
                <th>SKU</th>
                <th>Name</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Category</th>
              </tr>
            </thead>
            <tbody>
              {page.content.map((product) => (
                <tr key={product.id}>
                  <td>{product.sku}</td>
                  <td>
                    <Link to={`/products/${product.id}`}>{product.name}</Link>
                  </td>
                  <td>{product.price}</td>
                  <td>{product.stock}</td>
                  <td>{product.category}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </section>
  )
}
