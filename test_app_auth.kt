// Test file để kiểm tra authentication trong Android app
// Chạy test này để xem app có gửi đúng credentials không

import com.example.prmcar.data.api.ApiClient
import com.example.prmcar.data.model.UserLogin
import kotlinx.coroutines.runBlocking

fun testAppAuthentication() {
    runBlocking {
        try {
            println("Testing Android App Authentication...")
            
            // Test 1: Login với credentials đúng
            val authApi = ApiClient.createAuthApi()
            val loginRequest = UserLogin(
                email = "test@example.com",
                passwordHash = "test123"
            )
            
            val response = authApi.login(loginRequest)
            
            if (response.isSuccessful) {
                val loginResponse = response.body()
                println("✅ Login successful!")
                println("Token: ${loginResponse?.token}")
                
                // Test 2: Sử dụng token để gọi Cars API
                println("\nTesting Cars API with token...")
                // TODO: Implement cars API test
                
            } else {
                println("❌ Login failed!")
                println("Status Code: ${response.code()}")
                println("Error Body: ${response.errorBody()?.string()}")
            }
            
        } catch (e: Exception) {
            println("❌ Exception occurred: ${e.message}")
            e.printStackTrace()
        }
    }
}

// Test credentials để sử dụng trong app:
// Email: test@example.com
// Password: test123
// 
// Hoặc:
// Email: admin@example.com  
// Password: admin123 