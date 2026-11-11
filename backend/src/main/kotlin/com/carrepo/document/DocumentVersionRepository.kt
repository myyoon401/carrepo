package com.carrepo.document

import org.springframework.data.jpa.repository.JpaRepository

interface DocumentVersionRepository : JpaRepository<DocumentVersion, Long> {

    fun findTopByDocumentIdOrderBySeqDesc(documentId: Long): DocumentVersion?
}
