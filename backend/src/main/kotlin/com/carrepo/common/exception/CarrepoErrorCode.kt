package com.carrepo.common.exception

import org.springframework.http.HttpStatus

enum class CarrepoErrorCode(
    val status: HttpStatus,
    val message: String,
) {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 API를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    DUPLICATE_USER(HttpStatus.CONFLICT, "이름·휴대폰번호·이메일이 모두 같은 사용자가 이미 있습니다."),
    RESUME_NOT_FOUND(HttpStatus.NOT_FOUND, "이력서를 찾을 수 없습니다."),
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "처리 중 오류가 발생했습니다."),
}
