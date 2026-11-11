import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Navigate, useNavigate, useParams } from 'react-router';

import { AppShell } from '@/components/app-shell';
import { Button, Chip, Panel } from '@/components/ui/field';
import { deletePosting, updatePosting } from '@/features/posting/api';
import { postingKeys, postingQueries } from '@/features/posting/queries';
import { session } from '@/features/user/session';
import { ApiError } from '@/lib/api';

export function PostingDetailPage() {
  const userId = session.get();
  const { id } = useParams();
  const postingId = Number(id);

  if (userId === null) return <Navigate to="/" replace />;
  if (!Number.isFinite(postingId)) return <Navigate to="/postings" replace />;
  return <PostingDetail userId={userId} postingId={postingId} />;
}

function PostingDetail({ userId, postingId }: { userId: number; postingId: number }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const posting = useQuery(postingQueries.detail(postingId));

  const [draft, setDraft] = useState<string | null>(null);

  const invalidate = async () => {
    await queryClient.invalidateQueries({ queryKey: postingKeys.detail(postingId) });
    await queryClient.invalidateQueries({ queryKey: postingKeys.list(userId) });
  };

  const save = useMutation({
    mutationFn: () => updatePosting(postingId, { rawMd: draft ?? '' }),
    onSuccess: async () => {
      await invalidate();
      setDraft(null);
    },
  });

  const remove = useMutation({
    mutationFn: () => deletePosting(postingId),
    onSuccess: async () => {
      await invalidate();
      void navigate('/postings');
    },
  });

  if (posting.isPending) return <AppShell title="공고">{<p className="text-sm text-muted">불러오는 중…</p>}</AppShell>;
  if (posting.isError || !posting.data) {
    const message = posting.error instanceof ApiError ? posting.error.message : '공고를 불러오지 못했습니다.';
    return (
      <AppShell title="공고">
        <p className="text-sm font-semibold text-red-700">{message}</p>
      </AppShell>
    );
  }

  const data = posting.data;
  const editing = draft !== null;

  return (
    <AppShell
      title={data.title}
      description={data.companyName}
      actions={
        <div className="flex gap-2">
          {editing ? (
            <>
              <Button onClick={() => save.mutate()} disabled={save.isPending}>
                {save.isPending ? '저장 중…' : '저장'}
              </Button>
              <Button variant="ghost" onClick={() => setDraft(null)}>
                취소
              </Button>
            </>
          ) : (
            <>
              <Button variant="ghost" onClick={() => setDraft(data.rawMd)}>
                본문 수정
              </Button>
              <Button
                variant="ghost"
                className="text-red-700"
                onClick={() => {
                  if (confirm('이 공고를 지울까요? 되돌릴 수 없습니다.')) remove.mutate();
                }}
              >
                삭제
              </Button>
            </>
          )}
        </div>
      }
    >
      <div className="mb-4 flex flex-wrap items-center gap-2 text-sm">
        {data.deadlineAt && <Chip>마감 {data.deadlineAt}</Chip>}
        {data.sourceUrl && (
          <a
            href={data.sourceUrl}
            target="_blank"
            rel="noreferrer"
            className="font-medium text-brand underline-offset-4 hover:underline"
          >
            원문 열기 ↗
          </a>
        )}
        <span className="text-muted">
          {new Date(data.createdAt).toLocaleDateString('ko-KR')} 저장
        </span>
      </div>

      {editing ? (
        <textarea
          value={draft}
          onChange={(e) => setDraft(e.target.value)}
          rows={30}
          className="brutal brutal-focus w-full resize-y bg-white px-4 py-3 font-mono text-sm leading-relaxed"
        />
      ) : (
        // 마크다운 렌더러를 아직 들이지 않는다 — 공고 본문은 대부분 평문이라
        // 줄바꿈만 살려도 읽힌다. 서식이 실제로 아쉬워지면 그때 붙인다.
        <Panel className="overflow-x-auto p-4 sm:p-6">
          <pre className="font-sans text-sm leading-relaxed whitespace-pre-wrap">{data.rawMd}</pre>
        </Panel>
      )}
    </AppShell>
  );
}
