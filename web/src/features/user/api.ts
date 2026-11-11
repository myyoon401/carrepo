import { api } from '@/lib/api';

import type { Resume, SignUpRequest, UpdateUserRequest, User } from './types';

export const signUp = (body: SignUpRequest) => api.post<User>('/users', body);

export const fetchUser = (id: number) => api.get<User>(`/users/${id}`);

export const updateUser = (id: number, body: UpdateUserRequest) =>
  api.patch<User>(`/users/${id}`, body);

export const fetchResume = (id: number) => api.get<Resume>(`/users/${id}/resume`);
