export class ApiError extends Error {
  constructor(
    readonly status: number,
    readonly code: string,
    message: string,
    readonly fieldErrors?: Record<string, string>,
  ) {
    super(message);
  }
}

type ErrorBody = { code?: string; message?: string; fieldErrors?: Record<string, string> };

/**
 * 백엔드 호출 한 곳.
 *
 * 주소를 상대 경로로만 쓴다 — dev 는 Vite 프록시가, 배포는 같은 jar 가 받는다.
 * 오류 응답은 `{ code, message, fieldErrors? }` 한 가지 형태라 여기서 ApiError 로 바꾼다.
 */
async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`/api${path}`, {
    ...init,
    headers: {
      ...(init?.body ? { 'Content-Type': 'application/json' } : {}),
      ...init?.headers,
    },
  });

  if (!response.ok) {
    const body: ErrorBody = await response.json().catch(() => ({}));
    throw new ApiError(
      response.status,
      body.code ?? 'UNKNOWN',
      body.message ?? '요청을 처리하지 못했습니다.',
      body.fieldErrors,
    );
  }

  if (response.status === 204) return undefined as T;
  return (await response.json()) as T;
}

export const api = {
  get: <T>(path: string) => request<T>(path),
  post: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'POST', body: JSON.stringify(body) }),
  patch: <T>(path: string, body: unknown) =>
    request<T>(path, { method: 'PATCH', body: JSON.stringify(body) }),
  del: <T>(path: string) => request<T>(path, { method: 'DELETE' }),
};
