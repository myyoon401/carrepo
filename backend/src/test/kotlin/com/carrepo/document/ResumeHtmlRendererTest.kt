package com.carrepo.document

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class ResumeHtmlRendererTest {

    private val renderer = ResumeHtmlRenderer()

    private fun header(
        name: String = "홍길동",
        phone: String = "010-1234-5678",
        email: String = "hong@example.com",
        title: String = "Backend Engineer (Java / Spring)",
    ) = ResumeHeaderData(name = name, phone = phone, email = email, title = title)

    @Test
    @DisplayName("머리말에 이름·타이틀·연락처가 들어간다")
    fun rendersHeader() {
        val html = renderer.render(header())

        assertTrue(html.contains("<h1>홍길동</h1>"))
        assertTrue(html.contains("""<div class="subtitle">Backend Engineer (Java / Spring)</div>"""))
        assertTrue(html.contains("010-1234-5678"))
        assertTrue(html.contains("hong@example.com"))
    }

    @Test
    @DisplayName("그 자체로 열리는 완결된 문서다")
    fun rendersStandaloneDocument() {
        val html = renderer.render(header())

        assertTrue(html.startsWith("<!DOCTYPE html>"))
        assertTrue(html.contains("<style>"))
        assertTrue(html.contains("@page { size: A4;"))
        assertTrue(html.trimEnd().endsWith("</html>"))
    }

    @Test
    @DisplayName("타이틀이 비면 subtitle 블록 자체를 빼서 빈 줄이 남지 않는다")
    fun omitsEmptySubtitle() {
        val html = renderer.render(header(title = "   "))

        assertFalse(html.contains("class=\"subtitle\""))
        assertTrue(html.contains("<h1>홍길동</h1>"))
    }

    @Test
    @DisplayName("이름과 이메일은 이스케이프한다")
    fun escapesUserInput() {
        val html = renderer.render(header(name = "<script>alert(1)</script>"))

        assertFalse(html.contains("<script>alert(1)</script>"))
        assertTrue(html.contains("&lt;script&gt;"))
    }
}
