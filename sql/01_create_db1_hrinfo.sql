-- ============================================
-- DB1: CSDL THÔNG TIN CHUNG NHÂN SỰ (HR_INFO)
-- Chứa dữ liệu công khai, không nhạy cảm
-- ============================================

-- Tạo Database DB1
USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'HR_INFO')
    DROP DATABASE HR_INFO;
GO

CREATE DATABASE HR_INFO;
GO

USE HR_INFO;
GO

-- ============================================
-- BẢNG PHÒNG BAN
-- ============================================
CREATE TABLE PhongBan (
    MaPB NVARCHAR(10) PRIMARY KEY,
    TenPB NVARCHAR(100) NOT NULL,
    DiaChi NVARCHAR(200),
    SoDienThoai NVARCHAR(20),
    NgayThanhLap DATE,
    TrangThai BIT DEFAULT 1
);
GO

-- ============================================
-- BẢNG CHỨC VỤ
-- ============================================
CREATE TABLE ChucVu (
    MaCV NVARCHAR(10) PRIMARY KEY,
    TenCV NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(500),
    TrangThai BIT DEFAULT 1
);
GO

-- ============================================
-- BẢNG NHÂN VIÊN (THÔNG TIN CHUNG)
-- ============================================
CREATE TABLE NhanVien (
    MaNV NVARCHAR(10) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE,
    GioiTinh NVARCHAR(10),
    CMND NVARCHAR(20),
    DiaChi NVARCHAR(200),
    SoDienThoai NVARCHAR(20),
    Email NVARCHAR(100),
    MaPB NVARCHAR(10),
    MaCV NVARCHAR(10),
    NgayVaoLam DATE,
    TrangThai BIT DEFAULT 1,
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayCapNhat DATETIME,
    CONSTRAINT FK_NhanVien_PhongBan FOREIGN KEY (MaPB) REFERENCES PhongBan(MaPB),
    CONSTRAINT FK_NhanVien_ChucVu FOREIGN KEY (MaCV) REFERENCES ChucVu(MaCV)
);
GO

-- ============================================
-- BẢNG NGƯỜI DÙNG HỆ THỐNG
-- ============================================
CREATE TABLE NguoiDung (
    MaND INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap NVARCHAR(50) UNIQUE NOT NULL,
    MatKhau NVARCHAR(255) NOT NULL, -- Nên hash password
    MaNV NVARCHAR(10),
    VaiTro NVARCHAR(20) NOT NULL, -- 'ADMIN', 'NHANVIEN', 'KETOAN'
    TrangThai BIT DEFAULT 1,
    NgayTao DATETIME DEFAULT GETDATE(),
    LanDangNhapCuoi DATETIME,
    CONSTRAINT FK_NguoiDung_NhanVien FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV)
);
GO

-- ============================================
-- BẢNG LOG HOẠT ĐỘNG
-- ============================================
CREATE TABLE LogHoatDong (
    MaLog INT IDENTITY(1,1) PRIMARY KEY,
    MaND INT,
    HanhDong NVARCHAR(100),
    MoTa NVARCHAR(500),
    ThoiGian DATETIME DEFAULT GETDATE(),
    DiaChi_IP NVARCHAR(50),
    CONSTRAINT FK_Log_NguoiDung FOREIGN KEY (MaND) REFERENCES NguoiDung(MaND)
);
GO

-- ============================================
-- THÊM DỮ LIỆU MẪU
-- ============================================

-- Phòng ban
INSERT INTO PhongBan (MaPB, TenPB, DiaChi, SoDienThoai, NgayThanhLap) VALUES
(N'PB001', N'Phòng Nhân sự', N'Tầng 1, Tòa nhà A', N'028-1234567', '2020-01-01'),
(N'PB002', N'Phòng Kế toán', N'Tầng 2, Tòa nhà A', N'028-1234568', '2020-01-01'),
(N'PB003', N'Phòng IT', N'Tầng 3, Tòa nhà B', N'028-1234569', '2020-06-01'),
(N'PB004', N'Phòng Kinh doanh', N'Tầng 4, Tòa nhà B', N'028-1234570', '2020-06-01'),
(N'PB005', N'Phòng Marketing', N'Tầng 5, Tòa nhà C', N'028-1234571', '2021-01-01');
GO

-- Chức vụ
INSERT INTO ChucVu (MaCV, TenCV, MoTa) VALUES
(N'CV001', N'Giám đốc', N'Quản lý toàn bộ công ty'),
(N'CV002', N'Trưởng phòng', N'Quản lý phòng ban'),
(N'CV003', N'Phó phòng', N'Hỗ trợ trưởng phòng'),
(N'CV004', N'Nhân viên', N'Nhân viên thường'),
(N'CV005', N'Thực tập sinh', N'Nhân viên thực tập');
GO

-- Nhân viên
INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, CMND, DiaChi, SoDienThoai, Email, MaPB, MaCV, NgayVaoLam) VALUES
(N'NV001', N'Nguyễn Văn An', '1985-05-15', N'Nam', N'079185001234', N'123 Nguyễn Huệ, Q1, TP.HCM', N'0901234567', N'an.nguyen@company.com', N'PB001', N'CV001', '2020-01-01'),
(N'NV002', N'Trần Thị Bình', '1990-08-20', N'Nữ', N'079190002345', N'456 Lê Lợi, Q1, TP.HCM', N'0902345678', N'binh.tran@company.com', N'PB002', N'CV002', '2020-02-01'),
(N'NV003', N'Lê Văn Cường', '1992-03-10', N'Nam', N'079192003456', N'789 Hai Bà Trưng, Q3, TP.HCM', N'0903456789', N'cuong.le@company.com', N'PB003', N'CV002', '2020-03-01'),
(N'NV004', N'Phạm Thị Dung', '1995-12-25', N'Nữ', N'079195004567', N'321 Võ Văn Tần, Q3, TP.HCM', N'0904567890', N'dung.pham@company.com', N'PB004', N'CV003', '2020-06-01'),
(N'NV005', N'Hoàng Văn Em', '1993-07-30', N'Nam', N'079193005678', N'654 Pasteur, Q1, TP.HCM', N'0905678901', N'em.hoang@company.com', N'PB005', N'CV004', '2021-01-01'),
(N'NV006', N'Ngô Thị Phương', '1998-01-18', N'Nữ', N'079198006789', N'987 CMT8, Q10, TP.HCM', N'0906789012', N'phuong.ngo@company.com', N'PB001', N'CV004', '2021-06-01'),
(N'NV007', N'Đỗ Văn Giang', '1988-09-05', N'Nam', N'079188007890', N'147 Điện Biên Phủ, Q.Bình Thạnh', N'0907890123', N'giang.do@company.com', N'PB002', N'CV004', '2022-01-01'),
(N'NV008', N'Vũ Thị Hương', '1996-04-12', N'Nữ', N'079196008901', N'258 Xô Viết Nghệ Tĩnh, Q.Bình Thạnh', N'0908901234', N'huong.vu@company.com', N'PB003', N'CV004', '2022-06-01'),
(N'NV009', N'Bùi Văn Inh', '2000-11-22', N'Nam', N'079200009012', N'369 Nguyễn Thị Minh Khai, Q1', N'0909012345', N'inh.bui@company.com', N'PB004', N'CV005', '2023-01-01'),
(N'NV010', N'Lý Thị Kim', '1997-06-08', N'Nữ', N'079197010123', N'741 Trần Hưng Đạo, Q5', N'0910123456', N'kim.ly@company.com', N'PB005', N'CV004', '2023-06-01');
GO

-- Người dùng hệ thống
-- Mật khẩu: admin123, nhanvien123, ketoan123 (cần hash trong thực tế)
INSERT INTO NguoiDung (TenDangNhap, MatKhau, MaNV, VaiTro) VALUES
(N'admin', N'admin123', N'NV001', N'ADMIN'),
(N'nhanvien', N'nhanvien123', N'NV006', N'NHANVIEN'),
(N'ketoan', N'ketoan123', N'NV002', N'KETOAN'),
(N'it_user', N'it123', N'NV003', N'NHANVIEN');
GO

-- ============================================
-- TẠO LINKED SERVER ĐẾN DB2 (HR_SALARY)
-- Cần chạy sau khi tạo DB2
-- ============================================
-- Lưu ý: Trong môi trường thực tế, DB2 sẽ ở server khác
-- Ở đây demo trên cùng server với login riêng

-- ============================================
-- VIEW TỔNG HỢP NHÂN VIÊN (CHỈ THÔNG TIN CHUNG)
-- Dành cho user không có quyền xem lương
-- ============================================
CREATE VIEW vw_NhanVien_ThongTinChung AS
SELECT 
    nv.MaNV,
    nv.HoTen,
    nv.NgaySinh,
    nv.GioiTinh,
    nv.CMND,
    nv.DiaChi,
    nv.SoDienThoai,
    nv.Email,
    pb.TenPB AS PhongBan,
    cv.TenCV AS ChucVu,
    nv.NgayVaoLam,
    DATEDIFF(YEAR, nv.NgayVaoLam, GETDATE()) AS SoNamLamViec,
    nv.TrangThai
FROM NhanVien nv
LEFT JOIN PhongBan pb ON nv.MaPB = pb.MaPB
LEFT JOIN ChucVu cv ON nv.MaCV = cv.MaCV
WHERE nv.TrangThai = 1;
GO

-- ============================================
-- STORED PROCEDURE THÊM NHÂN VIÊN
-- Chỉ thêm thông tin chung vào DB1
-- ============================================
CREATE PROCEDURE sp_ThemNhanVien_DB1
    @MaNV NVARCHAR(10),
    @HoTen NVARCHAR(100),
    @NgaySinh DATE,
    @GioiTinh NVARCHAR(10),
    @CMND NVARCHAR(20),
    @DiaChi NVARCHAR(200),
    @SoDienThoai NVARCHAR(20),
    @Email NVARCHAR(100),
    @MaPB NVARCHAR(10),
    @MaCV NVARCHAR(10),
    @NgayVaoLam DATE
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, CMND, DiaChi, SoDienThoai, Email, MaPB, MaCV, NgayVaoLam)
        VALUES (@MaNV, @HoTen, @NgaySinh, @GioiTinh, @CMND, @DiaChi, @SoDienThoai, @Email, @MaPB, @MaCV, @NgayVaoLam);
        
        SELECT 1 AS Result, N'Thêm nhân viên thành công vào DB1' AS Message;
    END TRY
    BEGIN CATCH
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- STORED PROCEDURE XÓA NHÂN VIÊN
-- Soft delete - chỉ cập nhật trạng thái
-- ============================================
CREATE PROCEDURE sp_XoaNhanVien_DB1
    @MaNV NVARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        UPDATE NhanVien SET TrangThai = 0, NgayCapNhat = GETDATE() WHERE MaNV = @MaNV;
        SELECT 1 AS Result, N'Xóa nhân viên thành công từ DB1' AS Message;
    END TRY
    BEGIN CATCH
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- STORED PROCEDURE CẬP NHẬT NHÂN VIÊN
-- ============================================
CREATE PROCEDURE sp_CapNhatNhanVien_DB1
    @MaNV NVARCHAR(10),
    @HoTen NVARCHAR(100),
    @NgaySinh DATE,
    @GioiTinh NVARCHAR(10),
    @CMND NVARCHAR(20),
    @DiaChi NVARCHAR(200),
    @SoDienThoai NVARCHAR(20),
    @Email NVARCHAR(100),
    @MaPB NVARCHAR(10),
    @MaCV NVARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        UPDATE NhanVien 
        SET HoTen = @HoTen,
            NgaySinh = @NgaySinh,
            GioiTinh = @GioiTinh,
            CMND = @CMND,
            DiaChi = @DiaChi,
            SoDienThoai = @SoDienThoai,
            Email = @Email,
            MaPB = @MaPB,
            MaCV = @MaCV,
            NgayCapNhat = GETDATE()
        WHERE MaNV = @MaNV;
        
        SELECT 1 AS Result, N'Cập nhật nhân viên thành công' AS Message;
    END TRY
    BEGIN CATCH
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- STORED PROCEDURE GHI LOG
-- ============================================
CREATE PROCEDURE sp_GhiLog
    @MaND INT,
    @HanhDong NVARCHAR(100),
    @MoTa NVARCHAR(500),
    @DiaChi_IP NVARCHAR(50) = NULL
AS
BEGIN
    INSERT INTO LogHoatDong (MaND, HanhDong, MoTa, DiaChi_IP)
    VALUES (@MaND, @HanhDong, @MoTa, @DiaChi_IP);
END;
GO

PRINT N'=== DB1 (HR_INFO) đã được tạo thành công ===';
GO
