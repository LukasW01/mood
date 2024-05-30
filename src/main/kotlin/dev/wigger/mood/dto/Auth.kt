package dev.wigger.mood.dto

import dev.wigger.mood.user.Users
import jakarta.json.bind.annotation.JsonbCreator
import java.util.UUID

/**
 * This file contains all incoming DTOs.
 * Here, [LoginDto] is a data class containing immutable class members
 */
data class LoginDto @JsonbCreator constructor(
    val mail: String,
    val password: String,
)

data class RegisterDto @JsonbCreator constructor(
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
    val mail: String,
    val password: String,
)

data class AuthResponseDto @JsonbCreator constructor(
    val token: String,
    val user: UserDto,
)

data class UserDto @JsonbCreator constructor(
    val id: Long,
    val mail: String,
    val firstName: String?,
    val lastName: String?,
)

data class MailResetDto @JsonbCreator constructor(
    val mail: String,
)

data class ResetDto @JsonbCreator constructor(
    val password: String,
    val passwordRepeat: String,
    val token: UUID,
)

data class TokenDto @JsonbCreator constructor(
    val token: String,
)

data class TokenUuiddto @JsonbCreator constructor(
    val token: UUID,
)

fun Users.toDto(): UserDto = UserDto(
    id = id,
    mail = mail,
    lastName = lastName,
    firstName = firstName,
)
