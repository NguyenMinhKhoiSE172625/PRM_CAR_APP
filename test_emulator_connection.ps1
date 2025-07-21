# Test kết nối từ emulator đến backend
$baseUrl = "http://192.168.2.125:5274"

Write-Host "Testing Emulator Connection to Backend..." -ForegroundColor Green

# Test 1: Kiểm tra kết nối cơ bản
Write-Host "`n1. Testing basic connection..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "$baseUrl/swagger" -Method GET -TimeoutSec 10
    Write-Host "✅ Connection successful!" -ForegroundColor Green
    Write-Host "Status Code: $($response.StatusCode)" -ForegroundColor Cyan
} catch {
    Write-Host "❌ Connection failed!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# Test 2: Test login API từ emulator perspective
Write-Host "`n2. Testing Login API from emulator..." -ForegroundColor Yellow
$loginData = @{
    email = "test@example.com"
    passwordHash = "test123"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/api/Authentication/Login" -Method POST -Body $loginData -ContentType "application/json" -TimeoutSec 10
    Write-Host "✅ Login successful from emulator!" -ForegroundColor Green
    Write-Host "Token: $($loginResponse.token)" -ForegroundColor Cyan
    
    # Test 3: Use token to access Cars API
    Write-Host "`n3. Testing Cars API with token..." -ForegroundColor Yellow
    $headers = @{
        "Authorization" = "Bearer $($loginResponse.token)"
        "Content-Type" = "application/json"
    }
    
    $carsResponse = Invoke-RestMethod -Uri "$baseUrl/api/Cars" -Method GET -Headers $headers -TimeoutSec 10
    Write-Host "✅ Cars API successful!" -ForegroundColor Green
    Write-Host "Number of cars: $($carsResponse.items.Count)" -ForegroundColor Cyan
    
} catch {
    Write-Host "❌ API call failed!" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    if ($_.Exception.Response) {
        $statusCode = $_.Exception.Response.StatusCode
        Write-Host "Status Code: $statusCode" -ForegroundColor Red
    }
}

Write-Host "`nTest completed!" -ForegroundColor Green 