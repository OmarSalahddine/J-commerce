import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

// Browser calls localhost:5173/api/...; Vite forwards those to Spring Boot.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
