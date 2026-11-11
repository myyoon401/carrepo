import { useQuery } from '@tanstack/react-query';
import { useEffect, type ReactNode } from 'react';
import { Link, Navigate } from 'react-router';

import { BrandMark } from '@/components/brand-mark';
import { Chip, Panel } from '@/components/ui/field';
import { resumeQueries } from '@/features/document';
import { postingQueries } from '@/features/posting/queries';
import { userQueries } from '@/features/user/queries';
import { session } from '@/features/user/session';
import { ApiError } from '@/lib/api';

/** 로그인 뒤 첫 화면. 지금은 두 갈래뿐이라 메뉴가 곧 전부다. */
export function MenuPage() {
  const userId = session.get();
  if (userId === null) return <Navigate to="/" replace />;
  return <Menu userId={userId} />;
}

function Menu({ userId }: { userId: number }) {
  const user = useQuery(userQueries.detail(userId));
  const resume = useQuery(resumeQueries.byUser(userId));
  const postings = useQuery(postingQueries.list(userId));

  // 지운 사용자의 id 가 브라우저에 남아 있을 수 있다. 그때만 세션을 비운다.
  // 네트워크 끊김이나 서버 오류로 로그아웃시키면 안 된다 — 다시 시도하면 되는 상황이다.
  const staleSession = user.error instanceof ApiError && user.error.status === 404;

  // render 는 순수해야 한다. 저장소를 비우는 것은 effect 에서.
  useEffect(() => {
    if (staleSession) session.clear();
  }, [staleSession]);

  if (staleSession) return <Navigate to="/" replace />;

  if (user.isError) {
    return (
      <main className="mx-auto w-full max-w-lg px-5 py-16 text-center">
        <p className="text-sm font-semibold text-red-700">
          {user.error instanceof ApiError ? user.error.message : '불러오지 못했습니다.'}
        </p>
        <button
          type="button"
          className="mt-4 text-sm font-medium text-muted underline-offset-4 hover:underline"
          onClick={() => void user.refetch()}
        >
          다시 시도
        </button>
      </main>
    );
  }

  return (
    <main className="mx-auto w-full max-w-3xl px-5 py-10 sm:py-16">
      <header className="mb-10 flex flex-col items-center text-center">
        <BrandMark size={72} />
        <h1 className="mt-4 text-2xl font-extrabold tracking-tight">
          {user.data ? `${user.data.name}님의 커리포` : '커리포'}
        </h1>
        <p className="mt-1.5 text-sm text-muted">
          공고부터 면접까지 쌓일수록 나를 더 잘 아는 커리어 저장소
        </p>
      </header>

      <nav className="grid gap-4 sm:grid-cols-2">
        <MenuCard
          to="/me"
          label="이력서 관리"
          description="이력서를 보고 고칩니다. 고칠 때마다 버전이 쌓입니다."
          badge={resume.data ? <Chip>v{resume.data.seq}</Chip> : null}
        />
        <MenuCard
          to="/postings"
          label="관심 공고 등록"
          description="주소를 넣어 가져오거나 본문을 붙여넣어 보관합니다."
          badge={postings.data ? <Chip>{postings.data.length}건</Chip> : null}
        />
      </nav>

      <button
        type="button"
        className="mx-auto mt-10 block text-sm font-medium text-muted underline-offset-4 hover:underline"
        onClick={() => {
          session.clear();
          location.href = '/';
        }}
      >
        다른 정보로 시작하기
      </button>
    </main>
  );
}

function MenuCard({
  to,
  label,
  description,
  badge,
}: {
  to: string;
  label: string;
  description: string;
  badge: ReactNode;
}) {
  return (
    <Link to={to} className="block">
      <Panel className="brutal-press flex h-full flex-col gap-2 p-5">
        <div className="flex items-start justify-between gap-2">
          <h2 className="text-lg font-extrabold">{label}</h2>
          {badge}
        </div>
        <p className="text-sm text-muted">{description}</p>
      </Panel>
    </Link>
  );
}
