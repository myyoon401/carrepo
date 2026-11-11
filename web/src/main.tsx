import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter, Navigate, RouterProvider } from 'react-router';

import { HomePage } from '@/routes/home';
import { MenuPage } from '@/routes/menu';
import { MePage } from '@/routes/me';
import { PostingDetailPage } from '@/routes/posting-detail';
import { PostingNewPage } from '@/routes/posting-new';
import { PostingListPage } from '@/routes/postings';

import './styles.css';

// 라우트가 열 개 남짓이라 파일 기반 라우팅(코드 생성)을 두지 않는다.
// 늘어나면 그때 옮긴다.
const router = createBrowserRouter([
  { path: '/', element: <HomePage /> },
  { path: '/menu', element: <MenuPage /> },
  { path: '/me', element: <MePage /> },
  { path: '/postings', element: <PostingListPage /> },
  { path: '/postings/new', element: <PostingNewPage /> },
  { path: '/postings/:id', element: <PostingDetailPage /> },
  { path: '*', element: <Navigate to="/" replace /> },
]);

const queryClient = new QueryClient({
  defaultOptions: { queries: { retry: 1, refetchOnWindowFocus: false } },
});

const rootElement = document.getElementById('root');
if (!rootElement) throw new Error('#root 요소를 찾지 못했습니다.');

createRoot(rootElement).render(
  <StrictMode>
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
    </QueryClientProvider>
  </StrictMode>,
);
