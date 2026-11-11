import type { ReactNode } from 'react';

import { cn } from '@/lib/cn';

type FieldProps = {
  label: string;
  htmlFor?: string;
  error?: string;
  hint?: string;
  children: ReactNode;
};

export function Field({ label, htmlFor, error, hint, children }: FieldProps) {
  return (
    <div className="flex flex-col gap-2">
      <label htmlFor={htmlFor} className="text-sm font-bold">
        {label}
      </label>
      {children}
      {hint && !error && <p className="text-xs text-muted">{hint}</p>}
      {error && <p className="text-xs font-semibold text-red-600">{error}</p>}
    </div>
  );
}

export function TextInput({ className, ...props }: React.InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      {...props}
      className={cn(
        'brutal brutal-focus w-full bg-white px-3 py-2.5 text-base font-medium',
        'placeholder:font-normal placeholder:text-slate-400',
        'disabled:bg-slate-100 disabled:text-muted disabled:shadow-none',
        className,
      )}
    />
  );
}

export function Button({
  className,
  variant = 'primary',
  ...props
}: React.ButtonHTMLAttributes<HTMLButtonElement> & { variant?: 'primary' | 'ghost' }) {
  return (
    <button
      {...props}
      className={cn(
        'brutal brutal-press brutal-focus inline-flex items-center justify-center',
        'px-4 py-2.5 text-sm font-bold',
        'disabled:cursor-not-allowed disabled:opacity-50 disabled:shadow-none',
        variant === 'primary' ? 'bg-ink text-white' : 'bg-white text-ink',
        className,
      )}
    />
  );
}

/** 카드·패널의 공통 몸통. 안쪽 여백은 쓰는 쪽이 정한다. */
export function Panel({ className, ...props }: React.HTMLAttributes<HTMLDivElement>) {
  return <div {...props} className={cn('brutal bg-white', className)} />;
}

/** 작은 검정 알약 라벨. 상태·기간 표시에 쓴다. */
export function Chip({ className, ...props }: React.HTMLAttributes<HTMLSpanElement>) {
  return (
    <span
      {...props}
      className={cn(
        'inline-flex items-center gap-1 rounded-md bg-ink px-2 py-1 text-xs font-bold text-white',
        className,
      )}
    />
  );
}
