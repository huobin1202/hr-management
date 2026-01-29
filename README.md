# Hệ Thống Quản Lý Lương & Hồ Sơ Nhân Sự (HRMS)

## Mô tả Dự án

Hệ thống quản lý nhân sự với **phân mảnh dọc (Vertical Fragmentation)** sử dụng 2 cơ sở dữ liệu vật lý riêng biệt:

- **DB1 (HR_INFO)**: Chứa thông tin công khai của nhân viên
- **DB2 (HR_SALARY)**: Chứa thông tin lương thưởng (nhạy cảm, bảo mật)

## Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────────────────┐
│                        Java Swing UI                            │
├─────────────────────────────────────────────────────────────────┤
│                       Service Layer                             │
│     (NhanVienService, LuongService, AuthService)               │
├─────────────────────────────────────────────────────────────────┤
│                         DAO Layer                               │
│         (NhanVienDAO, LuongDAO, NguoiDungDAO)                  │
├─────────────────┬───────────────────────────────────────────────┤
│                 │                                               │
│   DB1: HR_INFO  │              DB2: HR_SALARY                   │
│  ┌───────────┐  │             ┌───────────────┐                │
│  │ NhanVien  │  │             │ LuongNhanVien │                │
│  │ PhongBan  │  │←── MaNV ───→│ BangLuongThang│                │
│  │ ChucVu    │  │             │ LichSuLuong   │                │
│  │ NguoiDung │  │             │ Thuong        │                │
│  │ LogHoatDong│ │             │               │                │
│  └───────────┘  │             └───────────────┘                │
└─────────────────┴───────────────────────────────────────────────┘
```

## Yêu Cầu Hệ Thống

- **JDK**: 17 trở lên
- **SQL Server**: 2019 trở lên
- **Maven**: 3.8+

## Cài Đặt

### 1. Chuẩn bị SQL Server

Thực thi các script SQL theo thứ tự:

```bash
# 1. Tạo database HR_INFO (DB1)
sql/01_create_db1_hrinfo.sql

# 2. Tạo database HR_SALARY (DB2)
sql/02_create_db2_salary.sql

# 3. Tạo users và phân quyền
sql/03_create_users_permissions.sql

# 4. Tạo stored procedures cho giao dịch phân tán
sql/04_stored_procedures_distributed.sql
```

### 2. Cấu hình kết nối

Chỉnh sửa file `src/main/resources/database.properties`:

```properties
# DB1 - Thông tin chung
db1.server=localhost
db1.port=1433
db1.database=HR_INFO
db1.username=admin_login
db1.password=Admin@123456

# DB2 - Lương thưởng
db2.server=localhost
db2.port=1433
db2.database=HR_SALARY
db2.username=admin_login
db2.password=Admin@123456
```

### 3. Build và Chạy

```bash
# Build project
mvn clean compile

# Chạy ứng dụng
mvn exec:java -Dexec.mainClass="com.hrms.MainApp"

# Hoặc tạo JAR
mvn package
java -jar target/hrms-distributed-1.0.0.jar
```

## Tài Khoản Demo

| Vai trò | Username | Password | Quyền truy cập |
|---------|----------|----------|----------------|
| Admin | admin | admin123 | DB1 + DB2 (toàn quyền) |
| Kế toán | ketoan | ketoan123 | DB1 + DB2 (đọc/ghi) |
| Nhân viên | nhanvien | nv123456 | Chỉ DB1 (đọc) |
| IT | itadmin | it123456 | Chỉ DB1 (đọc/ghi) |

## Chức Năng Chính

### 1. Quản lý Nhân viên (DB1)
- Xem danh sách nhân viên
- Tìm kiếm theo tên, phòng ban
- Thêm/Sửa/Xóa nhân viên (Admin)
- Xem thông tin chi tiết

### 2. Quản lý Lương (DB2) - Chỉ Admin & Kế toán
- Xem bảng lương cơ bản
- Tính lương tháng
- Thêm thưởng, khấu trừ
- In bảng lương

### 3. Báo cáo Thống kê
- Thống kê theo phòng ban
- Biến động nhân sự
- Tổng quỹ lương

### 4. Demo Phân quyền
- Test kết nối từng database
- Kiểm tra quyền truy cập
- Minh họa giao dịch phân tán

## Cấu Trúc Thư Mục

```
do-an-csdlpt/
├── pom.xml                          # Maven config
├── sql/
│   ├── 01_create_db1_hrinfo.sql    # Script tạo DB1
│   ├── 02_create_db2_salary.sql    # Script tạo DB2
│   ├── 03_create_users_permissions.sql
│   └── 04_stored_procedures_distributed.sql
└── src/main/java/com/hrms/
    ├── MainApp.java                 # Entry point
    ├── dao/                         # Data Access Objects
    │   ├── NhanVienDAO.java        # Thao tác DB1
    │   ├── LuongDAO.java           # Thao tác DB2
    │   └── NguoiDungDAO.java
    ├── model/                       # Entity classes
    │   ├── NhanVien.java
    │   ├── LuongNhanVien.java
    │   ├── BangLuongThang.java
    │   └── ...
    ├── service/                     # Business logic
    │   ├── NhanVienService.java    # Xử lý phân tán
    │   ├── LuongService.java
    │   └── AuthService.java
    ├── ui/                          # Java Swing UI
    │   ├── LoginFrame.java
    │   ├── MainFrame.java
    │   ├── NhanVienPanel.java
    │   ├── LuongPanel.java
    │   ├── BaoCaoPanel.java
    │   └── PhanQuyenDemoPanel.java
    └── util/
        ├── DatabaseConnection.java
        ├── DistributedTransactionManager.java
        └── SessionManager.java
```

## Cơ Chế Phân Mảnh Dọc

### Lý do phân mảnh:
1. **Bảo mật**: Dữ liệu lương tách biệt, kiểm soát truy cập độc lập
2. **Hiệu năng**: Query nhanh hơn do bảng nhỏ hơn
3. **Bảo trì**: Dễ backup/restore theo nhóm dữ liệu

### Liên kết dữ liệu:
- Khóa chính `MaNV` được dùng để liên kết 2 database
- Sử dụng cross-database query: `HR_SALARY.dbo.LuongNhanVien`

### Giao dịch phân tán:
```java
DistributedTransactionManager txManager = new DistributedTransactionManager();
txManager.beginTransaction();
try {
    // Thao tác DB1
    nhanVienDAO.insert(nv, txManager.getDB1Connection());
    // Thao tác DB2
    luongDAO.insert(luong, txManager.getDB2Connection());
    // Commit cả 2
    txManager.commit();
} catch (Exception e) {
    txManager.rollback();
}
```

## Phân Quyền Truy Cập

| Vai trò | DB1 (HR_INFO) | DB2 (HR_SALARY) |
|---------|---------------|-----------------|
| Admin | ✅ Toàn quyền | ✅ Toàn quyền |
| Kế toán | ✅ Đọc/Ghi | ✅ Đọc/Ghi |
| Nhân viên | ✅ Chỉ đọc | ❌ Không |
| IT | ✅ Đọc/Ghi | ❌ Không |

## Lưu Ý

1. Đảm bảo SQL Server đang chạy và cho phép kết nối TCP/IP
2. Kiểm tra firewall nếu kết nối từ xa
3. Mật khẩu trong demo khác với production

## Tác Giả

Đồ án môn CSDL Phân tán - Đề tài 1: Quản lý Lương & Hồ sơ Nhân sự
