package com.carrepo.document

import org.springframework.stereotype.Component

/**
 * 이력서 기본 HTML 을 만든다.
 *
 * 산출물은 **그 자체로 완결된 문서**다 — 파일로 저장해 열어도, 인쇄해도 그대로 나와야 하므로
 * 스타일을 외부 파일로 빼지 않고 `<style>` 에 담는다.
 * 지금은 머리말 블록만 그린다. 본문 섹션은 뒤 단계에서 붙인다.
 *
 * 템플릿 엔진을 두지 않은 이유: 블록 하나에 Thymeleaf 를 들이는 건 과하다.
 * 섹션이 여러 개로 늘어나면 그때 옮긴다.
 */
@Component
class ResumeHtmlRenderer {

    fun render(header: ResumeHeaderData): String {
        val name = escape(header.name)
        val email = escape(header.email)
        val phone = escape(header.phone)
        val title = header.title.trim()

        return """
            <!DOCTYPE html>
            <html lang="ko">
            <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>$name 이력서</title>
            <style>
              * { margin: 0; padding: 0; box-sizing: border-box;
                  -webkit-print-color-adjust: exact; print-color-adjust: exact; }
              @page { size: A4; margin: 14mm 15mm; }

              body {
                font-family: -apple-system, BlinkMacSystemFont, 'Helvetica Neue', Arial, sans-serif;
                font-size: 12.5px; line-height: 1.55; color: #1a1a1a; background: #fff;
                padding: 30px 50px; max-width: 800px; margin: 0 auto;
              }

              .header { margin-bottom: 20px; }
              .header h1 { font-size: 25px; font-weight: 700; letter-spacing: -0.5px; margin-bottom: 2px; }
              .header .subtitle { font-size: 13.5px; color: #444; margin-bottom: 8px; }
              .header .contact { font-size: 12.5px; color: #444; display: flex; gap: 18px; margin-top: 5px; }

              @media print { body { padding: 0; } }
              /* 좁은 폭에서 .contact 가 flex 라 전화·메일이 붙는다. 세로로 쌓는다. */
              @media (max-width: 480px) {
                body { padding: 20px 18px; }
                .header .contact { flex-direction: column; gap: 4px; }
              }
            </style>
            </head>
            <body>
            <div class="header">
              <h1>$name</h1><!--SUBTITLE-->
              <div class="contact">
                <span>&#128222; $phone</span>
                <span>&#9993; $email</span>
              </div>
            </div>
            </body>
            </html>
        """.trimIndent().replace(SUBTITLE_MARKER, subtitleLine(title))
    }

    /**
     * title 은 필수지만, 과거 데이터나 검증을 빠져나간 값이 들어와도 빈 줄이 남지 않게 한다.
     *
     * 자리표시자를 쓰는 이유: 여러 줄 값을 템플릿에 직접 끼우면 그 줄의 들여쓰기가
     * `trimIndent()` 의 공통 들여쓰기 계산에 끼어들어 문서 전체가 밀린다.
     * 들여쓰기를 걷어낸 뒤에 끼운다.
     */
    private fun subtitleLine(title: String): String =
        if (title.isEmpty()) "" else "\n  <div class=\"subtitle\">${escape(title)}</div>"

    private companion object {
        const val SUBTITLE_MARKER = "<!--SUBTITLE-->"
    }

    private fun escape(value: String): String = value
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
}
