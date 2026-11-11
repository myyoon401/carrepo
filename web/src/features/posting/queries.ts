import { fetchPosting, fetchPostings } from './api';

// 캐시 키와 fetcher 는 늘 같이 바뀐다. 한 파일에 둔다.
export const postingKeys = {
  all: ['postings'] as const,
  list: (userId: number) => ['postings', 'list', userId] as const,
  detail: (userId: number, id: number) => ['postings', userId, id] as const,
};

export const postingQueries = {
  list: (userId: number) => ({
    queryKey: postingKeys.list(userId),
    queryFn: () => fetchPostings(userId),
  }),
  detail: (userId: number, id: number) => ({
    queryKey: postingKeys.detail(userId, id),
    queryFn: () => fetchPosting(userId, id),
  }),
};
