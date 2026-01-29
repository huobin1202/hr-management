package com.hrms.dao;

import com.hrms.model.NhanVien;
import com.hrms.model.PhongBan;
import com.hrms.model.ChucVu;
import com.hrms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class cho các thao tác trên DB1 (HR_INFO)
 * Quản lý thông tin chung của nhân viên
 */
public class NhanVienDAO {
    
    /**
     * Lấy danh sách tất cả nhân viên đang hoạt động
     */
    public List<NhanVien> getAllNhanVien() throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.*, pb.TenPB, cv.TenCV " +
                    "FROM NhanVien nv " +
                    "LEFT JOIN PhongBan pb ON nv.MaPB = pb.MaPB " +
                    "LEFT JOIN ChucVu cv ON nv.MaCV = cv.MaCV " +
                    "WHERE nv.TrangThai = 1 " +
                    "ORDER BY nv.MaNV";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToNhanVien(rs));
            }
        }
        return list;
    }
    
    /**
     * Tìm nhân viên theo mã
     */
    public NhanVien findByMaNV(String maNV) throws SQLException {
        String sql = "SELECT nv.*, pb.TenPB, cv.TenCV " +
                    "FROM NhanVien nv " +
                    "LEFT JOIN PhongBan pb ON nv.MaPB = pb.MaPB " +
                    "LEFT JOIN ChucVu cv ON nv.MaCV = cv.MaCV " +
                    "WHERE nv.MaNV = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNhanVien(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Tìm kiếm nhân viên theo tên
     */
    public List<NhanVien> searchByName(String keyword) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.*, pb.TenPB, cv.TenCV " +
                    "FROM NhanVien nv " +
                    "LEFT JOIN PhongBan pb ON nv.MaPB = pb.MaPB " +
                    "LEFT JOIN ChucVu cv ON nv.MaCV = cv.MaCV " +
                    "WHERE nv.TrangThai = 1 AND nv.HoTen LIKE ? " +
                    "ORDER BY nv.HoTen";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanVien(rs));
                }
            }
        }
        return list;
    }
    
    /**
     * Lấy nhân viên theo phòng ban
     */
    public List<NhanVien> getByPhongBan(String maPB) throws SQLException {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT nv.*, pb.TenPB, cv.TenCV " +
                    "FROM NhanVien nv " +
                    "LEFT JOIN PhongBan pb ON nv.MaPB = pb.MaPB " +
                    "LEFT JOIN ChucVu cv ON nv.MaCV = cv.MaCV " +
                    "WHERE nv.TrangThai = 1 AND nv.MaPB = ? " +
                    "ORDER BY nv.HoTen";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maPB);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToNhanVien(rs));
                }
            }
        }
        return list;
    }
    
    /**
     * Thêm nhân viên mới vào DB1
     */
    public boolean insert(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, CMND, DiaChi, " +
                    "SoDienThoai, Email, MaPB, MaCV, NgayVaoLam) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getHoTen());
            stmt.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(4, nv.getGioiTinh());
            stmt.setString(5, nv.getCmnd());
            stmt.setString(6, nv.getDiaChi());
            stmt.setString(7, nv.getSoDienThoai());
            stmt.setString(8, nv.getEmail());
            stmt.setString(9, nv.getMaPB());
            stmt.setString(10, nv.getMaCV());
            stmt.setDate(11, nv.getNgayVaoLam() != null ? Date.valueOf(nv.getNgayVaoLam()) : null);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Thêm nhân viên sử dụng connection từ transaction manager
     */
    public boolean insert(NhanVien nv, Connection conn) throws SQLException {
        String sql = "INSERT INTO NhanVien (MaNV, HoTen, NgaySinh, GioiTinh, CMND, DiaChi, " +
                    "SoDienThoai, Email, MaPB, MaCV, NgayVaoLam) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getMaNV());
            stmt.setString(2, nv.getHoTen());
            stmt.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(4, nv.getGioiTinh());
            stmt.setString(5, nv.getCmnd());
            stmt.setString(6, nv.getDiaChi());
            stmt.setString(7, nv.getSoDienThoai());
            stmt.setString(8, nv.getEmail());
            stmt.setString(9, nv.getMaPB());
            stmt.setString(10, nv.getMaCV());
            stmt.setDate(11, nv.getNgayVaoLam() != null ? Date.valueOf(nv.getNgayVaoLam()) : null);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật thông tin nhân viên
     */
    public boolean update(NhanVien nv) throws SQLException {
        String sql = "UPDATE NhanVien SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, CMND = ?, " +
                    "DiaChi = ?, SoDienThoai = ?, Email = ?, MaPB = ?, MaCV = ?, NgayCapNhat = GETDATE() " +
                    "WHERE MaNV = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nv.getHoTen());
            stmt.setDate(2, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(3, nv.getGioiTinh());
            stmt.setString(4, nv.getCmnd());
            stmt.setString(5, nv.getDiaChi());
            stmt.setString(6, nv.getSoDienThoai());
            stmt.setString(7, nv.getEmail());
            stmt.setString(8, nv.getMaPB());
            stmt.setString(9, nv.getMaCV());
            stmt.setString(10, nv.getMaNV());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật nhân viên sử dụng connection từ transaction
     */
    public boolean update(NhanVien nv, Connection conn) throws SQLException {
        String sql = "UPDATE NhanVien SET HoTen = ?, NgaySinh = ?, GioiTinh = ?, CMND = ?, " +
                    "DiaChi = ?, SoDienThoai = ?, Email = ?, MaPB = ?, MaCV = ?, NgayCapNhat = GETDATE() " +
                    "WHERE MaNV = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nv.getHoTen());
            stmt.setDate(2, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
            stmt.setString(3, nv.getGioiTinh());
            stmt.setString(4, nv.getCmnd());
            stmt.setString(5, nv.getDiaChi());
            stmt.setString(6, nv.getSoDienThoai());
            stmt.setString(7, nv.getEmail());
            stmt.setString(8, nv.getMaPB());
            stmt.setString(9, nv.getMaCV());
            stmt.setString(10, nv.getMaNV());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa mềm nhân viên (cập nhật trạng thái)
     */
    public boolean softDelete(String maNV) throws SQLException {
        String sql = "UPDATE NhanVien SET TrangThai = 0, NgayCapNhat = GETDATE() WHERE MaNV = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maNV);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa mềm sử dụng connection từ transaction
     */
    public boolean softDelete(String maNV, Connection conn) throws SQLException {
        String sql = "UPDATE NhanVien SET TrangThai = 0, NgayCapNhat = GETDATE() WHERE MaNV = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa vĩnh viễn nhân viên
     */
    public boolean hardDelete(String maNV, Connection conn) throws SQLException {
        // Xóa người dùng liên quan trước
        String sqlUser = "DELETE FROM NguoiDung WHERE MaNV = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sqlUser)) {
            stmt.setString(1, maNV);
            stmt.executeUpdate();
        }
        
        // Xóa nhân viên
        String sql = "DELETE FROM NhanVien WHERE MaNV = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maNV);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Kiểm tra mã nhân viên đã tồn tại chưa
     */
    public boolean existsByMaNV(String maNV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE MaNV = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Tạo mã nhân viên tự động
     */
    public String generateMaNV() throws SQLException {
        String sql = "SELECT MAX(CAST(SUBSTRING(MaNV, 3, LEN(MaNV)) AS INT)) FROM NhanVien WHERE MaNV LIKE 'NV%'";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            int maxId = 0;
            if (rs.next()) {
                maxId = rs.getInt(1);
            }
            return String.format("NV%03d", maxId + 1);
        }
    }
    
    /**
     * Lấy danh sách phòng ban
     */
    public List<PhongBan> getAllPhongBan() throws SQLException {
        List<PhongBan> list = new ArrayList<>();
        String sql = "SELECT * FROM PhongBan WHERE TrangThai = 1 ORDER BY TenPB";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                PhongBan pb = new PhongBan();
                pb.setMaPB(rs.getString("MaPB"));
                pb.setTenPB(rs.getNString("TenPB"));
                pb.setDiaChi(rs.getNString("DiaChi"));
                pb.setSoDienThoai(rs.getString("SoDienThoai"));
                pb.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(pb);
            }
        }
        return list;
    }
    
    /**
     * Lấy danh sách chức vụ
     */
    public List<ChucVu> getAllChucVu() throws SQLException {
        List<ChucVu> list = new ArrayList<>();
        String sql = "SELECT * FROM ChucVu WHERE TrangThai = 1 ORDER BY TenCV";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ChucVu cv = new ChucVu();
                cv.setMaCV(rs.getString("MaCV"));
                cv.setTenCV(rs.getNString("TenCV"));
                cv.setMoTa(rs.getNString("MoTa"));
                cv.setTrangThai(rs.getBoolean("TrangThai"));
                list.add(cv);
            }
        }
        return list;
    }
    
    /**
     * Đếm tổng số nhân viên đang hoạt động
     */
    public int countActiveEmployees() throws SQLException {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE TrangThai = 1";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Map ResultSet sang object NhanVien
     */
    private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        NhanVien nv = new NhanVien();
        nv.setMaNV(rs.getString("MaNV"));
        nv.setHoTen(rs.getNString("HoTen"));
        
        Date ngaySinh = rs.getDate("NgaySinh");
        if (ngaySinh != null) {
            nv.setNgaySinh(ngaySinh.toLocalDate());
        }
        
        nv.setGioiTinh(rs.getNString("GioiTinh"));
        nv.setCmnd(rs.getString("CMND"));
        nv.setDiaChi(rs.getNString("DiaChi"));
        nv.setSoDienThoai(rs.getString("SoDienThoai"));
        nv.setEmail(rs.getString("Email"));
        nv.setMaPB(rs.getString("MaPB"));
        nv.setMaCV(rs.getString("MaCV"));
        
        Date ngayVaoLam = rs.getDate("NgayVaoLam");
        if (ngayVaoLam != null) {
            nv.setNgayVaoLam(ngayVaoLam.toLocalDate());
        }
        
        nv.setTrangThai(rs.getBoolean("TrangThai"));
        
        // Thông tin từ join
        try {
            nv.setTenPhongBan(rs.getNString("TenPB"));
            nv.setTenChucVu(rs.getNString("TenCV"));
        } catch (SQLException e) {
            // Ignore if columns not in result set
        }
        
        return nv;
    }
    
    /**
     * Đếm số nhân viên mới trong năm
     */
    public int countNewEmployeesInYear(int nam) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NhanVien WHERE YEAR(NgayVaoLam) = ? AND TrangThai = 1";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    /**
     * Lấy thống kê theo phòng ban
     */
    public List<java.util.Map<String, Object>> getStatsByPhongBan() throws SQLException {
        List<java.util.Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT pb.TenPB, COUNT(nv.MaNV) as SoNV " +
                    "FROM PhongBan pb " +
                    "LEFT JOIN NhanVien nv ON pb.MaPB = nv.MaPB AND nv.TrangThai = 1 " +
                    "WHERE pb.TrangThai = 1 " +
                    "GROUP BY pb.MaPB, pb.TenPB " +
                    "ORDER BY pb.TenPB";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                java.util.Map<String, Object> row = new java.util.HashMap<>();
                row.put("tenPB", rs.getNString("TenPB"));
                row.put("soNV", rs.getInt("SoNV"));
                row.put("tongLuong", null);
                row.put("luongTB", null);
                row.put("luongMax", null);
                row.put("luongMin", null);
                result.add(row);
            }
        }
        return result;
    }
    
    /**
     * Test connection to DB1
     */
    public boolean testConnection() throws SQLException {
        try (Connection conn = DatabaseConnection.getDB1Connection()) {
            return conn != null && !conn.isClosed();
        }
    }
}
