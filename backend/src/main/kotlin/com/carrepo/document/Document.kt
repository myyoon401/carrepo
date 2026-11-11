package com.carrepo.document

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

// 논리 문서 한 건. 본문은 갖지 않고, 현재 버전을 가리키기만 한다.
// 연관관계 대신 FK 값을 직접 들고 있다 — 지연로딩 함정을 피하고 쓰기 경로를 단순하게 둔다.
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name = "document")
class Document(
    @Column(name = "user_id", nullable = false, updatable = false)
    val userId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "kind", length = 30, nullable = false, updatable = false)
    val kind: DocumentKind,

    @Column(name = "name", length = 100, nullable = false)
    var name: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

    @Column(name = "head_version_id")
    var headVersionId: Long? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant

    fun moveHeadTo(versionId: Long) {
        headVersionId = versionId
    }
}
