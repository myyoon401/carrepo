import { api } from '@/lib/api';

import type { Resume } from './types';

export const fetchResume = (userId: number) => api.get<Resume>(`/users/${userId}/resume`);

// iframe src · 내려받기 링크가 쓰는 주소. 브라우저가 직접 받아가므로 fetch 를 타지 않는다.
export const resumeRawUrl = (userId: number, seq?: number) =>
  `/api/users/${userId}/resume/raw${seq ? `?v=${seq}` : ''}`;
