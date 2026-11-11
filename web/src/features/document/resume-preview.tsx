import { useRef } from 'react';

import { Button } from '@/components/ui/field';
import { cn } from '@/lib/cn';

import { resumeRawUrl } from './api';

/**
 * 이력서 미리보기.
 *
 * sandbox 로 띄우므로 문서 안의 스크립트가 실행되지 않는다 —
 * 이력서 본문을 직접 편집하게 되어도 XSS 가 구조적으로 막힌다.
 * allow-modals 는 인쇄 대화상자 때문에 필요하다.
 */
export function ResumePreview({ userId, seq }: { userId: number; seq?: number }) {
  const frameRef = useRef<HTMLIFrameElement>(null);
  const src = resumeRawUrl(userId, seq);

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

      {/* seq 가 바뀌면 같은 주소라도 새로 받아야 하므로 key 로 다시 그린다 */}
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
