import type { ReactNode } from 'react';
import { NavLink } from 'react-router';

import { BrandMark } from '@/components/brand-mark';
import { session } from '@/features/user/session';
import { cn } from '@/lib/cn';

const NAV = [
  { to: '/me', label: '이력서' },
  { to: '/postings', label: '공고' },
];

/** 로그인한 화면들의 공통 틀. 머리말과 이동 메뉴를 한 곳에 둔다. */
export function AppShell({
  title,
  description,
  actions,
  children,
}: {
  title: string;
  description?: string;
  actions?: ReactNode;
  children: ReactNode;
}) {
  return (
    <div className="mx-auto w-full max-w-6xl px-5 py-6 sm:py-10">
      <nav className="mb-8 flex flex-wrap items-center gap-3">
        <BrandMark size={36} className="shrink-0" />
        <div className="flex gap-2">
          {NAV.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  'rounded-lg border-2 border-ink px-3 py-1.5 text-sm font-bold transition-[transform,box-shadow] duration-75',
                  isActive
                    ? 'translate-x-[3px] translate-y-[3px] bg-ink text-white'
                    : 'bg-white shadow-[3px_3px_0_0_var(--color-ink)]',
                )
              }
            >
              {item.label}
            </NavLink>
          ))}
        </div>
        <button
          type="button"
          className="ml-auto text-sm font-medium text-muted underline-offset-4 hover:underline"
          onClick={() => {
            session.clear();
            location.href = '/';
          }}
        >
          다른 정보로 시작하기
        </button>
      </nav>

      <header className="mb-6 flex flex-wrap items-start justify-between gap-3">
        <div>
          <h1 className="text-2xl font-extrabold tracking-tight">{title}</h1>
          {description && <p className="mt-1.5 text-sm text-muted">{description}</p>}
        </div>
        {actions}
      </header>

      {children}
    </div>
  );
}
