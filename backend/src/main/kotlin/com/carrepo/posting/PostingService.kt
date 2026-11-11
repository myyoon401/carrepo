package com.carrepo.posting

import com.carrepo.common.exception.CarrepoErrorCode
import com.carrepo.common.exception.CarrepoException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PostingService(
    private val postingRepository: PostingRepository,
    private val companyRepository: CompanyRepository,
    private val extractor: PostingExtractor,
) {

    /** 주소에서 본문만 뽑아 돌려준다. 저장하지 않는다 — 추출이 자주 어긋나므로 사람이 보고 고친 뒤 저장한다. */
    fun extract(url: String): ExtractedPosting = extractor.extract(url)

    @Transactional
    fun create(userId: Long, request: CreatePostingRequest): PostingDetail {
        val company = findOrCreateCompany(request.companyName)
        val posting = postingRepository.save(
            Posting(
                userId = userId,
                companyId = requireNotNull(company.id),
                title = request.title.trim(),
                rawMd = MarkdownCleaner.clean(request.rawMd),
                sourceUrl = request.sourceUrl?.trim()?.takeIf { it.isNotEmpty() },
                deadlineAt = request.deadlineAt,
            ),
        )
        return detail(posting, company.name)
    }

    @Transactional(readOnly = true)
    fun list(userId: Long): List<PostingSummary> {
        val postings = postingRepository.findByUserIdOrderByCreatedAtDesc(userId)
        val names = companyNames(postings)
        return postings.map {
            PostingSummary(
                id = requireNotNull(it.id),
                companyName = names.getValue(it.companyId),
                title = it.title,
                sourceUrl = it.sourceUrl,
                deadlineAt = it.deadlineAt,
                createdAt = it.createdAt,
            )
        }
    }

    @Transactional(readOnly = true)
    fun get(id: Long): PostingDetail {
        val posting = find(id)
        return detail(posting, companyName(posting.companyId))
    }

    @Transactional
    fun update(id: Long, request: UpdatePostingRequest): PostingDetail {
        val posting = find(id)

        request.title?.trim()?.takeIf { it.isNotEmpty() }?.let { posting.title = it }
        request.rawMd?.takeIf { it.isNotBlank() }?.let { posting.rawMd = MarkdownCleaner.clean(it) }
        request.sourceUrl?.let { posting.sourceUrl = it.trim().takeIf { url -> url.isNotEmpty() } }
        request.deadlineAt?.let { posting.deadlineAt = it }

        val companyName = request.companyName?.trim()?.takeIf { it.isNotEmpty() }?.let {
            val company = findOrCreateCompany(it)
            posting.companyId = requireNotNull(company.id)
            company.name
        } ?: companyName(posting.companyId)

        return detail(posting, companyName)
    }

    @Transactional
    fun delete(id: Long) = postingRepository.delete(find(id))

    private fun find(id: Long): Posting =
        postingRepository.findById(id).orElseThrow { CarrepoException(CarrepoErrorCode.POSTING_NOT_FOUND) }

    // 같은 회사에 여러 번 지원하므로 회사는 이름으로 찾아 쓰고, 없을 때만 만든다.
    private fun findOrCreateCompany(name: String): Company {
        val trimmed = name.trim()
        return companyRepository.findByName(trimmed) ?: companyRepository.save(Company(trimmed))
    }

    private fun companyName(companyId: Long): String =
        companyRepository.findById(companyId).map { it.name }.orElse("(알 수 없음)")

    /** 목록에서 회사명을 한 번에 모아 온다 — 행마다 조회하면 N+1 이 된다. */
    private fun companyNames(postings: List<Posting>): Map<Long, String> {
        val ids = postings.map { it.companyId }.distinct()
        return companyRepository.findAllById(ids).associate { requireNotNull(it.id) to it.name }
            .withDefault { "(알 수 없음)" }
    }

    private fun detail(posting: Posting, companyName: String) = PostingDetail(
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
