package com.carrepo.user

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {

    @PostMapping
    fun signUp(@Valid @RequestBody request: SignUpRequest): ResponseEntity<UserResponse> {
        val user = userService.signUp(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(UserResponse.of(user))
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): UserResponse = UserResponse.of(userService.get(id))

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest,
    ): UserResponse = UserResponse.of(userService.update(id, request))
}
