package io.sc.eppCordova.data.remote

import io.sc.eppCordova.data.local.entity.CropRecord
import io.sc.eppCordova.data.local.entity.LandRecord
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

data class ApiResponse(val success: Boolean, val message: String)
data class TokenResponse(val success: Boolean, val token: String, val message: String)
data class OtpRequest(val mobile: String)
data class VerifyOtpRequest(val mobile: String, val otp: String)

interface ApiService {
    @GET("divisions")
    suspend fun getDivisions(): List<String>

    @GET("districts")
    suspend fun getDistricts(@Query("division") division: String): List<String>

    @GET("talukas")
    suspend fun getTalukas(@Query("district") district: String): List<String>

    @GET("villages")
    suspend fun getVillages(@Query("taluka") taluka: String): List<String>

    @GET("parcels")
    suspend fun getParcels(@Query("village") village: String): List<LandRecord>

    @POST("submitSurvey")
    suspend fun submitSurvey(@Body record: CropRecord): ApiResponse

    @POST("sendOtp")
    suspend fun sendOtp(@Body request: OtpRequest): ApiResponse

    @POST("verifyOtp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): TokenResponse
}
