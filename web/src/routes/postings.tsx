import { useQuery } from '@tanstack/react-query';
import { Link, Navigate } from 'react-router';

import { AppShell } from '@/components/app-shell';
import { Button, Chip, Panel } from '@/components/ui/field';
import { postingQueries } from '@/features/posting/queries';
import type { PostingSummary } from '@/features/posting/types';
import { session } from '@/features/user/session';

export function PostingListPage() {
  const userId = session.get();
  if (userId === null) return <Navigate to="/" replace />;
  return <PostingList userId={userId} />;
}

function PostingList({ userId }: { userId: number }) {
  const postings = useQuery(postingQueries.list(userId));

  return (
    <AppShell
      title="공고"
      description="저장해 둔 공고입니다. 원문을 그대로 보관합니다."
      actions={
        <Link
          to="/postings/new"
          className="brutal brutal-press brutal-focus inline-flex items-center bg-ink px-4 py-2.5 text-sm font-bold text-white"
        >
          공고 추가
        </Link>
      }
    >
      {postings.isPending && <p className="text-sm text-muted">불러오는 중…</p>}

      {postings.data?.length === 0 && (
        <Panel className="p-8 text-center">
          <p className="text-sm text-muted">아직 저장한 공고가 없습니다.</p>
          <Link to="/postings/new" className="mt-3 inline-block">
            <Button>첫 공고 추가하기</Button>
          </Link>
        </Panel>
      )}

      {/* 넓으면 2단, 좁으면 1단. 같은 카드가 폭만 바꾼다 */}
      <ul className="grid gap-4 sm:grid-cols-2">
        {postings.data?.map((posting) => (
          <li key={posting.id}>
            <PostingCard posting={posting} />
          </li>
        ))}
      </ul>
    </AppShell>
  );
}

function PostingCard({ posting }: { posting: PostingSummary }) {
  const deadline = posting.deadlineAt ? daysLeft(posting.deadlineAt) : null;

  return (
    <Link to={`/postings/${posting.id}`} className="block">
      <Panel className="brutal-press h-full p-4">
        <div className="flex items-start justify-between gap-2">
          <p className="text-sm font-bold text-brand">{posting.companyName}</p>
          {deadline !== null && (
            <Chip className={deadline <= 3 ? 'bg-red-600' : undefined}>
              {deadline < 0 ? '마감' : `D-${deadline}`}
            </Chip>
          )}
        </div>
        <p className="mt-1 font-bold">{posting.title}</p>
        <p className="mt-3 text-xs text-muted">
          {new Date(posting.createdAt).toLocaleDateString('ko-KR')} 저장
          {posting.sourceUrl ? ' · 주소 있음' : ' · 붙여넣기'}
        </p>
      </Panel>
    </Link>
  );
}

function daysLeft(deadline: string): number {
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const target = new Date(`${deadline}T00:00:00`);
  return Math.round((target.getTime() - today.getTime()) / 86_400_000);
}
