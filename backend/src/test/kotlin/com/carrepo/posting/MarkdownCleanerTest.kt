package com.carrepo.posting

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class MarkdownCleanerTest {

    @Test
    @DisplayName("빈 줄은 최대 한 줄까지만 남긴다")
    fun collapsesBlankLines() {
        assertEquals("가\n\n나", MarkdownCleaner.clean("가\n\n\n\n나"))
    }

    @Test
    @DisplayName("줄 끝 공백과 앞뒤 빈 줄을 없앤다")
    fun trimsEdges() {
        assertEquals("가\n나", MarkdownCleaner.clean("\n\n가   \n나\t\n\n"))
    }

    @Test
    @DisplayName("복사하면 딸려오는 non-breaking space 를 보통 공백으로 바꾼다")
    fun normalizesNonBreakingSpace() {
        assertEquals("경력 4년 이상", MarkdownCleaner.clean("경력 4년 이상"))
    }

    @Test
    @DisplayName("윈도 줄바꿈을 정리한다")
    fun normalizesCrlf() {
        assertEquals("가\n나", MarkdownCleaner.clean("가\r\n나"))
    }

    @Test
    @DisplayName("내용 자체는 바꾸지 않는다")
    fun keepsContent() {
        val raw = "# 백엔드 개발자\n\n- Kotlin / Spring\n- **4년 이상**"
        assertEquals(raw, MarkdownCleaner.clean(raw))
    }
}
