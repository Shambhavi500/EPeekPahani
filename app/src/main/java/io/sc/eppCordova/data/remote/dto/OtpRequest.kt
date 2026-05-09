package io.sc.eppCordova.data.remote.dto

data class OtpRequest(
    val mobile: String,
    val name: String
)

data class OtpVerifyRequest(
    val mobile: String,
    val otp: String
)
