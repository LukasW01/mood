package dev.wigger.mood.dto

import jakarta.json.bind.annotation.JsonbCreator
import java.util.UUID

data class Ip @JsonbCreator constructor(
    val ip: String,
)

data class ErrorResponse @JsonbCreator constructor(
    val text: String,
    val status: Int,
)

data class InfoResponse @JsonbCreator constructor(
    val text: String,
    val status: Int,
)

data class UuidTokenDto @JsonbCreator constructor(
    val token: UUID,
)

data class TokenDto @JsonbCreator constructor(
    val token: String,
)
