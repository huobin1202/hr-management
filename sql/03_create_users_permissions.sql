-- ============================================
-- SCRIPT TẠO USER VÀ PHÂN QUYỀN
-- Demo phân quyền truy cập giữa các CSDL
-- ============================================

USE master;
GO

-- ============================================
-- TẠO LOGIN CHO CÁC VAI TRÒ
-- ============================================

-- Xóa login cũ nếu tồn tại
IF EXISTS (SELECT * FROM sys.server_principals WHERE name = N'admin_login')
    DROP LOGIN admin_login;
IF EXISTS (SELECT * FROM sys.server_principals WHERE name = N'nhanvien_login')
    DROP LOGIN nhanvien_login;
IF EXISTS (SELECT * FROM sys.server_principals WHERE name = N'ketoan_login')
    DROP LOGIN ketoan_login;
IF EXISTS (SELECT * FROM sys.server_principals WHERE name = N'it_login')
    DROP LOGIN it_login;
GO

-- Tạo login mới
CREATE LOGIN admin_login WITH PASSWORD = N'Admin@123456', CHECK_POLICY = OFF;
CREATE LOGIN nhanvien_login WITH PASSWORD = N'NhanVien@123', CHECK_POLICY = OFF;
CREATE LOGIN ketoan_login WITH PASSWORD = N'KeToan@123456', CHECK_POLICY = OFF;
CREATE LOGIN it_login WITH PASSWORD = N'IT@123456789', CHECK_POLICY = OFF;
GO

-- ============================================
-- PHÂN QUYỀN TRÊN DB1 (HR_INFO)
-- ============================================
USE HR_INFO;
GO

-- Xóa user cũ
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = N'admin_user')
    DROP USER admin_user;
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = N'nhanvien_user')
    DROP USER nhanvien_user;
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = N'ketoan_user')
    DROP USER ketoan_user;
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = N'it_user')
    DROP USER it_user;
GO

-- Tạo user từ login
CREATE USER admin_user FOR LOGIN admin_login;
CREATE USER nhanvien_user FOR LOGIN nhanvien_login;
CREATE USER ketoan_user FOR LOGIN ketoan_login;
CREATE USER it_user FOR LOGIN it_login;
GO

-- === ADMIN: Full quyền trên DB1 ===
ALTER ROLE db_owner ADD MEMBER admin_user;
GO

-- === NHÂN VIÊN: Chỉ xem thông tin chung (không có lương) ===
-- Chỉ được SELECT trên view và một số bảng cơ bản
GRANT SELECT ON vw_NhanVien_ThongTinChung TO nhanvien_user;
GRANT SELECT ON PhongBan TO nhanvien_user;
GRANT SELECT ON ChucVu TO nhanvien_user;
-- Không được xem bảng NhanVien trực tiếp để tránh lộ thông tin nhạy cảm
GO

-- === KẾ TOÁN: Quyền hạn chế trên DB1 ===
GRANT SELECT ON vw_NhanVien_ThongTinChung TO ketoan_user;
GRANT SELECT ON PhongBan TO ketoan_user;
GRANT SELECT ON ChucVu TO ketoan_user;
GRANT SELECT ON NhanVien TO ketoan_user; -- Cần để join với bảng lương
GO

-- === IT: Quyền quản trị nhưng KHÔNG được xem lương ===
GRANT SELECT, INSERT, UPDATE, DELETE ON NhanVien TO it_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON PhongBan TO it_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ChucVu TO it_user;
GRANT SELECT ON vw_NhanVien_ThongTinChung TO it_user;
GRANT EXECUTE ON sp_ThemNhanVien_DB1 TO it_user;
GRANT EXECUTE ON sp_XoaNhanVien_DB1 TO it_user;
GRANT EXECUTE ON sp_CapNhatNhanVien_DB1 TO it_user;
-- IT KHÔNG có quyền trên DB2 -> không thể xem lương
GO

-- ============================================
-- PHÂN QUYỀN TRÊN DB2 (HR_SALARY)
-- ============================================
USE HR_SALARY;
GO

-- Xóa user cũ
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = N'admin_user')
    DROP USER admin_user;
IF EXISTS (SELECT * FROM sys.database_principals WHERE name = N'ketoan_user')
    DROP USER ketoan_user;
GO

-- Tạo user - CHỈ admin và kế toán có quyền trên DB2
CREATE USER admin_user FOR LOGIN admin_login;
CREATE USER ketoan_user FOR LOGIN ketoan_login;
GO

-- === ADMIN: Full quyền trên DB2 ===
ALTER ROLE db_owner ADD MEMBER admin_user;
GO

-- === KẾ TOÁN: Full quyền trên DB2 (quản lý lương) ===
GRANT SELECT, INSERT, UPDATE, DELETE ON LuongNhanVien TO ketoan_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON BangLuongThang TO ketoan_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON LichSuLuong TO ketoan_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON Thuong TO ketoan_user;
GRANT SELECT ON vw_BangLuongTongHop TO ketoan_user;
GRANT EXECUTE ON sp_ThemLuongNhanVien TO ketoan_user;
GRANT EXECUTE ON sp_XoaLuongNhanVien TO ketoan_user;
GRANT EXECUTE ON sp_CapNhatLuong TO ketoan_user;
GRANT EXECUTE ON sp_TinhLuongThang TO ketoan_user;
GO

-- ============================================
-- LƯU Ý QUAN TRỌNG:
-- - nhanvien_login: KHÔNG có quyền gì trên DB2 -> không thể xem lương
-- - it_login: KHÔNG có quyền gì trên DB2 -> IT quản trị hệ thống 
--   nhưng không thể xem thông tin lương
-- - Chỉ admin_login và ketoan_login mới truy cập được DB2
-- ============================================

-- ============================================
-- TẠO VIEW TỔNG HỢP TRÊN DB1 CHO ADMIN
-- (Join dữ liệu từ cả 2 DB - chỉ Admin mới dùng được)
-- ============================================
USE HR_INFO;
GO

-- View này sử dụng cross-database query
-- Chỉ hoạt động khi user có quyền trên cả 2 database
CREATE OR ALTER VIEW vw_NhanVien_DayDu AS
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
    -- Thông tin lương từ DB2
    lnv.LuongCoBan,
    lnv.HeSoLuong,
    lnv.PhuCapChucVu,
    lnv.PhuCapKhac,
    (lnv.LuongCoBan * lnv.HeSoLuong + lnv.PhuCapChucVu + lnv.PhuCapKhac) AS TongLuong,
    nv.TrangThai
FROM NhanVien nv
LEFT JOIN PhongBan pb ON nv.MaPB = pb.MaPB
LEFT JOIN ChucVu cv ON nv.MaCV = cv.MaCV
LEFT JOIN HR_SALARY.dbo.LuongNhanVien lnv ON nv.MaNV = lnv.MaNV
WHERE nv.TrangThai = 1;
GO

-- Chỉ Admin mới được xem view đầy đủ này
GRANT SELECT ON vw_NhanVien_DayDu TO admin_user;
-- Từ chối quyền cho các user khác
DENY SELECT ON vw_NhanVien_DayDu TO nhanvien_user;
DENY SELECT ON vw_NhanVien_DayDu TO it_user;
-- Kế toán có thể xem view này (vì họ cũng có quyền trên DB2)
GRANT SELECT ON vw_NhanVien_DayDu TO ketoan_user;
GO

PRINT N'=== Đã tạo Users và phân quyền thành công ===';
PRINT N'';
PRINT N'THÔNG TIN ĐĂNG NHẬP:';
PRINT N'1. Admin    : admin_login / Admin@123456     -> Full quyền cả 2 DB';
PRINT N'2. Nhân viên: nhanvien_login / NhanVien@123  -> Chỉ xem DB1 (thông tin chung)';
PRINT N'3. Kế toán  : ketoan_login / KeToan@123456   -> Xem DB1 + Full quyền DB2 (lương)';
PRINT N'4. IT       : it_login / IT@123456789        -> Quản trị DB1, KHÔNG xem được lương';
GO
