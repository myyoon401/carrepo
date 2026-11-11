import { api } from '@/lib/api';

import type { SignUpRequest, UpdateUserRequest, User } from './types';

export const signUp = (body: SignUpRequest) => api.post<User>('/users', body);

export const fetchUser = (id: number) => api.get<User>(`/users/${id}`);

export const updateUser = (id: number, body: UpdateUserRequest) =>
  api.patch<User>(`/users/${id}`, body);
