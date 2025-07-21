package com.example.prmcar.data.api

import com.example.prmcar.data.preferences.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object ApiClient {
    private const val BASE_URL = "http://10.0.2.2:5274/" // For Android emulator to access localhost

    // Trust all certificates for development (NOT for production)
    private fun createTrustAllCerts(): Array<TrustManager> {
        return arrayOf(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })
    }

    private fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        val trustAllCerts = createTrustAllCerts()
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())
        
        return OkHttpClient.Builder()
            .addInterceptor(createLoggingInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier { _, _ -> true } // Trust all hostnames
            .build()
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Auth API doesn't need auth header
    fun createAuthApi(): AuthApi {
        return createRetrofit().create(AuthApi::class.java)
    }

    // Other APIs are now created simply
    fun createCarApi(): CarApi {
        return createRetrofit().create(CarApi::class.java)
    }

    fun createCarTypeApi(): CarTypeApi {
        return createRetrofit().create(CarTypeApi::class.java)
    }

    fun createTransactionApi(): TransactionApi {
        return createRetrofit().create(TransactionApi::class.java)
    }

    fun createUserApi(): UserApi {
        return createRetrofit().create(UserApi::class.java)
    }
} 