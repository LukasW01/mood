package dev.wigger.mood.dto

import jakarta.json.bind.annotation.JsonbCreator
import java.time.ZonedDateTime
import java.util.UUID

data class SharingDto @JsonbCreator constructor(
    val createdAt: ZonedDateTime?,
    val updatedAt: ZonedDateTime?,
    val user: UserDto,
)

data class SharingDelegatorDto @JsonbCreator constructor(
    val createdAt: ZonedDateTime?,
    val updatedAt: ZonedDateTime?,
    val delegator: UserDto,
)

data class SharingTokenDto @JsonbCreator constructor(
    val token: UUID,
)
