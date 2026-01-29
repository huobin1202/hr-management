-- ============================================
-- STORED PROCEDURES CHO THAO TÁC PHÂN TÁN
-- Đảm bảo tính toàn vẹn dữ liệu giữa 2 CSDL
-- ============================================

USE HR_INFO;
GO

-- ============================================
-- SP THÊM NHÂN VIÊN MỚI (DISTRIBUTED)
-- Thêm vào cả DB1 và DB2 trong 1 transaction
-- ============================================
CREATE OR ALTER PROCEDURE sp_ThemNhanVien_Distributed
    -- Thông tin chung (DB1)
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
    @NgayVaoLam DATE,
    -- Thông tin lương (DB2)
    @LuongCoBan DECIMAL(18,2),
    @HeSoLuong DECIMAL(5,2) = 1.0,
    @PhuCapChucVu DECIMAL(18,2) = 0,
    @PhuCapKhac DECIMAL(18,2) = 0,
    @GhiChuLuong NVARCHAR(500) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON; -- Tự động rollback khi có lỗi
    
    BEGIN TRY
        BEGIN DISTRIBUTED TRANSACTION;
        
        -- 1. Thêm vào DB1 (Thông tin chung)
        INSERT INTO HR_INFO.dbo.NhanVien 
            (MaNV, HoTen, NgaySinh, GioiTinh, CMND, DiaChi, SoDienThoai, Email, MaPB, MaCV, NgayVaoLam)
        VALUES 
            (@MaNV, @HoTen, @NgaySinh, @GioiTinh, @CMND, @DiaChi, @SoDienThoai, @Email, @MaPB, @MaCV, @NgayVaoLam);
        
        -- 2. Thêm vào DB2 (Thông tin lương)
        INSERT INTO HR_SALARY.dbo.LuongNhanVien 
            (MaNV, LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, NgayHieuLuc, GhiChu)
        VALUES 
            (@MaNV, @LuongCoBan, @HeSoLuong, @PhuCapChucVu, @PhuCapKhac, GETDATE(), @GhiChuLuong);
        
        COMMIT TRANSACTION;
        
        SELECT 1 AS Result, N'Thêm nhân viên thành công vào cả 2 CSDL' AS Message;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        SELECT 0 AS Result, 
               N'Lỗi: ' + ERROR_MESSAGE() + N' (Dòng: ' + CAST(ERROR_LINE() AS NVARCHAR) + N')' AS Message;
    END CATCH
END;
GO

-- ============================================
-- SP XÓA NHÂN VIÊN (DISTRIBUTED)
-- Xóa từ cả DB1 và DB2 trong 1 transaction
-- ============================================
CREATE OR ALTER PROCEDURE sp_XoaNhanVien_Distributed
    @MaNV NVARCHAR(10),
    @XoaVinhVien BIT = 0 -- 0: Soft delete, 1: Hard delete
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    
    BEGIN TRY
        BEGIN DISTRIBUTED TRANSACTION;
        
        IF @XoaVinhVien = 1
        BEGIN
            -- Hard delete: Xóa hoàn toàn
            -- Xóa từ DB2 trước (do không có FK constraint)
            DELETE FROM HR_SALARY.dbo.Thuong WHERE MaNV = @MaNV;
            DELETE FROM HR_SALARY.dbo.BangLuongThang WHERE MaNV = @MaNV;
            DELETE FROM HR_SALARY.dbo.LichSuLuong WHERE MaNV = @MaNV;
            DELETE FROM HR_SALARY.dbo.LuongNhanVien WHERE MaNV = @MaNV;
            
            -- Xóa từ DB1
            DELETE FROM HR_INFO.dbo.NguoiDung WHERE MaNV = @MaNV;
            DELETE FROM HR_INFO.dbo.NhanVien WHERE MaNV = @MaNV;
        END
        ELSE
        BEGIN
            -- Soft delete: Chỉ cập nhật trạng thái
            UPDATE HR_INFO.dbo.NhanVien 
            SET TrangThai = 0, NgayCapNhat = GETDATE() 
            WHERE MaNV = @MaNV;
            
            -- Đánh dấu tài khoản không hoạt động
            UPDATE HR_INFO.dbo.NguoiDung 
            SET TrangThai = 0 
            WHERE MaNV = @MaNV;
        END
        
        COMMIT TRANSACTION;
        
        SELECT 1 AS Result, 
               CASE WHEN @XoaVinhVien = 1 
                    THEN N'Xóa vĩnh viễn nhân viên thành công' 
                    ELSE N'Vô hiệu hóa nhân viên thành công' 
               END AS Message;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        SELECT 0 AS Result, 
               N'Lỗi: ' + ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- SP CẬP NHẬT NHÂN VIÊN (DISTRIBUTED)
-- Cập nhật cả thông tin chung và lương
-- ============================================
CREATE OR ALTER PROCEDURE sp_CapNhatNhanVien_Distributed
    -- Thông tin chung (DB1)
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
    -- Thông tin lương (DB2)
    @LuongCoBan DECIMAL(18,2),
    @HeSoLuong DECIMAL(5,2),
    @PhuCapChucVu DECIMAL(18,2),
    @PhuCapKhac DECIMAL(18,2),
    @LyDoThayDoiLuong NVARCHAR(500) = NULL,
    @NguoiCapNhat NVARCHAR(50) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    
    BEGIN TRY
        BEGIN DISTRIBUTED TRANSACTION;
        
        -- 1. Cập nhật DB1 (Thông tin chung)
        UPDATE HR_INFO.dbo.NhanVien 
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
        
        -- 2. Lưu lịch sử thay đổi lương (DB2)
        IF @LyDoThayDoiLuong IS NOT NULL
        BEGIN
            INSERT INTO HR_SALARY.dbo.LichSuLuong 
                (MaNV, LuongCoBanCu, LuongCoBanMoi, HeSoLuongCu, HeSoLuongMoi, LyDoThayDoi, NguoiThayDoi)
            SELECT MaNV, LuongCoBan, @LuongCoBan, HeSoLuong, @HeSoLuong, @LyDoThayDoiLuong, @NguoiCapNhat
            FROM HR_SALARY.dbo.LuongNhanVien 
            WHERE MaNV = @MaNV;
        END
        
        -- 3. Cập nhật DB2 (Thông tin lương)
        UPDATE HR_SALARY.dbo.LuongNhanVien 
        SET LuongCoBan = @LuongCoBan,
            HeSoLuong = @HeSoLuong,
            PhuCapChucVu = @PhuCapChucVu,
            PhuCapKhac = @PhuCapKhac,
            NgayCapNhat = GETDATE()
        WHERE MaNV = @MaNV;
        
        COMMIT TRANSACTION;
        
        SELECT 1 AS Result, N'Cập nhật nhân viên thành công' AS Message;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        SELECT 0 AS Result, 
               N'Lỗi: ' + ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- ============================================
-- SP KIỂM TRA TÍNH TOÀN VẸN DỮ LIỆU
-- Phát hiện bất đồng bộ giữa 2 CSDL
-- ============================================
CREATE OR ALTER PROCEDURE sp_KiemTraToanVen
AS
BEGIN
    SET NOCOUNT ON;
    
    -- Tìm nhân viên có trong DB1 nhưng không có trong DB2
    SELECT 'Có trong DB1, thiếu trong DB2' AS VanDe, nv.MaNV, nv.HoTen
    FROM HR_INFO.dbo.NhanVien nv
    LEFT JOIN HR_SALARY.dbo.LuongNhanVien lnv ON nv.MaNV = lnv.MaNV
    WHERE lnv.MaNV IS NULL AND nv.TrangThai = 1
    
    UNION ALL
    
    -- Tìm nhân viên có trong DB2 nhưng không có trong DB1
    SELECT 'Có trong DB2, thiếu trong DB1' AS VanDe, lnv.MaNV, NULL AS HoTen
    FROM HR_SALARY.dbo.LuongNhanVien lnv
    LEFT JOIN HR_INFO.dbo.NhanVien nv ON lnv.MaNV = nv.MaNV
    WHERE nv.MaNV IS NULL;
    
    -- Trả về kết quả tổng hợp
    DECLARE @SoLoi INT;
    SELECT @SoLoi = COUNT(*)
    FROM (
        SELECT nv.MaNV
        FROM HR_INFO.dbo.NhanVien nv
        LEFT JOIN HR_SALARY.dbo.LuongNhanVien lnv ON nv.MaNV = lnv.MaNV
        WHERE lnv.MaNV IS NULL AND nv.TrangThai = 1
        
        UNION ALL
        
        SELECT lnv.MaNV
        FROM HR_SALARY.dbo.LuongNhanVien lnv
        LEFT JOIN HR_INFO.dbo.NhanVien nv ON lnv.MaNV = nv.MaNV
        WHERE nv.MaNV IS NULL
    ) AS Loi;
    
    IF @SoLoi = 0
        SELECT 1 AS Result, N'Dữ liệu đồng bộ giữa 2 CSDL' AS Message;
    ELSE
        SELECT 0 AS Result, N'Phát hiện ' + CAST(@SoLoi AS NVARCHAR) + N' bản ghi không đồng bộ' AS Message;
END;
GO

-- ============================================
-- SP SỬA LỖI ĐỒNG BỘ
-- Tự động sửa các bản ghi không đồng bộ
-- ============================================
CREATE OR ALTER PROCEDURE sp_SuaLoiDongBo
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    
    BEGIN TRY
        BEGIN DISTRIBUTED TRANSACTION;
        
        -- Thêm bản ghi lương mặc định cho NV có trong DB1 nhưng thiếu trong DB2
        INSERT INTO HR_SALARY.dbo.LuongNhanVien (MaNV, LuongCoBan, HeSoLuong, NgayHieuLuc, GhiChu)
        SELECT nv.MaNV, 10000000, 1.0, GETDATE(), N'Tự động tạo khi sửa lỗi đồng bộ'
        FROM HR_INFO.dbo.NhanVien nv
        LEFT JOIN HR_SALARY.dbo.LuongNhanVien lnv ON nv.MaNV = lnv.MaNV
        WHERE lnv.MaNV IS NULL AND nv.TrangThai = 1;
        
        -- Xóa bản ghi lương mồ côi (có trong DB2 nhưng không có trong DB1)
        DELETE lnv
        FROM HR_SALARY.dbo.LuongNhanVien lnv
        LEFT JOIN HR_INFO.dbo.NhanVien nv ON lnv.MaNV = nv.MaNV
        WHERE nv.MaNV IS NULL;
        
        COMMIT TRANSACTION;
        
        SELECT 1 AS Result, N'Đã sửa lỗi đồng bộ thành công' AS Message;
    END TRY
    BEGIN CATCH
        IF @@TRANCOUNT > 0
            ROLLBACK TRANSACTION;
        
        SELECT 0 AS Result, ERROR_MESSAGE() AS Message;
    END CATCH
END;
GO

-- Cấp quyền cho Admin
GRANT EXECUTE ON sp_ThemNhanVien_Distributed TO admin_user;
GRANT EXECUTE ON sp_XoaNhanVien_Distributed TO admin_user;
GRANT EXECUTE ON sp_CapNhatNhanVien_Distributed TO admin_user;
GRANT EXECUTE ON sp_KiemTraToanVen TO admin_user;
GRANT EXECUTE ON sp_SuaLoiDongBo TO admin_user;
GO

PRINT N'=== Đã tạo Stored Procedures cho thao tác phân tán ===';
GO
