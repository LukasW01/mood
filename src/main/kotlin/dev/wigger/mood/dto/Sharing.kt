package dev.wigger.mood.dto

import dev.wigger.mood.shareing.Sharing
import dev.wigger.mood.util.enums.Permissions
import jakarta.json.bind.annotation.JsonbCreator
import java.time.ZonedDateTime
import java.util.UUID

data class SharingDto @JsonbCreator constructor(
    var createdAt: ZonedDateTime?,
    var updatedAt: ZonedDateTime?,
    var user: UserDto,
    var delegator: UserDto,
    val permissions: Permissions,
)

data class SharingDelegatorDto @JsonbCreator constructor(
    val createdAt: ZonedDateTime?,
    val updatedAt: ZonedDateTime?,
    val delegator: UserDto,
    val permissions: Permissions,
    val entry: List<EntryDto>?,
)

data class SharingUpdateDto @JsonbCreator constructor(
    val permissions: Permissions,
)

data class SharingTokenDto @JsonbCreator constructor(
    val token: UUID,
)

data class SharingSubmittDto @JsonbCreator constructor(
    val token: UUID,
    val permissions: Permissions,
)

fun Sharing.toDto(): SharingDto = SharingDto(
    createdAt = createdAt,
    updatedAt = updatedAt,
    user = user.toDto(),
    delegator = delegator.toDto(),
    permissions = permissions,
)

fun List<Sharing>.toDtoList(): List<SharingDto> = this.map { it.toDto() }

fun Sharing.toDelegatorDto(): SharingDelegatorDto = SharingDelegatorDto(
    createdAt = createdAt,
    updatedAt = updatedAt,
    delegator = delegator.toDto(),
    permissions = permissions,
    entry = entry?.toDtoList(),
)
