package com.carrepo.user

import com.carrepo.document.ResumeService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
    private val resumeService: ResumeService,
) {

    @PostMapping
    fun signUp(@Valid @RequestBody request: SignUpRequest): ResponseEntity<UserResponse> {
        val user = userService.signUp(request)
        return ResponseEntity.status(201).body(UserResponse.of(user))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): UserResponse = UserResponse.of(userService.get(id))

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest,
    ): UserResponse = UserResponse.of(userService.update(id, request))

    @GetMapping("/{id}/resume")
    fun resume(@PathVariable id: Long): ResumeResponse {
        val version = resumeService.headVersionOf(id)
        return ResumeResponse(
            documentId = version.documentId,
            versionId = requireNotNull(version.id),
            seq = version.seq,
            html = version.html,
            changeNote = version.changeNote,
            createdAt = version.createdAt,
        )
    }

    /**
     * 완결된 HTML 문서를 그대로 돌려준다. iframe 미리보기와 파일 저장이 같이 쓴다.
     * 미리보기는 sandbox iframe 에 띄우므로 스크립트가 실행되지 않는다.
     */
    @GetMapping("/{id}/resume/raw", produces = [MediaType.TEXT_HTML_VALUE])
    fun resumeRaw(@PathVariable id: Long): ResponseEntity<String> =
        ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
            .body(resumeService.headVersionOf(id).html)
}
