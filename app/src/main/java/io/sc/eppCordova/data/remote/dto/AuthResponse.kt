package io.sc.eppCordova.data.remote.dto

data class AuthResponse(
    val success: Boolean,
    val message: String?,
    val token: String?,
    val userId: String?,
    val name: String?
)
