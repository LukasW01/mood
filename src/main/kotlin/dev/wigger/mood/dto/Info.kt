package dev.wigger.mood.dto

data class Ip(
    val ip: String,
)

data class ErrorResponse(
    val text: String,
    val status: Int,
)
