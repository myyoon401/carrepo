package com.carrepo.posting

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.time.LocalDate

data class ExtractRequest(
    @field:NotBlank(message = "공고 주소를 입력해 주세요.")
    @field:Size(max = 2048, message = "주소가 너무 깁니다.")
    val url: String,
)

data class ExtractResponse(
    val title: String?,
    val markdown: String,
)

data class CreatePostingRequest(
    @field:NotBlank(message = "회사명을 입력해 주세요.")
    @field:Size(max = 100, message = "회사명은 100자를 넘을 수 없습니다.")
    val companyName: String,

    @field:NotBlank(message = "포지션을 입력해 주세요.")
    @field:Size(max = 200, message = "포지션은 200자를 넘을 수 없습니다.")
    val title: String,

    @field:NotBlank(message = "공고 본문이 비어 있습니다.")
    val rawMd: String,

    @field:Size(max = 2048, message = "주소가 너무 깁니다.")
    val sourceUrl: String? = null,

    val deadlineAt: LocalDate? = null,
)

/**
 * 지금 화면이 고치는 것은 본문뿐이다.
 *
 * 쓰이지 않는 수정 계약을 두지 않는다. 다른 필드까지 고치는 화면이 생기면
 * 그때 "누락" 과 "null" 을 구분할 수 있는 요청 모델을 따로 만든다 —
 * 현재의 `null = 변경 없음` 규칙으로는 마감일을 지울 수가 없다.
 */
data class UpdatePostingContentRequest(
    @field:NotBlank(message = "공고 본문이 비어 있습니다.")
    val rawMd: String,
)

/** 목록용. 본문은 빼고 보낸다 — 공고 하나가 수천 자라 목록에 실으면 금방 무거워진다. */
data class PostingSummary(
    val id: Long,
    val companyName: String,
    val title: String,
    val sourceUrl: String?,
    val deadlineAt: LocalDate?,
    val createdAt: Instant,
) {
    companion object {
        fun of(posting: Posting, companyName: String) = PostingSummary(
            id = requireNotNull(posting.id),
            companyName = companyName,
            title = posting.title,
            sourceUrl = posting.sourceUrl,
            deadlineAt = posting.deadlineAt,
            createdAt = posting.createdAt,
        )
    }
}

data class PostingDetail(
    val id: Long,
    val companyName: String,
    val title: String,
    val rawMd: String,
    val sourceUrl: String?,
    val deadlineAt: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun of(posting: Posting, companyName: String) = PostingDetail(
            id = requireNotNull(posting.id),
            companyName = companyName,
            title = posting.title,
            rawMd = posting.rawMd,
            sourceUrl = posting.sourceUrl,
            deadlineAt = posting.deadlineAt,
            createdAt = posting.createdAt,
            updatedAt = posting.updatedAt,
        )
    }
}
