package com.carrepo.user

// 입력은 `010-1234-5678` 도 `010 1234 5678` 도 올 수 있다. 저장은 숫자만, 출력은 하이픈 표기로 통일한다.
object PhoneNumber {

    fun normalize(raw: String): String = raw.filter { it.isDigit() }

    fun format(digits: String): String = when {
        digits.length == 11 -> "${digits.substring(0, 3)}-${digits.substring(3, 7)}-${digits.substring(7)}"
        digits.length == 10 -> "${digits.substring(0, 3)}-${digits.substring(3, 6)}-${digits.substring(6)}"
        else -> digits
    }
}
