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
  // fetch 는 네트워크 실패에 TypeError 를 던진다. 그대로 두면 화면마다 ApiError 와
  // TypeError 를 둘 다 다뤄야 하고 어디선가 반드시 빠뜨린다. 여기서 한 가지 타입으로 모은다.
  const response = await fetch(`/api${path}`, {
    ...init,
    headers: {
      ...(init?.body ? { 'Content-Type': 'application/json' } : {}),
      ...init?.headers,
    },
  }).catch(() => {
    throw new ApiError(0, 'NETWORK_ERROR', '서버에 연결하지 못했습니다. 잠시 후 다시 시도해 주세요.');
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
