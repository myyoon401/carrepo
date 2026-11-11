package com.carrepo.document

import java.time.Instant

data class ResumeResponse(
    val documentId: Long,
    val versionId: Long,
    val seq: Int,
    val html: String,
    val changeNote: String?,
    val createdAt: Instant,
) {
    companion object {
        fun of(version: DocumentVersion) = ResumeResponse(
            documentId = version.documentId,
            versionId = requireNotNull(version.id),
            seq = version.seq,
            html = version.html,
            changeNote = version.changeNote,
            createdAt = version.createdAt,
        )
    }
}
