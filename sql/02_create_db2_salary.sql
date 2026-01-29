-- ============================================
-- DB2: CSDL LƯƠNG THƯỞNG (HR_SALARY)
-- Chứa dữ liệu nhạy cảm về tài chính
-- Có cơ chế bảo mật/login riêng biệt
-- ============================================

USE master;
GO

IF EXISTS (SELECT name FROM sys.databases WHERE name = N'HR_SALARY')
    DROP DATABASE HR_SALARY;
GO

CREATE DATABASE HR_SALARY;
GO

USE HR_SALARY;
GO

-- ============================================
-- BẢNG THÔNG TIN LƯƠNG CƠ BẢN
-- ============================================
CREATE TABLE LuongNhanVien (
    MaNV NVARCHAR(10) PRIMARY KEY,
    LuongCoBan DECIMAL(18,2) NOT NULL,
    HeSoLuong DECIMAL(5,2) DEFAULT 1.0,
    PhuCapChucVu DECIMAL(18,2) DEFAULT 0,
    PhuCapKhac DECIMAL(18,2) DEFAULT 0,
    NgayHieuLuc DATE,
    NgayCapNhat DATETIME DEFAULT GETDATE(),
    GhiChu NVARCHAR(500)
);
GO

-- ============================================
-- BẢNG BẢNG LƯƠNG HÀNG THÁNG
-- ============================================
CREATE TABLE BangLuongThang (
    MaBangLuong INT IDENTITY(1,1) PRIMARY KEY,
    MaNV NVARCHAR(10) NOT NULL,
    Thang INT NOT NULL,
    Nam INT NOT NULL,
    SoNgayCong INT DEFAULT 22,
    SoNgayNghiPhep INT DEFAULT 0,
    SoNgayNghiKhongLuong INT DEFAULT 0,
    LuongCoBan DECIMAL(18,2),
    HeSoLuong DECIMAL(5,2),
    PhuCapChucVu DECIMAL(18,2),
    PhuCapKhac DECIMAL(18,2),
    Thuong DECIMAL(18,2) DEFAULT 0,
    KhauTruBHXH DECIMAL(18,2) DEFAULT 0, -- 8%
    KhauTruBHYT DECIMAL(18,2) DEFAULT 0, -- 1.5%
    KhauTruBHTN DECIMAL(18,2) DEFAULT 0, -- 1%
    KhauTruThueTNCN DECIMAL(18,2) DEFAULT 0,
    KhauTruKhac DECIMAL(18,2) DEFAULT 0,
    TongThuNhap DECIMAL(18,2),
    TongKhauTru DECIMAL(18,2),
    LuongThucNhan DECIMAL(18,2),
    TrangThai NVARCHAR(20) DEFAULT N'Chờ duyệt', -- 'Chờ duyệt', 'Đã duyệt', 'Đã thanh toán'
    NgayTao DATETIME DEFAULT GETDATE(),
    NgayDuyet DATETIME,
    NguoiDuyet NVARCHAR(50),
    CONSTRAINT FK_BangLuong_NhanVien FOREIGN KEY (MaNV) REFERENCES LuongNhanVien(MaNV),
    CONSTRAINT UQ_BangLuong_ThangNam UNIQUE (MaNV, Thang, Nam)
);
GO

-- ============================================
-- BẢNG LỊCH SỬ THAY ĐỔI LƯƠNG
-- ============================================
CREATE TABLE LichSuLuong (
    MaLichSu INT IDENTITY(1,1) PRIMARY KEY,
    MaNV NVARCHAR(10) NOT NULL,
    LuongCoBanCu DECIMAL(18,2),
    LuongCoBanMoi DECIMAL(18,2),
    HeSoLuongCu DECIMAL(5,2),
    HeSoLuongMoi DECIMAL(5,2),
    LyDoThayDoi NVARCHAR(500),
    NgayThayDoi DATETIME DEFAULT GETDATE(),
    NguoiThayDoi NVARCHAR(50),
    CONSTRAINT FK_LichSu_NhanVien FOREIGN KEY (MaNV) REFERENCES LuongNhanVien(MaNV)
);
GO

-- ============================================
-- BẢNG THƯỞNG
-- ============================================
CREATE TABLE Thuong (
    MaThuong INT IDENTITY(1,1) PRIMARY KEY,
    MaNV NVARCHAR(10) NOT NULL,
    LoaiThuong NVARCHAR(100),
    SoTien DECIMAL(18,2),
    LyDo NVARCHAR(500),
    NgayThuong DATE,
    Thang INT,
    Nam INT,
    TrangThai NVARCHAR(20) DEFAULT N'Chờ duyệt',
    NgayTao DATETIME DEFAULT GETDATE(),
    CONSTRAINT FK_Thuong_NhanVien FOREIGN KEY (MaNV) REFERENCES LuongNhanVien(MaNV)
);
GO

-- ============================================
-- THÊM DỮ LIỆU MẪU
-- ============================================

-- Thông tin lương cơ bản
INSERT INTO LuongNhanVien (MaNV, LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, NgayHieuLuc, GhiChu) VALUES
(N'NV001', 50000000, 3.5, 10000000, 5000000, '2024-01-01', N'Giám đốc'),
(N'NV002', 25000000, 2.5, 5000000, 2000000, '2024-01-01', N'Trưởng phòng Kế toán'),
(N'NV003', 25000000, 2.5, 5000000, 2000000, '2024-01-01', N'Trưởng phòng IT'),
(N'NV004', 18000000, 2.0, 3000000, 1500000, '2024-01-01', N'Phó phòng'),
(N'NV005', 15000000, 1.8, 0, 1000000, '2024-01-01', N'Nhân viên'),
(N'NV006', 12000000, 1.5, 0, 800000, '2024-01-01', N'Nhân viên'),
(N'NV007', 14000000, 1.6, 0, 900000, '2024-01-01', N'Nhân viên'),
(N'NV008', 16000000, 1.7, 0, 1000000, '2024-01-01', N'Nhân viên'),
(N'NV009', 8000000, 1.0, 0, 500000, '2024-01-01', N'Thực tập sinh'),
(N'NV010', 13000000, 1.5, 0, 800000, '2024-01-01', N'Nhân viên');
GO

-- Bảng lương tháng mẫu (Tháng 1/2026)
INSERT INTO BangLuongThang (MaNV, Thang, Nam, SoNgayCong, LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, Thuong, 
    KhauTruBHXH, KhauTruBHYT, KhauTruBHTN, KhauTruThueTNCN, TongThuNhap, TongKhauTru, LuongThucNhan, TrangThai)
SELECT 
    MaNV, 1, 2026, 22, LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, 0,
    LuongCoBan * 0.08, -- BHXH 8%
    LuongCoBan * 0.015, -- BHYT 1.5%
    LuongCoBan * 0.01, -- BHTN 1%
    CASE 
        WHEN (LuongCoBan * HeSoLuong + PhuCapChucVu + PhuCapKhac) > 20000000 
        THEN ((LuongCoBan * HeSoLuong + PhuCapChucVu + PhuCapKhac) - 11000000) * 0.1
        ELSE 0 
    END, -- Thuế TNCN đơn giản
    LuongCoBan * HeSoLuong + PhuCapChucVu + PhuCapKhac, -- Tổng thu nhập
    LuongCoBan * 0.105, -- Tổng khấu trừ BH
    (LuongCoBan * HeSoLuong + PhuCapChucVu + PhuCapKhac) - LuongCoBan * 0.105 - 
    CASE 
        WHEN (LuongCoBan * HeSoLuong + PhuCapChucVu + PhuCapKhac) > 20000000 
        THEN ((LuongCoBan * HeSoLuong + PhuCapChucVu + PhuCapKhac) - 11000000) * 0.1
        ELSE 0 
    END, -- Lương thực nhận
    N'Đã duyệt'
FROM LuongNhanVien;
GO

-- Thưởng mẫu
INSERT INTO Thuong (MaNV, LoaiThuong, SoTien, LyDo, NgayThuong, Thang, Nam, TrangThai) VALUES
(N'NV001', N'Thưởng Tết', 50000000, N'Thưởng Tết Nguyên Đán 2026', '2026-01-25', 1, 2026, N'Đã duyệt'),
(N'NV002', N'Thưởng Tết', 25000000, N'Thưởng Tết Nguyên Đán 2026', '2026-01-25', 1, 2026, N'Đã duyệt'),
(N'NV003', N'Thưởng Tết', 25000000, N'Thưởng Tết Nguyên Đán 2026', '2026-01-25', 1, 2026, N'Đã duyệt'),
(N'NV005', N'Thưởng KPI', 3000000, N'Hoàn thành KPI Q4/2025', '2026-01-15', 1, 2026, N'Đã duyệt');
GO

-- ============================================
-- STORED PROCEDURE THÊM LƯƠNG NHÂN VIÊN MỚI
-- ============================================
CREATE PROCEDURE sp_ThemLuongNhanVien
    @MaNV NVARCHAR(10),
    @LuongCoBan DECIMAL(18,2),
    @HeSoLuong DECIMAL(5,2) = 1.0,
    @PhuCapChucVu DECIMAL(18,2) = 0,
    @PhuCapKhac DECIMAL(18,2) = 0,
    @GhiChu NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        INSERT INTO LuongNhanVien (MaNV, LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, NgayHieuLuc, GhiChu)
        VALUES (@MaNV, @LuongCoBan, @HeSoLuong, @PhuCapChucVu, @PhuCapKhac, GETDATE(), @GhiChu);
        
        SELECT 1 AS Result, N'Thêm thông tin lương thành công vào DB2' AS Message;
    END TRY
    BEGIN CATCH
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- STORED PROCEDURE XÓA LƯƠNG NHÂN VIÊN
-- ============================================
CREATE PROCEDURE sp_XoaLuongNhanVien
    @MaNV NVARCHAR(10)
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        -- Xóa các bản ghi liên quan trước
        DELETE FROM Thuong WHERE MaNV = @MaNV;
        DELETE FROM BangLuongThang WHERE MaNV = @MaNV;
        DELETE FROM LichSuLuong WHERE MaNV = @MaNV;
        DELETE FROM LuongNhanVien WHERE MaNV = @MaNV;
        
        SELECT 1 AS Result, N'Xóa thông tin lương thành công từ DB2' AS Message;
    END TRY
    BEGIN CATCH
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- STORED PROCEDURE CẬP NHẬT LƯƠNG
-- ============================================
CREATE PROCEDURE sp_CapNhatLuong
    @MaNV NVARCHAR(10),
    @LuongCoBanMoi DECIMAL(18,2),
    @HeSoLuongMoi DECIMAL(5,2),
    @PhuCapChucVu DECIMAL(18,2),
    @PhuCapKhac DECIMAL(18,2),
    @LyDoThayDoi NVARCHAR(500),
    @NguoiThayDoi NVARCHAR(50)
AS
BEGIN
    SET NOCOUNT ON;
    
    BEGIN TRY
        BEGIN TRANSACTION;
        
        -- Lưu lịch sử thay đổi
        INSERT INTO LichSuLuong (MaNV, LuongCoBanCu, LuongCoBanMoi, HeSoLuongCu, HeSoLuongMoi, LyDoThayDoi, NguoiThayDoi)
        SELECT MaNV, LuongCoBan, @LuongCoBanMoi, HeSoLuong, @HeSoLuongMoi, @LyDoThayDoi, @NguoiThayDoi
        FROM LuongNhanVien WHERE MaNV = @MaNV;
        
        -- Cập nhật lương mới
        UPDATE LuongNhanVien 
        SET LuongCoBan = @LuongCoBanMoi,
            HeSoLuong = @HeSoLuongMoi,
            PhuCapChucVu = @PhuCapChucVu,
            PhuCapKhac = @PhuCapKhac,
            NgayCapNhat = GETDATE()
        WHERE MaNV = @MaNV;
        
        COMMIT TRANSACTION;
        SELECT 1 AS Result, N'Cập nhật lương thành công' AS Message;
    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- VIEW BẢNG LƯƠNG TỔNG HỢP
-- ============================================
CREATE VIEW vw_BangLuongTongHop AS
SELECT 
    bl.MaBangLuong,
    bl.MaNV,
    bl.Thang,
    bl.Nam,
    bl.SoNgayCong,
    bl.SoNgayNghiPhep,
    bl.LuongCoBan,
    bl.HeSoLuong,
    bl.PhuCapChucVu,
    bl.PhuCapKhac,
    bl.Thuong,
    bl.TongThuNhap,
    bl.KhauTruBHXH,
    bl.KhauTruBHYT,
    bl.KhauTruBHTN,
    bl.KhauTruThueTNCN,
    bl.TongKhauTru,
    bl.LuongThucNhan,
    bl.TrangThai,
    bl.NgayDuyet,
    bl.NguoiDuyet
FROM BangLuongThang bl;
GO

-- ============================================
-- STORED PROCEDURE TÍNH LƯƠNG THÁNG
-- ============================================
CREATE PROCEDURE sp_TinhLuongThang
    @MaNV NVARCHAR(10),
    @Thang INT,
    @Nam INT,
    @SoNgayCong INT = 22,
    @SoNgayNghiPhep INT = 0,
    @SoNgayNghiKhongLuong INT = 0,
    @Thuong DECIMAL(18,2) = 0
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @LuongCoBan DECIMAL(18,2), @HeSoLuong DECIMAL(5,2);
    DECLARE @PhuCapChucVu DECIMAL(18,2), @PhuCapKhac DECIMAL(18,2);
    DECLARE @TongThuNhap DECIMAL(18,2), @TongKhauTru DECIMAL(18,2);
    DECLARE @BHXH DECIMAL(18,2), @BHYT DECIMAL(18,2), @BHTN DECIMAL(18,2), @ThueTNCN DECIMAL(18,2);
    
    BEGIN TRY
        -- Lấy thông tin lương
        SELECT @LuongCoBan = LuongCoBan, @HeSoLuong = HeSoLuong,
               @PhuCapChucVu = PhuCapChucVu, @PhuCapKhac = PhuCapKhac
        FROM LuongNhanVien WHERE MaNV = @MaNV;
        
        IF @LuongCoBan IS NULL
        BEGIN
            SELECT 0 AS Result, N'Không tìm thấy thông tin lương của nhân viên' AS Message;
            RETURN;
        END
        
        -- Tính lương theo ngày công
        DECLARE @TyLeCong DECIMAL(5,2) = CAST(@SoNgayCong - @SoNgayNghiKhongLuong AS DECIMAL(5,2)) / 22.0;
        SET @TongThuNhap = (@LuongCoBan * @HeSoLuong + @PhuCapChucVu + @PhuCapKhac) * @TyLeCong + @Thuong;
        
        -- Tính các khoản khấu trừ
        SET @BHXH = @LuongCoBan * 0.08;
        SET @BHYT = @LuongCoBan * 0.015;
        SET @BHTN = @LuongCoBan * 0.01;
        
        -- Thuế TNCN đơn giản (giảm trừ gia cảnh 11 triệu)
        DECLARE @ThuNhapChiuThue DECIMAL(18,2) = @TongThuNhap - @BHXH - @BHYT - @BHTN - 11000000;
        IF @ThuNhapChiuThue > 0
            SET @ThueTNCN = @ThuNhapChiuThue * 0.1; -- Thuế suất 10% đơn giản hóa
        ELSE
            SET @ThueTNCN = 0;
        
        SET @TongKhauTru = @BHXH + @BHYT + @BHTN + @ThueTNCN;
        
        -- Kiểm tra đã tồn tại chưa
        IF EXISTS (SELECT 1 FROM BangLuongThang WHERE MaNV = @MaNV AND Thang = @Thang AND Nam = @Nam)
        BEGIN
            -- Cập nhật
            UPDATE BangLuongThang
            SET SoNgayCong = @SoNgayCong,
                SoNgayNghiPhep = @SoNgayNghiPhep,
                SoNgayNghiKhongLuong = @SoNgayNghiKhongLuong,
                LuongCoBan = @LuongCoBan,
                HeSoLuong = @HeSoLuong,
                PhuCapChucVu = @PhuCapChucVu,
                PhuCapKhac = @PhuCapKhac,
                Thuong = @Thuong,
                KhauTruBHXH = @BHXH,
                KhauTruBHYT = @BHYT,
                KhauTruBHTN = @BHTN,
                KhauTruThueTNCN = @ThueTNCN,
                TongThuNhap = @TongThuNhap,
                TongKhauTru = @TongKhauTru,
                LuongThucNhan = @TongThuNhap - @TongKhauTru,
                TrangThai = N'Chờ duyệt'
            WHERE MaNV = @MaNV AND Thang = @Thang AND Nam = @Nam;
        END
        ELSE
        BEGIN
            -- Thêm mới
            INSERT INTO BangLuongThang (MaNV, Thang, Nam, SoNgayCong, SoNgayNghiPhep, SoNgayNghiKhongLuong,
                LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, Thuong,
                KhauTruBHXH, KhauTruBHYT, KhauTruBHTN, KhauTruThueTNCN,
                TongThuNhap, TongKhauTru, LuongThucNhan)
            VALUES (@MaNV, @Thang, @Nam, @SoNgayCong, @SoNgayNghiPhep, @SoNgayNghiKhongLuong,
                @LuongCoBan, @HeSoLuong, @PhuCapChucVu, @PhuCapKhac, @Thuong,
                @BHXH, @BHYT, @BHTN, @ThueTNCN,
                @TongThuNhap, @TongKhauTru, @TongThuNhap - @TongKhauTru);
        END
        
        SELECT 1 AS Result, N'Tính lương thành công' AS Message, @TongThuNhap - @TongKhauTru AS LuongThucNhan;
    END TRY
    BEGIN CATCH
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

PRINT N'=== DB2 (HR_SALARY) đã được tạo thành công ===';
GO
