import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Navigate } from 'react-router';

import { AppShell } from '@/components/app-shell';
import { Button, Field, TextInput } from '@/components/ui/field';
import { ResumePreview, resumeKeys, resumeQueries } from '@/features/document';
import { updateUser } from '@/features/user/api';
import { userKeys, userQueries } from '@/features/user/queries';
import { CUSTOM_TITLE, RESUME_TITLE_PRESETS } from '@/features/user/resume-titles';
import { session } from '@/features/user/session';
import type { User } from '@/features/user/types';
import { ApiError } from '@/lib/api';
import { cn } from '@/lib/cn';

export function MePage() {
  const userId = session.get();
  if (userId === null) return <Navigate to="/" replace />;
  return <MeContent userId={userId} />;
}

/**
 * 불러오기만 맡는다.
 *
 * **오류를 로딩보다 먼저 본다.** 반대로 두면 조회가 실패했을 때 편집 초안이 영원히 비어 있어
 * 로딩 화면에서 빠져나오지 못한다.
 */
function MeContent({ userId }: { userId: number }) {
  const user = useQuery(userQueries.detail(userId));
  const resume = useQuery(resumeQueries.byUser(userId));

  if (user.isError) {
    const notFound = user.error instanceof ApiError && user.error.status === 404;
    return (
      <AppShell title="이력서 관리">
        <p className="text-sm font-semibold text-red-700">
          {user.error instanceof ApiError ? user.error.message : '사용자를 불러오지 못했습니다.'}
        </p>
        <div className="mt-4 flex gap-2">
          <Button variant="ghost" onClick={() => void user.refetch()}>
            다시 시도
          </Button>
          {notFound && (
            <Button
              variant="ghost"
              onClick={() => {
                session.clear();
                location.href = '/';
              }}
            >
              다시 시작하기
            </Button>
          )}
        </div>
      </AppShell>
    );
  }

  if (user.isPending) {
    return (
      <AppShell title="이력서 관리">
        <p className="text-sm text-muted">불러오는 중…</p>
      </AppShell>
    );
  }

  // 데이터가 온 뒤에만 편집기를 그린다. 덕분에 초안을 useState 초기값으로 둘 수 있고
  // useEffect 로 나중에 채우거나 non-null 단언을 쓸 일이 없다.
  return <MeEditor userId={userId} user={user.data} resumeSeq={resume.data?.seq} />;
}

type Draft = { name: string; phone: string; email: string; title: string };

function MeEditor({
  userId,
  user,
  resumeSeq,
}: {
  userId: number;
  user: User;
  resumeSeq?: number;
}) {
  const queryClient = useQueryClient();
  const [draft, setDraft] = useState<Draft>({
    name: user.name,
    phone: user.phone,
    email: user.email,
    title: user.title,
  });

  const mutation = useMutation({
    mutationFn: () => updateUser(userId, draft),
    onSuccess: async () => {
      // 정보를 고치면 이력서 새 버전이 생기므로 둘 다 다시 받는다.
      await queryClient.invalidateQueries({ queryKey: userKeys.detail(userId) });
      await queryClient.invalidateQueries({ queryKey: resumeKeys.byUser(userId) });
    },
  });

  const error = mutation.error instanceof ApiError ? mutation.error : null;
  const isPreset = RESUME_TITLE_PRESETS.includes(
    draft.title as (typeof RESUME_TITLE_PRESETS)[number],
  );

  return (
    <AppShell
      title="이력서 관리"
      description="정보를 고치면 이력서 새 버전이 만들어집니다. 이전 버전은 그대로 남습니다."
    >
      {/* 좁은 폭에서는 세로로 쌓이고, 넓으면 좌우 2단이 된다. 기능은 양쪽이 같다. */}
      <div className="grid gap-8 lg:grid-cols-[minmax(0,22rem)_minmax(0,1fr)]">
        <form
          className="flex flex-col gap-5"
          onSubmit={(e) => {
            e.preventDefault();
            mutation.mutate();
          }}
        >
          <Field label="이름" htmlFor="name" error={error?.fieldErrors?.name}>
            <TextInput
              id="name"
              value={draft.name}
              onChange={(e) => setDraft({ ...draft, name: e.target.value })}
            />
          </Field>

          <Field label="휴대폰번호" htmlFor="phone" error={error?.fieldErrors?.phone}>
            <TextInput
              id="phone"
              value={draft.phone}
              inputMode="tel"
              onChange={(e) => setDraft({ ...draft, phone: e.target.value })}
            />
          </Field>

          <Field label="이메일" htmlFor="email" error={error?.fieldErrors?.email}>
            <TextInput
              id="email"
              type="email"
              value={draft.email}
              onChange={(e) => setDraft({ ...draft, email: e.target.value })}
            />
          </Field>

          <Field label="이력서 타이틀" htmlFor="title" error={error?.fieldErrors?.title}>
            <select
              id="title"
              value={isPreset ? draft.title : CUSTOM_TITLE}
              onChange={(e) =>
                setDraft({ ...draft, title: e.target.value === CUSTOM_TITLE ? '' : e.target.value })
              }
              className={cn('brutal brutal-focus w-full bg-white px-3 py-2.5 text-base font-medium')}
            >
              {RESUME_TITLE_PRESETS.map((option) => (
                <option key={option} value={option}>
                  {option}
                </option>
              ))}
              <option value={CUSTOM_TITLE}>{CUSTOM_TITLE}</option>
            </select>
            {!isPreset && (
              <TextInput
                className="mt-2"
                value={draft.title}
                maxLength={100}
                placeholder="예) Data Engineer (Spark / Airflow)"
                onChange={(e) => setDraft({ ...draft, title: e.target.value })}
              />
            )}
          </Field>

          {error && !error.fieldErrors && (
            <p className="brutal bg-red-100 px-3 py-2.5 text-sm font-semibold text-red-700">
              {error.message}
            </p>
          )}
          {mutation.isSuccess && !mutation.isPending && (
            <p className="brutal bg-mint px-3 py-2.5 text-sm font-bold">
              저장했습니다. 새 버전이 만들어졌습니다.
            </p>
          )}

          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? '저장 중…' : '저장'}
          </Button>
        </form>

        <ResumePreview userId={userId} seq={resumeSeq} />
      </div>
    </AppShell>
  );
}
