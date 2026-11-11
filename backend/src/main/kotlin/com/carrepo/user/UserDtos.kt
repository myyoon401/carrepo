package com.carrepo.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant

data class SignUpRequest(
    @field:NotBlank(message = "이름을 입력해 주세요.")
    @field:Size(max = 50, message = "이름은 50자를 넘을 수 없습니다.")
    val name: String,

    @field:NotBlank(message = "휴대폰번호를 입력해 주세요.")
    @field:Pattern(regexp = "^[0-9-\\s]{10,20}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
    val phone: String,

    @field:NotBlank(message = "이메일을 입력해 주세요.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    @field:Size(max = 255, message = "이메일은 255자를 넘을 수 없습니다.")
    val email: String,

    // 화면이 보기를 제시하지만 서버는 검증하지 않는다 — 보기를 서버가 알면 화면 하드코딩의 이점이 사라진다.
    @field:NotBlank(message = "이력서 타이틀을 선택해 주세요.")
    @field:Size(max = 100, message = "이력서 타이틀은 100자를 넘을 수 없습니다.")
    @field:Pattern(regexp = "^[^\\r\\n]*$", message = "이력서 타이틀에 줄바꿈을 넣을 수 없습니다.")
    val title: String,
)

data class UpdateUserRequest(
    @field:Size(max = 50, message = "이름은 50자를 넘을 수 없습니다.")
    val name: String? = null,

    @field:Pattern(regexp = "^[0-9-\\s]{10,20}$", message = "휴대폰번호 형식이 올바르지 않습니다.")
    val phone: String? = null,

    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    @field:Size(max = 255, message = "이메일은 255자를 넘을 수 없습니다.")
    val email: String? = null,

    @field:Size(max = 100, message = "이력서 타이틀은 100자를 넘을 수 없습니다.")
    @field:Pattern(regexp = "^[^\\r\\n]*$", message = "이력서 타이틀에 줄바꿈을 넣을 수 없습니다.")
    val title: String? = null,
)

data class UserResponse(
    val id: Long,
    val name: String,
    val phone: String,
    val email: String,
    val title: String,
    val createdAt: Instant,
) {
    companion object {
        fun of(user: User) = UserResponse(
            id = requireNotNull(user.id),
            name = user.name,
            // 저장은 숫자만, 응답은 사람이 읽는 표기로.
            phone = PhoneNumber.format(user.phone),
            email = user.email,
            title = user.title,
            createdAt = user.createdAt,
        )
    }
}
