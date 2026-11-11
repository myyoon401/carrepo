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
    <div className="flex flex-col gap-1.5">
      <label htmlFor={htmlFor} className="text-sm font-medium text-slate-700">
        {label}
      </label>
      {children}
      {hint && !error && <p className="text-xs text-slate-500">{hint}</p>}
      {error && <p className="text-xs text-red-600">{error}</p>}
    </div>
  );
}

export function TextInput({
  className,
  ...props
}: React.InputHTMLAttributes<HTMLInputElement>) {
  return (
    <input
      {...props}
      className={cn(
        'w-full rounded-lg border border-slate-300 bg-white px-3 py-2.5 text-base',
        'placeholder:text-slate-400',
        'focus:border-slate-900 focus:ring-2 focus:ring-slate-900/10 focus:outline-none',
        'disabled:bg-slate-100 disabled:text-slate-500',
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
        'inline-flex items-center justify-center rounded-lg px-4 py-2.5 text-sm font-semibold transition',
        'disabled:cursor-not-allowed disabled:opacity-50',
        variant === 'primary'
          ? 'bg-slate-900 text-white hover:bg-slate-800'
          : 'border border-slate-300 bg-white text-slate-700 hover:bg-slate-50',
        className,
      )}
    />
  );
}
