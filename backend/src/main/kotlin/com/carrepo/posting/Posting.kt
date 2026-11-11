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
 * `rawMd` 는 원문이다. 추출 결과든 붙여넣은 글이든 서비스가 요약하거나 재구성하지 않는다 —
 * 나중에 분석 기준이 바뀌어도 원문이 남아 있어야 다시 볼 수 있다.
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "posting")
class Posting(
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: Long,

    @Column(name = "company_id", nullable = false)
    var companyId: Long,

    @Column(name = "title", length = 200, nullable = false)
    var title: String,

    @Column(name = "raw_md", nullable = false)
    var rawMd: String,

    @Column(name = "source_url", length = 2048)
    var sourceUrl: String? = null,

    @Column(name = "deadline_at")
    var deadlineAt: LocalDate? = null,
) {
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
}
