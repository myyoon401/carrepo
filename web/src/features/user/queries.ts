import { fetchUser } from './api';

// 캐시 키와 fetcher 는 늘 같이 바뀐다. 한 파일에 둔다.
export const userKeys = {
  all: ['users'] as const,
  detail: (id: number) => ['users', id] as const,
};

export const userQueries = {
  detail: (id: number) => ({
    queryKey: userKeys.detail(id),
    queryFn: () => fetchUser(id),
  }),
};
