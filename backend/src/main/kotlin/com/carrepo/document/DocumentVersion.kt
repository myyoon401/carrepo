package com.carrepo.document

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

// 문서의 한 버전. **불변이다** — 수정은 새 seq 를 insert 한다.
// 모든 컬럼이 updatable = false 라 실수로 바꿔도 UPDATE 문이 나가지 않는다.
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    name = "document_version",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_document_version_seq", columnNames = ["document_id", "seq"]),
    ],
)
class DocumentVersion(
    @Column(name = "document_id", nullable = false, updatable = false)
    val documentId: Long,

    @Column(name = "seq", nullable = false, updatable = false)
    val seq: Int,

    @Column(name = "html", nullable = false, updatable = false)
    val html: String,

    @Column(name = "change_note", length = 255, updatable = false)
    val changeNote: String? = null,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant
}
