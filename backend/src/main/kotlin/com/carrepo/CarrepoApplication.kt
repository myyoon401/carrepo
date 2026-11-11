package com.carrepo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CarrepoApplication

fun main(args: Array<String>) {
    runApplication<CarrepoApplication>(*args)
}
