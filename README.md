# PRM CAR - Car Management Android App

Ứng dụng quản lý xe hơi được phát triển bằng Android (Kotlin + Jetpack Compose) và .NET Core Web API.

## Cấu trúc Project

- **PRMCAR/**: Android App (Frontend)
- **CarManager/**: .NET Core Web API (Backend)

## Hướng dẫn chạy ứng dụng

### 1. Chạy Backend (.NET API)

1. Mở folder `CarManager/PRM392_Assigment_CarManager` bằng Visual Studio hoặc VS Code
2. Restore packages:
   ```bash
   dotnet restore
   ```
3. Cập nhật connection string trong `WebAPI/appsettings.json` nếu cần
4. Chạy API:
   ```bash
   cd WebAPI
   dotnet run
   ```
5. API sẽ chạy tại `http://localhost:5274` (HTTP) hoặc `https://localhost:7051` (HTTPS)
6. Truy cập Swagger UI tại: `http://localhost:5274/swagger`

### 2. Setup Database

1. Chạy script SQL trong file `CarManager/dataCarmanager.sql` để tạo database và dữ liệu mẫu
2. Cập nhật connection string trong `appsettings.json` nếu cần

### 3. Chạy Android App

1. Mở folder `PRMCAR` bằng Android Studio
2. Sync project để download dependencies
3. Chạy ứng dụng trên emulator hoặc thiết bị thật

## Tính năng chính

- **Authentication**: Đăng nhập/đăng xuất với JWT tokens
- **Car Management**: Xem danh sách xe, chi tiết xe, tìm kiếm
- **Real-time Data**: Tự động refresh và hiển thị dữ liệu mới nhất

## Tài khoản test

Sử dụng các tài khoản có sẵn trong database:

```
Email: nguyenvana@example.com
Password: hashed_pass_seller1

Email: tranthib@example.com  
Password: hashed_pass_buyer1
```

## API Endpoints

### Authentication
- `POST /api/Authentication/Login` - Đăng nhập

### Cars
- `GET /api/Cars` - Lấy danh sách xe (có filter, search, pagination)
- `GET /api/Cars/{id}` - Lấy chi tiết xe
- `POST /api/Cars` - Tạo xe mới
- `PUT /api/Cars/{id}` - Cập nhật xe
- `DELETE /api/Cars/{id}` - Xóa xe

### Car Types
- `GET /api/CarTypes` - Lấy danh sách loại xe
- Các CRUD endpoints khác...

### Transactions
- `GET /api/Transactions` - Lấy danh sách giao dịch
- Các CRUD endpoints khác...

### Users
- `GET /api/Users` - Lấy danh sách người dùng
- Các CRUD endpoints khác...

## Kiến trúc ứng dụng

### Android App
- **MVVM Pattern** với ViewModels
- **Jetpack Compose** cho UI
- **Retrofit** cho API calls
- **Navigation Compose** cho điều hướng
- **DataStore** cho lưu trữ token
- **Coroutines & Flow** cho async operations

### Backend API
- **Clean Architecture** với Repository Pattern
- **Entity Framework Core** cho database access
- **JWT Authentication**
- **AutoMapper** cho mapping models
- **Swagger** cho API documentation

## Troubleshooting

### Lỗi Backend
1. **".NET SDK not found"**: 
   - Cài đặt .NET 8.0 SDK từ: https://dotnet.microsoft.com/download/dotnet/8.0
   - Restart terminal sau khi cài đặt
   - Kiểm tra: `dotnet --version`

2. **Database errors**: 
   - Kiểm tra SQL Server đang chạy
   - Cập nhật connection string trong `appsettings.json`
   - Chạy script SQL trong `dataCarmanager.sql`

3. **Port conflicts**: Nếu port 5274 đã được sử dụng, sửa trong `launchSettings.json`

### Lỗi Android App
1. **Lỗi kết nối API**: 
   - Đảm bảo backend đang chạy tại `http://localhost:5274`
   - Kiểm tra network security config đã được thêm
   - Với emulator, API URL phải là `http://10.0.2.2:5274`

2. **Authentication issues**: 
   - Kiểm tra JWT configuration trong appsettings.json
   - Đảm bảo tài khoản test có trong database

3. **Build errors**: 
   - Sync project trong Android Studio
   - Clean and rebuild project

## Yêu cầu hệ thống

- **Android**: API level 24+ (Android 7.0+)
- **.NET**: .NET 8.0+
- **Database**: SQL Server
- **IDE**: Android Studio, Visual Studio/VS Code 