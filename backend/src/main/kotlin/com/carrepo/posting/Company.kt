package com.carrepo.posting

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

// 회사. 지금은 이름뿐이다 — 계열·도메인 같은 집계용 컬럼은 집계를 실제로 만들 때 추가한다.
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    name = "company",
    uniqueConstraints = [UniqueConstraint(name = "uk_company_name", columnNames = ["name"])],
)
class Company(
    @Column(name = "name", length = 100, nullable = false)
    var name: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant
}
