/**
 * 이력서 타이틀 보기.
 *
 * 이름은 "이력서" 지만 user 가 갖는다 — 값이 users.title 컬럼에 있고,
 * 쓰는 화면도 가입·내 정보뿐이다. document 는 완성된 문자열만 넘겨받는다.
 *
 * 일부러 화면에만 둔다 — 서버는 title 을 자유 문자열로만 받는다.
 * 보기를 테이블로 빼면 하나 늘릴 때마다 마이그레이션이 필요해진다.
 * 보기가 많아지거나 사용자마다 달라지면 그때 API 로 옮기고 이 파일만 고치면 된다.
 */
export const RESUME_TITLE_PRESETS = [
  'Backend Engineer (Java / Spring)',
  'Frontend Engineer (React / Web)',
  'Mobile Engineer (AOS)',
  'Mobile Engineer (iOS)',
] as const;

export const CUSTOM_TITLE = '직접 입력' as const;
