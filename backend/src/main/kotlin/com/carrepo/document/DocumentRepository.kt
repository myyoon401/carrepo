package com.carrepo.document

import org.springframework.data.jpa.repository.JpaRepository

interface DocumentRepository : JpaRepository<Document, Long> {

    fun findByUserIdAndKind(userId: Long, kind: DocumentKind): Document?
}
