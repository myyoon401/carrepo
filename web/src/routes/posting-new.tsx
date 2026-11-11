import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router';

import { AppShell } from '@/components/app-shell';
import { Button, Field, Panel, TextInput } from '@/components/ui/field';
import { createPosting, extractPosting } from '@/features/posting/api';
import { postingKeys } from '@/features/posting/queries';
import { session } from '@/features/user/session';
import { ApiError } from '@/lib/api';

export function PostingNewPage() {
  const userId = session.get();
  if (userId === null) return <Navigate to="/" replace />;
  return <PostingForm userId={userId} />;
}

/**
 * 공고 등록.
 *
 * 주소를 넣으면 본문을 가져와 **채워 넣기만** 한다. 바로 저장하지 않는 이유는
 * 추출이 자주 어긋나기 때문이다 — 사이트 홍보 문구가 섞이거나 본문이 잘린다.
 * 가져오기에 실패해도 붙여넣기 칸은 늘 열려 있어서 기능이 멈추지 않는다.
 */
function PostingForm({ userId }: { userId: number }) {
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [sourceUrl, setSourceUrl] = useState('');
  const [companyName, setCompanyName] = useState('');
  const [title, setTitle] = useState('');
  const [deadlineAt, setDeadlineAt] = useState('');
  const [rawMd, setRawMd] = useState('');

  const extract = useMutation({
    mutationFn: () => extractPosting(sourceUrl),
    onSuccess: (result) => {
      setRawMd(result.markdown);
      if (!title && result.title) setTitle(result.title);
    },
  });

  const create = useMutation({
    mutationFn: () =>
      createPosting(userId, {
        companyName,
        title,
        rawMd,
        sourceUrl: sourceUrl.trim() || null,
        deadlineAt: deadlineAt || null,
      }),
    onSuccess: async (posting) => {
      await queryClient.invalidateQueries({ queryKey: postingKeys.list(userId) });
      void navigate(`/postings/${posting.id}`);
    },
  });

  const extractError = extract.error instanceof ApiError ? extract.error : null;
  const saveError = create.error instanceof ApiError ? create.error : null;

  return (
    <AppShell title="공고 추가" description="주소를 넣어 가져오거나, 본문을 직접 붙여넣으세요.">
      <form
        className="flex flex-col gap-6"
        onSubmit={(e) => {
          e.preventDefault();
          create.mutate();
        }}
      >
        <Panel className="flex flex-col gap-3 bg-brand-soft p-4">
          <Field
            label="공고 주소"
            htmlFor="sourceUrl"
            hint="비워 두고 본문만 붙여넣어도 됩니다."
          >
            <div className="flex flex-col gap-2 sm:flex-row">
              <TextInput
                id="sourceUrl"
                value={sourceUrl}
                onChange={(e) => setSourceUrl(e.target.value)}
                placeholder="https://…"
                inputMode="url"
              />
              <Button
                type="button"
                variant="ghost"
                className="shrink-0"
                disabled={!sourceUrl.trim() || extract.isPending}
                onClick={() => extract.mutate()}
              >
                {extract.isPending ? '가져오는 중…' : '본문 가져오기'}
              </Button>
            </div>
          </Field>

          {extractError && (
            <p className="brutal bg-amber-100 px-3 py-2.5 text-sm font-semibold">
              {extractError.message}
            </p>
          )}
          {extract.isSuccess && (
            <p className="brutal bg-mint px-3 py-2.5 text-sm font-bold">
              본문을 가져왔습니다. 아래에서 확인하고 고친 뒤 저장하세요.
            </p>
          )}
        </Panel>

        <div className="grid gap-5 sm:grid-cols-2">
          <Field label="회사" htmlFor="companyName" error={saveError?.fieldErrors?.companyName}>
            <TextInput
              id="companyName"
              value={companyName}
              onChange={(e) => setCompanyName(e.target.value)}
              placeholder="예) 에이피알"
              required
            />
          </Field>

          <Field label="마감일" htmlFor="deadlineAt" hint="모르면 비워 두세요.">
            <TextInput
              id="deadlineAt"
              type="date"
              value={deadlineAt}
              onChange={(e) => setDeadlineAt(e.target.value)}
            />
          </Field>
        </div>

        <Field label="포지션" htmlFor="title" error={saveError?.fieldErrors?.title}>
          <TextInput
            id="title"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            placeholder="예) 백엔드 개발자 (경력)"
            required
          />
        </Field>

        <Field
          label="공고 본문"
          htmlFor="rawMd"
          error={saveError?.fieldErrors?.rawMd}
          hint="원문 그대로 보관합니다. 서비스가 요약하거나 고치지 않습니다."
        >
          <textarea
            id="rawMd"
            value={rawMd}
            onChange={(e) => setRawMd(e.target.value)}
            placeholder="브라우저에서 공고 본문을 복사해 붙여넣으세요."
            required
            rows={18}
            className="brutal brutal-focus w-full resize-y bg-white px-3 py-2.5 font-mono text-sm leading-relaxed"
          />
        </Field>

        {saveError && !saveError.fieldErrors && (
          <p className="brutal bg-red-100 px-3 py-2.5 text-sm font-semibold text-red-700">
            {saveError.message}
          </p>
        )}

        <div className="flex gap-2">
          <Button type="submit" disabled={create.isPending}>
            {create.isPending ? '저장 중…' : '저장'}
          </Button>
          <Button type="button" variant="ghost" onClick={() => void navigate('/postings')}>
            취소
          </Button>
        </div>
      </form>
    </AppShell>
  );
}
