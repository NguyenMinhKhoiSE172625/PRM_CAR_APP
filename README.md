# PRM Car Management App

Ứng dụng quản lý xe hơi với các tính năng phân quyền theo role và upload ảnh.

## Tính năng chính

### 🔐 Authentication & Authorization
- **Đăng nhập**: Tất cả users đều đăng nhập bằng Email và Password
- **Phân quyền theo role**:
  - **Admin**: Toàn quyền (xem, thêm, xóa xe, upload ảnh)
  - **Seller**: Có thể thêm, xóa xe và upload ảnh
  - **Buyer**: Chỉ xem danh sách xe và chi tiết xe

### 🚗 Quản lý xe
- **Homepage**: Hiển thị danh sách xe với tìm kiếm
- **Car Details**: Xem chi tiết xe (không thể sửa)
- **Add Car**: Thêm xe mới (chỉ Admin và Seller)
- **Delete Car**: Xóa xe (chỉ Admin và Seller)

### 📸 Upload ảnh
- **Chọn ảnh**: Từ gallery hoặc camera
- **Lưu URL**: Ảnh được lưu dưới dạng URL để load lại khi mở app
- **Hiển thị ảnh**: Sử dụng Coil để load ảnh từ URL

## Cấu trúc dự án

```
PRMCAR/
├── app/src/main/java/com/example/prmcar/
│   ├── data/
│   │   ├── api/           # API services
│   │   ├── model/         # Data models
│   │   ├── repository/    # Repository layer
│   │   └── utils/         # Utilities (ImageUploadManager)
│   ├── di/                # Dependency injection
│   ├── navigation/        # Navigation
│   ├── presentation/
│   │   ├── screen/        # UI screens
│   │   └── viewmodel/     # ViewModels
│   └── ui/theme/          # UI theme
```

## Cài đặt và chạy

### Yêu cầu
- Android Studio Hedgehog | 2023.1.1
- Android SDK 35
- Kotlin 1.9.0

### Bước 1: Clone và mở project
```bash
git clone <repository-url>
cd PRMCAR
```

### Bước 2: Cấu hình backend
Đảm bảo backend PRM392 đang chạy trên `http://localhost:5274`

### Bước 3: Build và chạy
```bash
./gradlew build
./gradlew installDebug
```

## Tài khoản test

### Admin
- Email: `admin@example.com`
- Password: `admin123`

### Seller
- Email: `seller@example.com`
- Password: `seller123`

### Buyer
- Email: `buyer@example.com`
- Password: `buyer123`

## API Endpoints

### Authentication
- `POST /api/Authentication/Login` - Đăng nhập

### Cars
- `GET /api/Cars` - Lấy danh sách xe
- `GET /api/Cars/{id}` - Lấy chi tiết xe
- `POST /api/Cars` - Thêm xe mới
- `PUT /api/Cars/{id}` - Cập nhật xe
- `DELETE /api/Cars/{id}` - Xóa xe

### Image Upload
- `POST /api/upload/image` - Upload ảnh
- `POST /api/cars/{carId}/image` - Cập nhật ảnh cho xe

## Công nghệ sử dụng

### Frontend (Android)
- **Jetpack Compose** - UI framework
- **Navigation Compose** - Navigation
- **ViewModel & StateFlow** - State management
- **Retrofit** - HTTP client
- **Coil** - Image loading
- **DataStore** - Local storage

### Backend (ASP.NET Core)
- **Entity Framework Core** - ORM
- **JWT Authentication** - Authentication
- **AutoMapper** - Object mapping
- **Repository Pattern** - Data access

## Tính năng nổi bật

### 🔒 Phân quyền thông minh
- Mỗi role có quyền truy cập khác nhau
- UI tự động ẩn/hiện các chức năng theo role
- Backend validation cho tất cả API calls

### 📱 UX/UI hiện đại
- Material Design 3
- Dark/Light theme support
- Responsive design
- Loading states và error handling

### 🖼️ Quản lý ảnh
- Upload ảnh từ gallery/camera
- Lưu URL để load lại
- Optimized image loading với Coil
- Placeholder khi chưa có ảnh

### 🔄 State Management
- Single source of truth
- Reactive UI updates
- Error handling và retry logic
- Offline support (cached data)

## Troubleshooting

### Lỗi kết nối backend
1. Kiểm tra backend có đang chạy không
2. Kiểm tra URL trong `AppModule.kt`
3. Kiểm tra network permissions

### Lỗi upload ảnh
1. Kiểm tra storage permissions
2. Kiểm tra camera permissions
3. Kiểm tra backend image upload endpoint

### Lỗi authentication
1. Kiểm tra JWT token
2. Kiểm tra user credentials
3. Kiểm tra backend authentication endpoint

## Đóng góp

1. Fork project
2. Tạo feature branch
3. Commit changes
4. Push to branch
5. Tạo Pull Request

## License

MIT License - xem file LICENSE để biết thêm chi tiết. 