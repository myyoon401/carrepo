package com.carrepo.posting

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PostingController(
    private val postingService: PostingService,
) {

    /**
     * 주소에서 본문만 뽑는다. 저장하지 않는다.
     * 추출이 어긋나는 일이 잦아서, 사람이 결과를 보고 고친 뒤 저장하게 한다.
     */
    @PostMapping("/api/postings/extract")
    fun extract(@Valid @RequestBody request: ExtractRequest): ExtractResponse {
        val extracted = postingService.extract(request.url)
        return ExtractResponse(title = extracted.title, markdown = extracted.markdown)
    }

    @PostMapping("/api/users/{userId}/postings")
    fun create(
        @PathVariable userId: Long,
        @Valid @RequestBody request: CreatePostingRequest,
    ): ResponseEntity<PostingDetail> =
        ResponseEntity.status(201).body(postingService.create(userId, request))

    @GetMapping("/api/users/{userId}/postings")
    fun list(@PathVariable userId: Long): List<PostingSummary> = postingService.list(userId)

    @GetMapping("/api/postings/{id}")
    fun get(@PathVariable id: Long): PostingDetail = postingService.get(id)

    @PatchMapping("/api/postings/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdatePostingRequest,
    ): PostingDetail = postingService.update(id, request)

    @DeleteMapping("/api/postings/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        postingService.delete(id)
        return ResponseEntity.noContent().build()
    }
}
