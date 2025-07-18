package com.example.prmcar.data.utils

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.example.prmcar.data.api.ImageUploadService
import com.example.prmcar.data.api.UpdateImageRequest
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

class ImageUploadManager(
    private val imageUploadService: ImageUploadService
) {
    
    suspend fun uploadImage(context: Context, imageUri: Uri, carId: Int): Result<String> {
        return try {
            // Tạo file từ URI
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            val outputStream = FileOutputStream(file)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            // Tạo MultipartBody
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val carIdBody = carId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            
            // Upload ảnh
            val response = imageUploadService.uploadImage(body, carIdBody)
            
            if (response.isSuccessful) {
                val imageUrl = response.body()?.imageUrl
                if (imageUrl != null) {
                    // Cập nhật URL ảnh cho xe
                    imageUploadService.updateCarImage(carId, UpdateImageRequest(imageUrl))
                    Result.success(imageUrl)
                } else {
                    Result.failure(Exception("Upload failed: No image URL returned"))
                }
            } else {
                Result.failure(Exception("Upload failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

@Composable
fun rememberImageUploadLauncher(
    onImageSelected: (Uri) -> Unit
) = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.GetContent()
) { uri ->
    uri?.let { onImageSelected(it) }
} 