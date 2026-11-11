import path from 'node:path';

import tailwindcss from '@tailwindcss/vite';
import react from '@vitejs/plugin-react';
import { defineConfig } from 'vite';

// 브라우저 코드는 API 주소를 모른다. 항상 같은 출처의 `/api/...` 상대 경로로 부른다.
// dev 는 여기서 백엔드로 넘기고, 배포는 같은 jar 가 둘 다 서빙하므로 프록시 자체가 없다.
export default defineConfig({
  plugins: [react(), tailwindcss()],
  resolve: {
    alias: { '@': path.resolve(import.meta.dirname, './src') },
  },
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      '^/api/': { target: 'http://localhost:8080', changeOrigin: true },
    },
  },
});
