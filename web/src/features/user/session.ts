const KEY = 'carrepo.userId';

/**
 * V1 은 로컬 전용이라 인증이 없다. 가입할 때 받은 id 를 브라우저에 두고 그 사람으로 본다.
 * 외부에 올릴 때 이 파일을 실제 인증으로 바꾼다.
 */
export const session = {
  get(): number | null {
    const raw = localStorage.getItem(KEY);
    if (!raw) return null;
    const id = Number(raw);
    return Number.isFinite(id) ? id : null;
  },
  set(id: number) {
    localStorage.setItem(KEY, String(id));
  },
  clear() {
    localStorage.removeItem(KEY);
  },
};
