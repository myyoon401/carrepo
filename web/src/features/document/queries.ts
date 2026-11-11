import { fetchResume } from './api';

// 캐시 키와 fetcher 는 늘 같이 바뀐다. 한 파일에 둔다.
export const resumeKeys = {
  all: ['resumes'] as const,
  byUser: (userId: number) => ['resumes', userId] as const,
};

export const resumeQueries = {
  byUser: (userId: number) => ({
    queryKey: resumeKeys.byUser(userId),
    queryFn: () => fetchResume(userId),
  }),
};
