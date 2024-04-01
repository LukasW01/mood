package dev.wigger.mood.dto

import jakarta.json.bind.annotation.JsonbCreator

/**
 * This file contains all incoming DTOs.
 * Here, [LoginDto] is a data class containing immutable class members
 */
data class LoginDto @JsonbCreator constructor(
    val username: String,
    val password: String,
)

data class RegisterDto @JsonbCreator constructor(
    val username: String,
    val mail: String,
    val firstName: String?,
    val lastName: String?,
    val password: String,
)

data class UpdateDto @JsonbCreator constructor(
    val mail: String?,
    val firstName: String?,
    val lastName: String?,
    val oldPassword: String,
    val newPassword: String,
)

data class DeleteDto @JsonbCreator constructor(
    val username: String,
    val password: String,
)
data class UserDto @JsonbCreator constructor(
    val mail: String,
    val username: String,
    val firstName: String?,
    val lastName: String?,
)

data class AuthResponseDto @JsonbCreator constructor(
    val token: String,
)
