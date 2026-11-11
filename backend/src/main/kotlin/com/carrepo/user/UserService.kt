package com.carrepo.user

import com.carrepo.common.exception.CarrepoErrorCode
import com.carrepo.common.exception.CarrepoException
import com.carrepo.document.ResumeService
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

        val user = userRepository.save(
            User(name = name, phone = phone, email = email, title = request.title.trim()),
        )
        resumeService.createFor(user)
        return user
    }

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
            resumeService.appendVersionFor(user, changeNote = "내 정보 수정")
        }
        return user
    }
}
