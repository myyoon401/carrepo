import { api } from '@/lib/api';

import type {
  CreatePostingRequest,
  ExtractResult,
  PostingDetail,
  PostingSummary,
  UpdatePostingRequest,
} from './types';

/** 주소에서 본문만 뽑는다. 저장하지 않는다 — 결과를 보고 고친 뒤 저장한다. */
export const extractPosting = (url: string) =>
  api.post<ExtractResult>('/postings/extract', { url });

export const createPosting = (userId: number, body: CreatePostingRequest) =>
  api.post<PostingDetail>(`/users/${userId}/postings`, body);

export const fetchPostings = (userId: number) =>
  api.get<PostingSummary[]>(`/users/${userId}/postings`);

export const fetchPosting = (id: number) => api.get<PostingDetail>(`/postings/${id}`);

export const updatePosting = (id: number, body: UpdatePostingRequest) =>
  api.patch<PostingDetail>(`/postings/${id}`, body);

export const deletePosting = (id: number) => api.del<void>(`/postings/${id}`);
