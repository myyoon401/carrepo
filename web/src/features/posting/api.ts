import { api } from '@/lib/api';

import type {
  CreatePostingRequest,
  ExtractResult,
  PostingDetail,
  PostingSummary,
} from './types';

/** 주소에서 본문만 뽑는다. 저장하지 않는다 — 결과를 보고 고친 뒤 저장한다. */
export const extractPosting = (url: string) =>
  api.post<ExtractResult>('/postings/extract', { url });

export const createPosting = (userId: number, body: CreatePostingRequest) =>
  api.post<PostingDetail>(`/users/${userId}/postings`, body);

export const fetchPostings = (userId: number) =>
  api.get<PostingSummary[]>(`/users/${userId}/postings`);

export const fetchPosting = (userId: number, id: number) =>
  api.get<PostingDetail>(`/users/${userId}/postings/${id}`);

/** 지금 화면이 고치는 것은 본문뿐이다. 다른 필드 수정 화면이 생기면 그때 요청을 늘린다. */
export const updatePostingContent = (userId: number, id: number, rawMd: string) =>
  api.patch<PostingDetail>(`/users/${userId}/postings/${id}`, { rawMd });

export const deletePosting = (userId: number, id: number) =>
  api.del<void>(`/users/${userId}/postings/${id}`);
