import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { useNavigate } from 'react-router';

import { Button, Field, TextInput } from '@/components/ui/field';
import { signUp } from '@/features/user/api';
import { CUSTOM_TITLE, RESUME_TITLE_PRESETS } from '@/features/user/resume-titles';
import { session } from '@/features/user/session';
import { ApiError } from '@/lib/api';
import { cn } from '@/lib/cn';

type TitleChoice = (typeof RESUME_TITLE_PRESETS)[number] | typeof CUSTOM_TITLE;

export function SignUpPage() {
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [choice, setChoice] = useState<TitleChoice>(RESUME_TITLE_PRESETS[0]);
  const [customTitle, setCustomTitle] = useState('');

  const title = choice === CUSTOM_TITLE ? customTitle : choice;

  const mutation = useMutation({
    mutationFn: () => signUp({ name, phone, email, title }),
    onSuccess: (user) => {
      session.set(user.id);
      void navigate('/me');
    },
  });

  const error = mutation.error instanceof ApiError ? mutation.error : null;
  const fieldError = (field: string) => error?.fieldErrors?.[field];

  return (
    <main className="mx-auto w-full max-w-lg px-5 py-10 sm:py-16">
      <header className="mb-8">
        <h1 className="text-2xl font-bold tracking-tight">커리포 시작하기</h1>
        <p className="mt-2 text-sm text-slate-600">
          이력서에 들어갈 기본 정보를 입력하면 이력서 머리말이 바로 만들어집니다.
        </p>
      </header>

      <form
        className="flex flex-col gap-5"
        onSubmit={(e) => {
          e.preventDefault();
          mutation.mutate();
        }}
      >
        <Field label="이름" htmlFor="name" error={fieldError('name')}>
          <TextInput
            id="name"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="홍길동"
            autoComplete="name"
            required
          />
        </Field>

        <Field
          label="휴대폰번호"
          htmlFor="phone"
          error={fieldError('phone')}
          hint="하이픈은 넣어도 되고 빼도 됩니다."
        >
          <TextInput
            id="phone"
            value={phone}
            onChange={(e) => setPhone(e.target.value)}
            placeholder="010-1234-5678"
            inputMode="tel"
            autoComplete="tel"
            required
          />
        </Field>

        <Field label="이메일" htmlFor="email" error={fieldError('email')}>
          <TextInput
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="name@example.com"
            autoComplete="email"
            required
          />
        </Field>

        <fieldset className="flex flex-col gap-1.5">
          <legend className="mb-1.5 text-sm font-medium text-slate-700">
            이력서 타이틀을 선택해주세요
          </legend>

          <div className="flex flex-col gap-2">
            {[...RESUME_TITLE_PRESETS, CUSTOM_TITLE].map((option) => (
              <label
                key={option}
                className={cn(
                  'flex cursor-pointer items-center gap-3 rounded-lg border px-3 py-3 text-sm transition',
                  choice === option
                    ? 'border-slate-900 bg-white ring-2 ring-slate-900/10'
                    : 'border-slate-300 bg-white hover:bg-slate-50',
                )}
              >
                <input
                  type="radio"
                  name="title"
                  value={option}
                  checked={choice === option}
                  onChange={() => setChoice(option as TitleChoice)}
                  className="size-4 accent-slate-900"
                />
                <span className={option === CUSTOM_TITLE ? 'text-slate-600' : ''}>{option}</span>
              </label>
            ))}
          </div>

          {choice === CUSTOM_TITLE && (
            <TextInput
              className="mt-1"
              value={customTitle}
              onChange={(e) => setCustomTitle(e.target.value)}
              placeholder="예) Data Engineer (Spark / Airflow)"
              maxLength={100}
              required
              autoFocus
            />
          )}
          {fieldError('title') && (
            <p className="text-xs text-red-600">{fieldError('title')}</p>
          )}
        </fieldset>

        {error && !error.fieldErrors && (
          <p className="rounded-lg bg-red-50 px-3 py-2.5 text-sm text-red-700">{error.message}</p>
        )}

        <Button type="submit" disabled={mutation.isPending} className="mt-2 py-3">
          {mutation.isPending ? '만드는 중…' : '시작하기'}
        </Button>
      </form>
    </main>
  );
}
