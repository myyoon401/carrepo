package com.carrepo.common.exception

class CarrepoException(
    val errorCode: CarrepoErrorCode,
    override val message: String = errorCode.message,
) : RuntimeException(message)
