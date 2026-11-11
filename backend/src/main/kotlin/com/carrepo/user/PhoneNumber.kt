package com.carrepo.user

// 입력은 `010-1234-5678` 도 `010 1234 5678` 도 올 수 있다. 저장은 숫자만, 출력은 하이픈 표기로 통일한다.
object PhoneNumber {

    /**
     * 모양과 **자릿수를 함께** 강제한다.
     *
     * 자릿수를 안 보면 `"----------"` 같은 입력이 통과한 뒤 정규화 결과가 빈 문자열이 되고,
     * 그게 `uk_users_identity` 에 그대로 들어간다.
     * 세 요청(가입·식별·수정)이 같은 상수를 쓰게 해서 나중에 갈라지지 않도록 한다.
     */
    const val PATTERN = "^01[016789][-\\s]?\\d{3,4}[-\\s]?\\d{4}$"

    fun normalize(raw: String): String = raw.filter(Char::isDigit)

    fun format(digits: String): String = when {
        digits.length == 11 -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
        digits.length == 10 -> "${digits.substring(0, 3)}-${digits.substring(3, 6)}-${digits.substring(6)}"
        else -> digits
    }
}
