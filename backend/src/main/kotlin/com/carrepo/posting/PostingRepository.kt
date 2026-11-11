package com.carrepo.posting

import org.springframework.data.jpa.repository.JpaRepository

interface PostingRepository : JpaRepository<Posting, Long> {

    fun findByUserIdOrderByCreatedAtDesc(userId: Long): List<Posting>
}
