package com.carrepo.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PhoneNumberTest {

    @Test
    @DisplayName("입력에서 숫자만 남긴다")
    fun normalizesToDigits() {
        assertEquals("01012345678", PhoneNumber.normalize("010-1234-5678"))
        assertEquals("01012345678", PhoneNumber.normalize("010 1234 5678"))
        assertEquals("01012345678", PhoneNumber.normalize("01012345678"))
    }

    @Test
    @DisplayName("11자리와 10자리를 각각 하이픈 표기로 만든다")
    fun formatsByLength() {
        assertEquals("010-1234-5678", PhoneNumber.format("01012345678"))
        assertEquals("021-234-5678", PhoneNumber.format("0212345678"))
    }

    @Test
    @DisplayName("길이가 맞지 않으면 그대로 둔다")
    fun leavesUnknownLengthAlone() {
        assertEquals("123", PhoneNumber.format("123"))
    }
}
