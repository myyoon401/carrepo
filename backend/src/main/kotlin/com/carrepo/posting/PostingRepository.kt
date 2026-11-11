package com.carrepo.posting

import org.springframework.data.jpa.repository.JpaRepository

interface PostingRepository : JpaRepository<Posting, Long> {

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Posting>

    // 소유권을 조회 조건에 넣는다. 인증이 붙으면 userId 출처만 바뀐다.
    fun findByIdAndUserId(id: Long, userId: Long): Posting?
}
