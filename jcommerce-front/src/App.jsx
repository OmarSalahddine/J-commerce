import { Route, Routes } from 'react-router-dom'
import { Layout } from './components/Layout'
import { OrderDetailPage } from './pages/OrderDetailPage'
import { OrderFormPage } from './pages/OrderFormPage'
import { ProductDetailPage } from './pages/ProductDetailPage'
import { ProductFormPage } from './pages/ProductFormPage'
import { ProductsPage } from './pages/ProductsPage'

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route index element={<ProductsPage />} />
        <Route path="products/new" element={<ProductFormPage />} />
        <Route path="products/:id" element={<ProductDetailPage />} />
        <Route path="orders/new" element={<OrderFormPage />} />
        <Route path="orders/:id" element={<OrderDetailPage />} />
      </Route>
    </Routes>
  )
}
