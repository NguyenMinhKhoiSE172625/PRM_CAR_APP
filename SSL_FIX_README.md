# SSL Certificate Error Fix

## Lỗi gặp phải:
```
java.security.cert.CertPathValidatorException: Trust anchor for certification path not found
```

## Nguyên nhân:
Lỗi này xảy ra khi ứng dụng Android cố gắng kết nối với backend API và gặp vấn đề với SSL certificate. Có thể do:
1. Backend sử dụng self-signed certificate
2. Certificate không được trust bởi Android
3. Vấn đề với SSL/TLS configuration

## Giải pháp đã áp dụng:

### 1. Cập nhật ApiClient.kt
- Thêm `TrustManager` để trust tất cả certificates (chỉ cho development)
- Thêm `SSLContext` với custom trust manager
- Thêm `hostnameVerifier` để trust tất cả hostnames
- Áp dụng SSL trust cho tất cả API calls

### 2. Cập nhật network_security_config.xml
- Thêm `base-config` với `cleartextTrafficPermitted="true"`
- Thêm trust anchors cho system và user certificates
- Cho phép cleartext traffic cho development

### 3. Cập nhật AndroidManifest.xml
- Thêm permission `ACCESS_NETWORK_STATE`
- Thêm `android:usesCleartextTraffic="true"`

## Các thay đổi chính:

### ApiClient.kt:
```kotlin
// Thêm TrustManager để trust tất cả certificates
private fun createTrustAllCerts(): Array<TrustManager> {
    return arrayOf(object : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
    })
}

// Thêm SSL context và hostname verifier
.sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
.hostnameVerifier { _, _ -> true }
```

### network_security_config.xml:
```xml
<base-config cleartextTrafficPermitted="true">
    <trust-anchors>
        <certificates src="system"/>
        <certificates src="user"/>
    </trust-anchors>
</base-config>
```

### AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
android:usesCleartextTraffic="true"
```

## Lưu ý quan trọng:
⚠️ **CẢNH BÁO**: Các thay đổi này chỉ nên sử dụng cho development/testing. 
- Trust tất cả certificates có thể gây ra lỗ hổng bảo mật
- Trong production, nên sử dụng proper SSL certificates
- Nên remove các thay đổi này trước khi deploy lên production

## Cách test:
1. Clean và rebuild project
2. Chạy ứng dụng trên emulator
3. Thử đăng nhập với tài khoản test
4. Kiểm tra log để đảm bảo không còn lỗi SSL

## Tài khoản test:
```
Email: nguyenvana@example.com
Password: hashed_pass_seller1
```

## Nếu vẫn gặp lỗi:
1. Kiểm tra backend có đang chạy không
2. Kiểm tra URL API có đúng không (http://10.0.2.2:5274/)
3. Kiểm tra network connectivity
4. Xem log chi tiết trong Android Studio 