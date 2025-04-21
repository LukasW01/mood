package dev.wigger.mood.dto

import dev.wigger.mood.user.Users
import jakarta.json.bind.annotation.JsonbCreator
import java.util.UUID

data class UserDto @JsonbCreator constructor(
    val id: UUID,
    val mail: String,
    val firstName: String?,
    val lastName: String?,
)

data class UpdateDto @JsonbCreator constructor(
    val mail: String?,
    val firstName: String?,
    val lastName: String?,
    val password: String,
)

data class DeleteDto @JsonbCreator constructor(
    val mail: String,
    val password: String,
)

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

data class AuthResponseDto @JsonbCreator constructor(
    val token: String,
    val user: UserDto,
)

data class RequestResetDto @JsonbCreator constructor(
    val mail: String,
)

data class ResetDto @JsonbCreator constructor(
    val password: String,
    val passwordRepeat: String,
)

fun Users.toDto(): UserDto = UserDto(
    id = id,
    mail = mail,
    lastName = lastName,
    firstName = firstName,
)
