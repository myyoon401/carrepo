package com.carrepo.document

import com.carrepo.common.exception.CarrepoErrorCode
import com.carrepo.common.exception.CarrepoException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 이력서 문서와 그 버전을 관리한다.
 *
 * 버전 행은 불변이다. 머리말에 들어가는 값이 바뀌면 기존 행을 고치지 않고 다음 seq 를 insert 한 뒤
 * `document.head_version_id` 만 옮긴다. 바꿔본 시도가 전부 이력으로 남는다.
 */
@Service
class ResumeService(
    private val documentRepository: DocumentRepository,
    private val documentVersionRepository: DocumentVersionRepository,
    private val renderer: ResumeHtmlRenderer,
) {

    @Transactional
    fun createFor(userId: Long, header: ResumeHeaderData): Document {
        val document = documentRepository.save(
            Document(userId = userId, kind = DocumentKind.RESUME, name = "이력서"),
        )
        appendVersion(document, header, changeNote = "가입 시 자동 생성")
        return document
    }

    @Transactional
    fun appendVersionFor(userId: Long, header: ResumeHeaderData, changeNote: String): DocumentVersion {
        val document = findResume(userId)
        return appendVersion(document, header, changeNote)
    }

    @Transactional(readOnly = true)
    fun headVersionOf(userId: Long): DocumentVersion {
        val document = findResume(userId)
        val headId = document.headVersionId ?: throw CarrepoException(CarrepoErrorCode.RESUME_NOT_FOUND)
        return documentVersionRepository.findById(headId)
            .orElseThrow { CarrepoException(CarrepoErrorCode.RESUME_NOT_FOUND) }
    }

    private fun appendVersion(document: Document, header: ResumeHeaderData, changeNote: String): DocumentVersion {
        val documentId = requireNotNull(document.id)
        val nextSeq = (documentVersionRepository.findTopByDocumentIdOrderBySeqDesc(documentId)?.seq ?: 0) + 1

        val version = documentVersionRepository.save(
            DocumentVersion(
                documentId = documentId,
                seq = nextSeq,
                html = renderer.render(header),
                changeNote = changeNote,
            ),
        )
        document.moveHeadTo(requireNotNull(version.id))
        return version
    }

    private fun findResume(userId: Long): Document =
        documentRepository.findByUserIdAndKind(userId, DocumentKind.RESUME)
            ?: throw CarrepoException(CarrepoErrorCode.RESUME_NOT_FOUND)
}
