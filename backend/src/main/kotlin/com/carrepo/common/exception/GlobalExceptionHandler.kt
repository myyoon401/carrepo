package com.carrepo.common.exception

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.resource.NoResourceFoundException

// 오류 응답 형태를 한 곳에서만 만든다.
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(CarrepoException::class)
    fun handleCarrepo(e: CarrepoException): ResponseEntity<ErrorResponse> =
        toResponse(e.errorCode, e.message)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors = e.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "올바르지 않은 값입니다.")
        }
        return toResponse(CarrepoErrorCode.INVALID_REQUEST, fieldErrors = fieldErrors)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(): ResponseEntity<ErrorResponse> =
        toResponse(CarrepoErrorCode.INVALID_REQUEST, "요청 본문 형식이 올바르지 않습니다.")

    // 없는 API 는 404 다. 아래 포괄 핸들러가 500 으로 삼키지 않도록 먼저 잡는다.
    // (SPA fallback 은 `/api/` 로 시작하는 경로를 index.html 로 돌리지 않는다 — SpaResourceConfig)
    @ExceptionHandler(NoResourceFoundException::class, NoHandlerFoundException::class)
    fun handleRouteNotFound(): ResponseEntity<ErrorResponse> =
        toResponse(CarrepoErrorCode.ROUTE_NOT_FOUND)

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupported(): ResponseEntity<ErrorResponse> =
        toResponse(CarrepoErrorCode.METHOD_NOT_ALLOWED)

    // 유니크 제약 위반은 서비스에서 먼저 걸러지지만, 동시 요청으로 DB 까지 가는 경우가 있다.
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleIntegrity(e: DataIntegrityViolationException): ResponseEntity<ErrorResponse> {
        log.warn("무결성 제약 위반", e)
        return toResponse(CarrepoErrorCode.DUPLICATE_USER)
    }

    @ExceptionHandler(Exception::class)
    fun handleUnexpected(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("처리하지 못한 예외", e)
        return toResponse(CarrepoErrorCode.INTERNAL_ERROR)
    }

    private fun toResponse(
        code: CarrepoErrorCode,
        message: String = code.message,
        fieldErrors: Map<String, String>? = null,
    ): ResponseEntity<ErrorResponse> =
        ResponseEntity.status(code.status).body(ErrorResponse(code.name, message, fieldErrors))
}
