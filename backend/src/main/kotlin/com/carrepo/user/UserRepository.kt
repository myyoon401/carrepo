package com.carrepo.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun existsByNameAndPhoneAndEmail(name: String, phone: String, email: String): Boolean

    fun findByNameAndPhoneAndEmail(name: String, phone: String, email: String): User?
}
