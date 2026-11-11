package com.carrepo.user

import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 홈 화면의 입구. 이름·휴대폰·이메일로 사람을 찾고, 없으면 404 를 돌려준다.
 * 찾지 못했다고 해서 여기서 만들지는 않는다 — 오타로 계정이 생기면 안 된다.
 * 가입은 `POST /api/users` 로 따로 요청한다.
 *
 * ⚠️ 이것은 인증이 아니다. 이력서에 적혀 있는 세 값만 알면 누구나 들어올 수 있다.
 * V1 이 로컬 전용이라 성립하는 구조다. 외부에 공개한다면 이 엔드포인트 뒤에
 * 실제 인증(비밀번호·OAuth 등)을 붙여야 한다. 그때 바뀌는 것은 요청 본문이지 주소가 아니다.
 *
 * user 패키지에 둔 이유: 하는 일이 사용자 식별뿐이다. 토큰·비밀번호가 생겨
 * 실제로 다룰 상태가 늘어나면 그때 독립 기능으로 뺀다.
 */
@RestController
@RequestMapping("/api/sessions")
class SessionController(
    private val userService: UserService,
) {

    @PostMapping
    fun start(@Valid @RequestBody request: SessionRequest): UserResponse =
        UserResponse.of(userService.identify(request))
}
