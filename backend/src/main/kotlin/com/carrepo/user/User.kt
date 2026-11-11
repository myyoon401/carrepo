package com.carrepo.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

/**
 * 서비스 사용자.
 *
 * 이름·휴대폰번호·이메일 셋이 한 사람을 식별한다. 다만 그 셋을 PK 로 쓰지는 않는다 —
 * 연락처는 바뀌는 값이라 PK 로 삼으면 바뀔 때마다 모든 FK 가 깨진다.
 * 식별 의미는 유니크 제약(`uk_users_identity`)이 보장하고, 참조는 대리키 `id` 가 받는다.
 */
@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_users_identity", columnNames = ["name", "phone", "email"]),
    ],
)
class User(
    name: String,
    phone: String,
    email: String,
    title: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null

    @Column(name = "name", length = 50, nullable = false)
    var name: String = name

    // 숫자만 남긴 형태로 저장한다. 하이픈 표기는 출력할 때 만든다.
    @Column(name = "phone", length = 20, nullable = false)
    var phone: String = phone

    @Column(name = "email", length = 255, nullable = false)
    var email: String = email

    // 이력서 부제목. 화면이 보기를 제시하지만 서버는 자유 문자열로 받는다.
    @Column(name = "title", length = 100, nullable = false)
    var title: String = title

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    lateinit var createdAt: Instant

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    lateinit var updatedAt: Instant

    /** 넘어온 값만 바꾼다. 하나라도 실제로 바뀌면 true — 호출부가 새 이력서 버전을 만들지 판단한다. */
    fun update(name: String?, phone: String?, email: String?, title: String?): Boolean {
        var changed = false
        if (name != null && name != this.name) { this.name = name; changed = true }
        if (phone != null && phone != this.phone) { this.phone = phone; changed = true }
        if (email != null && email != this.email) { this.email = email; changed = true }
        if (title != null && title != this.title) { this.title = title; changed = true }
        return changed
    }
}
