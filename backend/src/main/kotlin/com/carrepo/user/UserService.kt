package com.carrepo.user

import com.carrepo.common.exception.CarrepoErrorCode
import com.carrepo.common.exception.CarrepoException
import com.carrepo.document.ResumeHeaderData
import com.carrepo.document.ResumeService
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val resumeService: ResumeService,
) {

    /** 가입과 이력서 생성은 한 트랜잭션이다 — 이력서 없는 사용자가 남지 않는다. */
    @Transactional
    fun signUp(request: SignUpRequest): User {
        val name = request.name.trim()
        val phone = PhoneNumber.normalize(request.phone)
        val email = request.email.trim()

        if (userRepository.existsByNameAndPhoneAndEmail(name, phone, email)) {
            throw CarrepoException(CarrepoErrorCode.DUPLICATE_USER)
        }

        // 사전 조회와 저장 사이에 같은 사람이 또 들어올 수 있다. 제약 위반을 여기서만 해석한다 —
        // 전역 핸들러가 모든 무결성 오류를 "중복 사용자" 로 번역하면 FK 오류까지 잘못 나간다.
        // saveAndFlush 로 제약 검사를 이 블록 안에서 끝낸다.
        val user = try {
            userRepository.saveAndFlush(
                User(name = name, phone = phone, email = email, title = request.title.trim()),
            )
        } catch (e: DataIntegrityViolationException) {
            throw CarrepoException(CarrepoErrorCode.DUPLICATE_USER)
        }
        resumeService.createFor(requireNotNull(user.id), user.toResumeHeader())
        return user
    }

    /** 이름·휴대폰·이메일로 사람을 찾는다. 없으면 호출부가 가입으로 안내한다. */
    @Transactional(readOnly = true)
    fun identify(request: SessionRequest): User =
        userRepository.findByNameAndPhoneAndEmail(
            request.name.trim(),
            PhoneNumber.normalize(request.phone),
            request.email.trim(),
        ) ?: throw CarrepoException(CarrepoErrorCode.USER_NOT_FOUND)

    @Transactional(readOnly = true)
    fun get(id: Long): User =
        userRepository.findById(id).orElseThrow { CarrepoException(CarrepoErrorCode.USER_NOT_FOUND) }

    /**
     * 머리말에 들어가는 값이 바뀌면 이력서 새 버전을 함께 만든다.
     * 바뀐 게 없으면 버전을 늘리지 않는다 — 같은 내용의 버전이 쌓이면 이력이 쓸모없어진다.
     */
    @Transactional
    fun update(id: Long, request: UpdateUserRequest): User {
        val user = userRepository.findById(id)
            .orElseThrow { CarrepoException(CarrepoErrorCode.USER_NOT_FOUND) }

        val name = request.name?.trim()
        val phone = request.phone?.let { PhoneNumber.normalize(it) }
        val email = request.email?.trim()
        val title = request.title?.trim()

        val nextName = name ?: user.name
        val nextPhone = phone ?: user.phone
        val nextEmail = email ?: user.email
        val identityChanged = nextName != user.name || nextPhone != user.phone || nextEmail != user.email
        if (identityChanged && userRepository.existsByNameAndPhoneAndEmail(nextName, nextPhone, nextEmail)) {
            throw CarrepoException(CarrepoErrorCode.DUPLICATE_USER)
        }

        if (user.update(name, phone, email, title)) {
            resumeService.appendVersionFor(
                userId = requireNotNull(user.id),
                header = user.toResumeHeader(),
                changeNote = "내 정보 수정",
            )
        }
        return user
    }

    // document 는 user 엔티티를 모른다. 넘길 값만 추려서 건넨다.
    // 전화번호 표기 규칙은 이 쪽 책임이라 여기서 포맷한다.
    private fun User.toResumeHeader() = ResumeHeaderData(
        name = name,
        phone = PhoneNumber.format(phone),
        email = email,
        title = title,
    )
}
