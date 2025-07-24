package com.example.prmcar.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ImageUploadService {
    
    @Multipart
    @POST("api/upload/image")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("carId") carId: RequestBody
    ): Response<ImageUploadResponse>
    
    @POST("api/cars/{carId}/image")
    suspend fun updateCarImage(
        @Path("carId") carId: Int,
        @Body request: UpdateImageRequest
    ): Response<Unit>
}

data class ImageUploadResponse(
    val image: String,
    val message: String
)

data class UpdateImageRequest(
    val image: String
) 