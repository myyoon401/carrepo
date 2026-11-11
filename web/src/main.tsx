import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router';

import { MePage } from '@/routes/me';
import { SignUpPage } from '@/routes/signup';

import './styles.css';

// 라우트가 열 개 남짓이라 파일 기반 라우팅(코드 생성)을 두지 않는다.
// 늘어나면 그때 옮긴다.
const router = createBrowserRouter([
  { path: '/', element: <Navigate to="/me" replace /> },
  { path: '/signup', element: <SignUpPage /> },
  { path: '/me', element: <MePage /> },
  { path: '*', element: <Navigate to="/me" replace /> },
]);

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, refetchOnWindowFocus: false } },
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  </StrictMode>,
);
