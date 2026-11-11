package com.carrepo.posting

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.time.LocalDate

/**
 * 공고 한 건.
 *
 * 본문(`rawMd`)은 **원문**이다. 서비스가 요약하거나 문장을 고치지 않는다 —
 * 나중에 분석 기준이 바뀌어도 원문이 남아 있어야 다시 볼 수 있다.
 *
 * 다만 **정리(공백·빈 줄·NBSP)는 엔티티가 책임진다.** 생성이든 수정이든 이 클래스를 거치므로
 * "저장된 본문은 늘 정리된 상태"가 관례가 아니라 불변조건이 된다.
 * 나머지 필드는 불변조건이 없어 변경 메서드를 두지 않는다 — 쓰이지 않는 변경 경로를 미리 만들지 않는다.
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "posting")
class Posting(
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: Long,

    @Column(name = "company_id", nullable = false, updatable = false)
    val companyId: Long,

    @Column(name = "title", length = 200, nullable = false)
    val title: String,

    rawMd: String,

    @Column(name = "source_url", length = 2048)
    val sourceUrl: String? = null,

    @Column(name = "deadline_at")
    val deadlineAt: LocalDate? = null,
) {
    // allOpen 때문에 공개 프로퍼티에는 private setter 를 둘 수 없다.
    // 비공개 필드 + 읽기 전용 프로퍼티로 변경 경로를 updateContent 하나로 좁힌다.
    @Column(name = "raw_md", nullable = false)
    private var content: String = MarkdownCleaner.clean(rawMd)

    val rawMd: String
        get() = content

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant

    fun updateContent(rawMd: String) {
        content = MarkdownCleaner.clean(rawMd)
    }
}
