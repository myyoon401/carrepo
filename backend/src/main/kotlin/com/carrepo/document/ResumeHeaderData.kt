package com.carrepo.document

/**
 * 이력서 머리말을 그리는 데 필요한 값.
 *
 * document 는 user 엔티티를 받지 않는다. 필요한 값만 이 객체로 건네받는다 —
 * 사용자 모델이 바뀌어도 이력서 렌더링은 영향을 받지 않는다.
 * phone 은 이미 사람이 읽는 표기로 넘어온다. 포맷 규칙은 넘기는 쪽의 몫이다.
 */
data class ResumeHeaderData(
    val name: String,
    val phone: String,
    val email: String,
    val title: String,
)
