# PRM Car Management App

Ứng dụng Android để quản lý xe hơi, kết nối với backend .NET Core API.

## 🚀 Cách chạy ứng dụng

### 1. Backend Setup
```bash
# Di chuyển vào thư mục backend
cd CarManager/PRM392_Assigment_CarManager/WebAPI

# Chạy backend
dotnet run
```

Backend sẽ chạy trên: `http://192.168.2.125:5274`

### 2. Database Setup
- Database đã được tạo sẵn với dữ liệu mẫu
- Các user test đã được thêm vào database

### 3. Android App Setup
```bash
# Di chuyển vào thư mục app
cd PRMCAR

# Build app
.\gradlew.bat build

# Chạy app trên emulator hoặc thiết bị thật
.\gradlew.bat installDebug
```

## 🔐 Thông tin đăng nhập

### User Test Accounts:
1. **Admin User:**
   - Email: `test@example.com`
   - Password: `test123`

2. **Admin User 2:**
   - Email: `admin@example.com`
   - Password: `admin123`

3. **Seller User:**
   - Email: `seller@example.com`
   - Password: `seller123`

4. **Buyer User:**
   - Email: `buyer@example.com`
   - Password: `buyer123`

## 📱 Tính năng chính

- ✅ Đăng nhập/Đăng xuất
- ✅ Xem danh sách xe
- ✅ Tìm kiếm xe
- ✅ Xem chi tiết xe (Click vào xe để xem chi tiết)
- ✅ Thêm xe mới (Click nút + để thêm xe)
- ✅ Xóa xe (Trong màn hình chi tiết xe)
- ✅ Refresh danh sách (Pull to refresh)

## 🔧 Cấu hình mạng

App đã được cấu hình để cho phép HTTP traffic đến:
- `10.0.2.2` (Android Emulator localhost)
- `localhost`
- `127.0.0.1`
- `192.168.2.125` (IP thật của máy)

## 🛠️ Troubleshooting

### Lỗi "CLEARTEXT communication not permitted"
- Đã được sửa bằng cách thêm IP vào `network_security_config.xml`

### Lỗi "Unauthorized"
- Kiểm tra backend có đang chạy không
- Kiểm tra IP trong `ApiClient.kt` có đúng không
- Sử dụng credentials đúng từ danh sách trên

### Lỗi kết nối
- Đảm bảo backend đang chạy trên `0.0.0.0:5274`
- Kiểm tra firewall Windows
- Đảm bảo emulator và máy host cùng mạng

### Các nút không hoạt động
- Đã được sửa bằng cách implement đầy đủ navigation và các screen
- Click vào xe để xem chi tiết
- Click nút + để thêm xe mới
- Pull to refresh để làm mới danh sách

## 📁 Cấu trúc project

```
PRMCAR/
├── app/src/main/java/com/example/prmcar/
│   ├── data/
│   │   ├── api/          # API interfaces
│   │   ├── model/        # Data models
│   │   ├── preferences/  # Token management
│   │   └── repository/   # Data repositories
│   ├── presentation/
│   │   ├── screen/       # UI screens
│   │   └── viewmodel/    # ViewModels
│   └── navigation/       # Navigation
└── app/src/main/res/
    └── xml/
        └── network_security_config.xml
```

## 🎯 API Endpoints

- `POST /api/Authentication/Login` - Đăng nhập
- `GET /api/Cars` - Lấy danh sách xe
- `GET /api/Cars/{id}` - Lấy chi tiết xe
- `POST /api/Cars` - Thêm xe mới
- `PUT /api/Cars/{id}` - Cập nhật xe
- `DELETE /api/Cars/{id}` - Xóa xe

Tất cả endpoints (trừ Login) yêu cầu JWT token trong header `Authorization: Bearer <token>` 