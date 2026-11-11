package com.carrepo.posting

import org.springframework.data.jpa.repository.JpaRepository

interface CompanyRepository : JpaRepository<Company, Long> {

    fun findByName(name: String): Company?
}
