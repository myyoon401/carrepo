import { useMutation } from '@tanstack/react-query';
import { useState } from 'react';
import { Navigate, useNavigate } from 'react-router';

import { BrandMark } from '@/components/brand-mark';
import { Button, Field, TextInput } from '@/components/ui/field';
import { signUp, startSession } from '@/features/user/api';
import { CUSTOM_TITLE, RESUME_TITLE_PRESETS } from '@/features/user/resume-titles';
import { session } from '@/features/user/session';
import { ApiError } from '@/lib/api';
import { cn } from '@/lib/cn';

type Step = 'identify' | 'register';
type TitleChoice = (typeof RESUME_TITLE_PRESETS)[number] | typeof CUSTOM_TITLE;

/**
 * 홈. 이름·휴대폰·이메일을 받아 **있으면 들어가고 없으면 가입**으로 갈라진다.
 *
 * 조회에 실패했다고 바로 만들지 않는다 — 오타로 계정이 생기는 걸 막으려고
 * 이력서 타이틀을 고르는 단계를 한 번 거치게 한다.
 */
export function HomePage() {
  if (session.get() !== null) return <Navigate to="/me" replace />;
  return <HomeForm />;
}

function HomeForm() {
  const navigate = useNavigate();

  const [step, setStep] = useState<Step>('identify');
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [email, setEmail] = useState('');
  const [choice, setChoice] = useState<TitleChoice>(RESUME_TITLE_PRESETS[0]);
  const [customTitle, setCustomTitle] = useState('');

  const enter = (userId: number) => {
    session.set(userId);
    void navigate('/me');
  };

  const identify = useMutation({
    mutationFn: () => startSession({ name, phone, email }),
    onSuccess: (user) => enter(user.id),
    onError: (error) => {
      // 없는 사람이면 가입 단계로 넘긴다. 그 외 오류는 화면에 남긴다.
      if (error instanceof ApiError && error.status === 404) setStep('register');
    },
  });

  const register = useMutation({
    mutationFn: () =>
      signUp({ name, phone, email, title: choice === CUSTOM_TITLE ? customTitle : choice }),
    onSuccess: (user) => enter(user.id),
  });

  const pending = identify.isPending || register.isPending;
  const error =
    register.error instanceof ApiError
      ? register.error
      : identify.error instanceof ApiError && identify.error.status !== 404
        ? identify.error
        : null;
  const fieldError = (field: string) => error?.fieldErrors?.[field];

  return (
    <main className="mx-auto w-full max-w-lg px-5 py-10 sm:py-16">
      <header className="mb-8 flex flex-col items-center text-center">
        <BrandMark size={72} />
        <h1 className="mt-4 text-2xl font-extrabold tracking-tight">커리포</h1>
        <p className="mt-1.5 text-sm text-muted">
          공고부터 면접까지 쌓일수록 나를 더 잘 아는 커리어 저장소
        </p>
      </header>

      <form
        className="flex flex-col gap-5"
        onSubmit={(e) => {
          e.preventDefault();
          if (step === 'identify') identify.mutate();
          else register.mutate();
        }}
      >
        <fieldset disabled={step === 'register'} className="flex flex-col gap-5">
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
            hint={step === 'identify' ? '하이픈은 넣어도 되고 빼도 됩니다.' : undefined}
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
        </fieldset>

        {step === 'register' && (
          <TitlePicker
            choice={choice}
            customTitle={customTitle}
            error={fieldError('title')}
            onChoice={setChoice}
            onCustomTitle={setCustomTitle}
          />
        )}

        {error && !error.fieldErrors && (
          <p className="brutal bg-red-100 px-3 py-2.5 text-sm font-semibold text-red-700">
            {error.message}
          </p>
        )}

        <Button type="submit" disabled={pending} className="mt-2 py-3">
          {pending ? '처리 중…' : step === 'identify' ? '시작하기' : '가입하고 시작하기'}
        </Button>

        {step === 'register' && (
          <button
            type="button"
            className="text-sm font-medium text-muted underline-offset-4 hover:underline"
            onClick={() => {
              setStep('identify');
              identify.reset();
              register.reset();
            }}
          >
            입력한 정보를 다시 확인할게요
          </button>
        )}
      </form>
    </main>
  );
}

function TitlePicker({
  choice,
  customTitle,
  error,
  onChoice,
  onCustomTitle,
}: {
  choice: TitleChoice;
  customTitle: string;
  error?: string;
  onChoice: (value: TitleChoice) => void;
  onCustomTitle: (value: string) => void;
}) {
  return (
    <fieldset className="brutal flex flex-col gap-1.5 bg-brand-soft p-4">
      <legend className="sr-only">이력서 타이틀</legend>
      <p className="text-sm">
        처음 오셨네요. <strong className="font-bold">이력서 타이틀</strong>만 고르면 바로 시작합니다.
      </p>

      <div className="mt-2 flex flex-col gap-2">
        {[...RESUME_TITLE_PRESETS, CUSTOM_TITLE].map((option) => (
          <label
            key={option}
            className={cn(
              'flex cursor-pointer items-center gap-3 rounded-lg border-2 border-ink bg-white px-3 py-3 text-sm font-medium',
              'transition-[transform,box-shadow] duration-75',
              choice === option
                ? 'translate-x-[3px] translate-y-[3px] font-bold shadow-none'
                : 'shadow-[3px_3px_0_0_var(--color-ink)]',
            )}
          >
            <input
              type="radio"
              name="title"
              value={option}
              checked={choice === option}
              onChange={() => onChoice(option as TitleChoice)}
              className="size-4 accent-brand"
            />
            <span className={option === CUSTOM_TITLE ? 'text-muted' : ''}>{option}</span>
          </label>
        ))}
      </div>

      {choice === CUSTOM_TITLE && (
        <TextInput
          className="mt-2"
          value={customTitle}
          onChange={(e) => onCustomTitle(e.target.value)}
          placeholder="예) Data Engineer (Spark / Airflow)"
          maxLength={100}
          required
          autoFocus
        />
      )}
      {error && <p className="mt-1 text-xs text-red-600">{error}</p>}
    </fieldset>
  );
}
