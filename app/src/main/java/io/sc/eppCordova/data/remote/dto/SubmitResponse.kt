package io.sc.eppCordova.data.remote.dto

data class SubmitResponse(
    val success: Boolean,
    val referenceId: String?,
    val error: String?
)
