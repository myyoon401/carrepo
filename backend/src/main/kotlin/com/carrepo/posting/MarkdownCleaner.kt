package com.carrepo.posting

/**
 * 공고 본문을 보기 좋은 마크다운으로 다듬는다.
 *
 * 내용을 바꾸지 않는다 — 요약하거나 문장을 고치지 않고, 공백과 빈 줄만 정리한다.
 * 변환기나 브라우저 복사가 남기는 잡음(非파괴 공백, 연속 빈 줄, 줄 끝 공백)을 없애는 정도다.
 */
object MarkdownCleaner {

    fun clean(raw: String): String =
        raw
            .replace(NBSP, ' ') // 복사하면 자주 딸려오는 non-breaking space
            .replace("\r\n", "\n")
            .replace("\r", "\n")
            .lineSequence()
            .map { it.trimEnd() }
            .joinToString("\n")
            .replace(BLANK_LINES, "\n\n") // 빈 줄은 최대 한 줄까지만
            .trim()

    /** 눈에 보이지 않는 문자는 코드포인트로 적는다. 편집기·포매터를 거치며 조용히 바뀔 수 있다. */
    private const val NBSP = '\u00A0'

    private val BLANK_LINES = Regex("\n{3,}")
}
