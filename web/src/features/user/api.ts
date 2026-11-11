import { api } from '@/lib/api';

import type { SessionRequest, SignUpRequest, UpdateUserRequest, User } from './types';

/**
 * 이름·휴대폰·이메일로 사람을 찾는다. 없으면 404 다 — 호출부가 가입으로 안내한다.
 * 인증이 아니다. 자세한 사정은 백엔드 SessionController 주석에 있다.
 */
export const startSession = (body: SessionRequest) => api.post<User>('/sessions', body);

export const signUp = (body: SignUpRequest) => api.post<User>('/users', body);

export const fetchUser = (id: number) => api.get<User>(`/users/${id}`);

export const updateUser = (id: number, body: UpdateUserRequest) =>
  api.patch<User>(`/users/${id}`, body);
