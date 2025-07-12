import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// https://vitejs.dev/config/
export default defineConfig({
    plugins: [react()],
    server: {
        proxy: {
            '/simulate': 'http://localhost:8080',
            '/simulations': 'http://localhost:8080',
            '/metrics': 'http://localhost:8080',
        }
    }
});
