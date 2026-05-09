package io.sc.eppCordova.data.remote

import io.sc.eppCordova.data.local.entity.CropRecord
import io.sc.eppCordova.data.remote.dto.AuthResponse
import io.sc.eppCordova.data.remote.dto.OtpRequest
import io.sc.eppCordova.data.remote.dto.SubmitResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface EPeekPahaniApi {
    @POST("sendOtp")
    suspend fun sendOtp(@Body request: OtpRequest): Response<AuthResponse>

    @POST("verifyOtp")
    suspend fun verifyOtp(@Body request: OtpRequest): Response<AuthResponse>

    @GET("divisions")
    suspend fun getDivisions(): Response<List<Any>>

    @GET("districts")
    suspend fun getDistricts(@Query("divisionCode") divisionCode: String): Response<List<Any>>

    @GET("crops")
    suspend fun getCrops(): Response<List<Any>>

    @POST("submit")
    suspend fun submitCropRecord(@Body record: CropRecord): Response<SubmitResponse>
}
