package com.carrepo.common.exception

// 오류 응답은 한 가지 형태만 쓴다. fieldErrors 는 검증 실패일 때만 채운다.
data class ErrorResponse(
    val code: String,
    val message: String,
    val fieldErrors: Map<String, String>? = null,
)
