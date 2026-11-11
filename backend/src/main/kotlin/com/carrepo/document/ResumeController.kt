package com.carrepo.document

import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// 이력서 API 는 document 가 갖는다. 주소가 사용자 밑에 있을 뿐 user 의 기능이 아니다.
@RestController
@RequestMapping("/api/users/{userId}/resume")
class ResumeController(
    private val resumeService: ResumeService,
) {

    @GetMapping
    fun resume(@PathVariable userId: Long): ResumeResponse =
        ResumeResponse.of(resumeService.headVersionOf(userId))

    /**
     * 완결된 HTML 문서를 그대로 돌려준다. iframe 미리보기와 파일 저장이 같이 쓴다.
     * 미리보기는 sandbox iframe 에 띄우므로 스크립트가 실행되지 않는다.
     */
    @GetMapping("/raw", produces = [MediaType.TEXT_HTML_VALUE])
    fun raw(@PathVariable userId: Long): ResponseEntity<String> =
        ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
            .body(resumeService.headVersionOf(userId).html)
}
