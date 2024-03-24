package dev.wigger.mood.dto

import dev.wigger.mood.model.Users
import jakarta.json.bind.annotation.JsonbCreator

/**
 * This file contains all incoming DTOs.
 * Here, [LoginDto] is a data class containing immutable class members
 */
data class LoginDto(
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

data class UpdateDto(
    val username: String,
    val firstName: String?,
    val lastName: String?,
    val oldPassword: String,
    val newPassword: String,
)

data class DeleteDto(
    val username: String,
    val password: String,
)

data class AuthResponseDto(
    val token: String,
    val user: UserDto,
)

data class UserDto(
    val id: Long,
    val username: String,
    val firstName: String?,
    val lastName: String?,
)

fun Users.toDto(): UserDto = UserDto(
    id = id,
    username = username,
    firstName = firstName,
    lastName = lastName,
)
