package com.carrepo.posting

import com.carrepo.common.exception.CarrepoErrorCode
import com.carrepo.common.exception.CarrepoException
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import net.dankito.readability4j.Readability4J
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.net.URI

data class ExtractedPosting(
    val title: String?,
    val markdown: String,
)

/**
 * 공고 주소에서 본문을 추출해 마크다운으로 바꾼다.
 *
 * **대부분의 채용 플랫폼은 화면을 스크립트로 그린다.** 그런 페이지를 그냥 받아오면
 * 본문이 없는 껍데기 HTML 만 온다. 헤드리스 브라우저를 들이면 거의 다 긁히지만
 * Chromium 이 배포 산출물에 들어와 "jar 하나" 가 깨진다.
 *
 * 그래서 여기서는 **되는 곳만 되게 하고, 안 되면 깔끔하게 실패한다.**
 * 붙여넣기 경로가 늘 열려 있으므로 기능이 멈추지 않는다.
 */
@Component
class PostingExtractor {

    private val log = LoggerFactory.getLogger(javaClass)
    private val html2md = FlexmarkHtmlConverter.builder().build()

    fun extract(url: String): ExtractedPosting {
        val uri = parse(url)
        val html = fetch(uri)

        val article = runCatching { Readability4J(uri.toString(), html).parse() }
            .onFailure { log.debug("본문 추려내기 실패: {}", url, it) }
            .getOrNull()

        val contentHtml = article?.content
        val textLength = article?.textContent?.trim()?.length ?: 0

        // 스크립트로 그리는 페이지는 글자가 거의 없는 껍데기가 온다. 그걸 "성공" 으로 저장하면
        // 빈 공고가 쌓이므로 여기서 끊는다.
        if (contentHtml.isNullOrBlank() || textLength < MIN_TEXT_LENGTH) {
            log.info("본문이 너무 짧아 추출 실패로 본다 ({}자): {}", textLength, url)
            throw CarrepoException(CarrepoErrorCode.POSTING_EXTRACT_FAILED)
        }

        return ExtractedPosting(
            title = article.title?.trim()?.takeIf { it.isNotEmpty() },
            markdown = MarkdownCleaner.clean(html2md.convert(contentHtml)),
        )
    }

    private fun parse(url: String): URI {
        val uri = runCatching { URI(url.trim()) }.getOrNull()
            ?: throw CarrepoException(CarrepoErrorCode.POSTING_UNREACHABLE)

        // 서버가 임의 주소로 요청을 보내게 되는 자리다. 최소한 사람이 여는 주소로만 제한한다.
        if (uri.scheme?.lowercase() !in setOf("http", "https") || uri.host.isNullOrBlank()) {
            throw CarrepoException(CarrepoErrorCode.POSTING_UNREACHABLE)
        }
        return uri
    }

    private fun fetch(uri: URI): String =
        runCatching {
            Jsoup.connect(uri.toString())
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .followRedirects(true)
                .ignoreHttpErrors(false)
                .get()
                .html()
        }.getOrElse {
            log.info("공고 주소 접속 실패: {} ({})", uri, it.message)
            throw CarrepoException(CarrepoErrorCode.POSTING_UNREACHABLE)
        }

    private companion object {
        /** 이보다 짧으면 본문이 아니라 껍데기로 본다 */
        const val MIN_TEXT_LENGTH = 400
        const val TIMEOUT_MS = 10_000
        const val USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"
    }
}
