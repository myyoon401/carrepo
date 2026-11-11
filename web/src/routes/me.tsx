import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useEffect, useRef, useState } from 'react';
import { Navigate } from 'react-router';

import { Button, Field, TextInput } from '@/components/ui/field';
import { updateUser } from '@/features/user/api';
import { userKeys, userQueries } from '@/features/user/queries';
import { CUSTOM_TITLE, RESUME_TITLE_PRESETS } from '@/features/user/resume-titles';
import { session } from '@/features/user/session';
import { ApiError } from '@/lib/api';
import { cn } from '@/lib/cn';

export function MePage() {
  const userId = session.get();
  if (userId === null) return <Navigate to="/signup" replace />;
  return <MeContent userId={userId} />;
}

function MeContent({ userId }: { userId: number }) {
  const queryClient = useQueryClient();
  const user = useQuery(userQueries.detail(userId));
  const resume = useQuery(userQueries.resume(userId));

  const [draft, setDraft] = useState<{ name: string; phone: string; email: string; title: string } | null>(null);

  // 서버 값이 도착하면 폼 초안을 한 번 채운다. 이후 편집 중에는 덮어쓰지 않는다.
  useEffect(() => {
    if (user.data && draft === null) {
      setDraft({
        name: user.data.name,
        phone: user.data.phone,
        email: user.data.email,
        title: user.data.title,
      });
    }
  }, [user.data, draft]);

  const mutation = useMutation({
    mutationFn: () => updateUser(userId, draft!),
    onSuccess: async () => {
      // 이력서는 새 버전이 생기므로 둘 다 다시 받는다.
      await queryClient.invalidateQueries({ queryKey: userKeys.detail(userId) });
      await queryClient.invalidateQueries({ queryKey: userKeys.resume(userId) });
    },
  });

  if (user.isPending || !draft) {
    return <p className="p-8 text-sm text-slate-500">불러오는 중…</p>;
  }
  if (user.isError) {
    return (
      <div className="flex flex-col items-start gap-3 p-8">
        <p className="text-sm text-red-700">
          {user.error instanceof ApiError ? user.error.message : '사용자를 불러오지 못했습니다.'}
        </p>
        <Button
          variant="ghost"
          onClick={() => {
            session.clear();
            location.href = '/signup';
          }}
        >
          다시 시작하기
        </Button>
      </div>
    );
  }

  const error = mutation.error instanceof ApiError ? mutation.error : null;
  const isPreset = RESUME_TITLE_PRESETS.includes(draft.title as (typeof RESUME_TITLE_PRESETS)[number]);

  return (
    <main className="mx-auto w-full max-w-6xl px-5 py-8 sm:py-12">
      <header className="mb-8">
        <h1 className="text-2xl font-bold tracking-tight">내 이력서</h1>
        <p className="mt-2 text-sm text-slate-600">
          정보를 고치면 이력서 새 버전이 만들어집니다. 이전 버전은 그대로 남습니다.
        </p>
      </header>

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
              className="w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-base focus:border-slate-900 focus:outline-none"
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
            <p className="rounded-lg bg-red-50 px-3 py-2.5 text-sm text-red-700">{error.message}</p>
          )}
          {mutation.isSuccess && !mutation.isPending && (
            <p className="rounded-lg bg-emerald-50 px-3 py-2.5 text-sm text-emerald-700">
              저장했습니다. 새 버전이 만들어졌습니다.
            </p>
          )}

          <Button type="submit" disabled={mutation.isPending}>
            {mutation.isPending ? '저장 중…' : '저장'}
          </Button>
        </form>

        <ResumePreview userId={userId} seq={resume.data?.seq} />
      </div>
    </main>
  );
}

function ResumePreview({ userId, seq }: { userId: number; seq?: number }) {
  const frameRef = useRef<HTMLIFrameElement>(null);

  // seq 가 바뀌면 같은 주소라도 새로 받아야 한다.
  const src = `/api/users/${userId}/resume/raw${seq ? `?v=${seq}` : ''}`;

  return (
    <section className="flex min-w-0 flex-col gap-3">
      <div className="flex flex-wrap items-center justify-between gap-2">
        <h2 className="text-sm font-semibold text-slate-700">
          미리보기 {seq ? <span className="font-normal text-slate-500">· v{seq}</span> : null}
        </h2>
        <div className="flex gap-2">
          <Button variant="ghost" onClick={() => frameRef.current?.contentWindow?.print()}>
            인쇄 · PDF 저장
          </Button>
          <a
            href={src}
            download="resume.html"
            className={cn(
              'inline-flex items-center justify-center rounded-lg border border-slate-300 bg-white',
              'px-4 py-2.5 text-sm font-semibold text-slate-700 transition hover:bg-slate-50',
            )}
          >
            HTML 내려받기
          </a>
        </div>
      </div>

      {/*
        sandbox 로 띄우므로 문서 안의 스크립트가 실행되지 않는다.
        이력서 본문을 직접 편집하게 되어도 XSS 가 구조적으로 막힌다.
        allow-modals 는 인쇄 대화상자 때문에 필요하다.
      */}
      <iframe
        ref={frameRef}
        key={src}
        src={src}
        title="이력서 미리보기"
        sandbox="allow-same-origin allow-modals"
        className="h-[60vh] w-full rounded-xl border border-slate-200 bg-white shadow-sm lg:h-[calc(100vh-14rem)]"
      />
    </section>
  );
}
