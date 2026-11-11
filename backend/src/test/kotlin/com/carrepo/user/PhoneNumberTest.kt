package com.carrepo.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PhoneNumberTest {

    private val pattern = Regex(PhoneNumber.PATTERN)

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
        assertEquals("010-123-4567", PhoneNumber.format("0101234567"))
    }

    @Test
    @DisplayName("길이가 맞지 않으면 그대로 둔다")
    fun leavesUnknownLengthAlone() {
        assertEquals("123", PhoneNumber.format("123"))
    }

    @Test
    @DisplayName("하이픈이 있든 없든 통과한다")
    fun acceptsBothSeparators() {
        assertTrue(pattern.matches("010-1234-5678"))
        assertTrue(pattern.matches("01012345678"))
        assertTrue(pattern.matches("010 1234 5678"))
        assertTrue(pattern.matches("0111234567"))
    }

    @Test
    @DisplayName("구분자만 있는 입력은 막는다 — 통과하면 정규화 결과가 빈 문자열이 되어 저장된다")
    fun rejectsSeparatorsOnly() {
        assertFalse(pattern.matches("----------"))
        assertFalse(pattern.matches("          "))
    }

    @Test
    @DisplayName("자릿수가 모자라거나 휴대폰 대역이 아니면 막는다")
    fun rejectsWrongShape() {
        assertFalse(pattern.matches("010"))
        assertFalse(pattern.matches("0212345678"))
        assertFalse(pattern.matches("010-1234-56789"))
        assertFalse(pattern.matches("01312345678"))
    }
}
