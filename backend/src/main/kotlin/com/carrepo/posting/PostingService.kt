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
                rawMd = request.rawMd, // 정리는 엔티티가 한다
                sourceUrl = request.sourceUrl?.trim()?.takeIf { it.isNotEmpty() },
                deadlineAt = request.deadlineAt,
            ),
        )
        return PostingDetail.of(posting, company.name)
    }

    @Transactional(readOnly = true)
    fun list(userId: Long): List<PostingSummary> {
        val postings = postingRepository.findByUserIdOrderByCreatedAtDesc(userId)
        val names = companyNames(postings)
        return postings.map { PostingSummary.of(it, names.getValue(it.companyId)) }
    }

    @Transactional(readOnly = true)
    fun get(userId: Long, id: Long): PostingDetail {
        val posting = find(userId, id)
        return PostingDetail.of(posting, companyName(posting.companyId))
    }

    @Transactional
    fun updateContent(userId: Long, id: Long, request: UpdatePostingContentRequest): PostingDetail {
        val posting = find(userId, id)
        posting.updateContent(request.rawMd)

        // @LastModifiedDate 는 flush 시점에 채워진다. 먼저 내보내지 않으면 이전 시각이 응답된다.
        postingRepository.flush()
        return PostingDetail.of(posting, companyName(posting.companyId))
    }

    @Transactional
    fun delete(userId: Long, id: Long) = postingRepository.delete(find(userId, id))

    /**
     * 소유권을 조회의 기본 조건으로 둔다.
     *
     * 인증이 없는 지금은 접근 통제가 아니라 **데이터 불변조건**이다 — 호출자가 고른 사용자의
     * 자원만 다룬다는 뜻. 인증이 붙으면 `userId` 의 출처만 실제 세션 사용자로 바꾸면 된다.
     */
    private fun find(userId: Long, id: Long): Posting =
        postingRepository.findByIdAndUserId(id, userId)
            ?: throw CarrepoException(CarrepoErrorCode.POSTING_NOT_FOUND)

    // 같은 회사에 여러 번 지원하므로 회사는 이름으로 찾아 쓰고, 없을 때만 만든다.
    private fun findOrCreateCompany(name: String): Company {
        val trimmed = name.trim()
        return companyRepository.findByName(trimmed) ?: companyRepository.save(Company(trimmed))
    }

    // company_id 는 NOT NULL FK 다. 없다는 것은 정상 상황이 아니므로 가려서 응답하지 않는다.
    private fun companyName(companyId: Long): String =
        companyRepository.findById(companyId).map { it.name }
            .orElseThrow { IllegalStateException("공고가 가리키는 회사($companyId)가 없습니다.") }

    /** 목록에서 회사명을 한 번에 모아 온다 — 행마다 조회하면 N+1 이 된다. */
    private fun companyNames(postings: List<Posting>): Map<Long, String> {
        val ids = postings.map { it.companyId }.distinct()
        val names = companyRepository.findAllById(ids).associate { requireNotNull(it.id) to it.name }
        return names.withDefault { error("공고가 가리키는 회사($it)가 없습니다.") }
    }
}
