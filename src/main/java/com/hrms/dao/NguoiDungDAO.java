package com.hrms.dao;

import com.hrms.model.NguoiDung;
import com.hrms.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class cho người dùng hệ thống
 * Quản lý đăng nhập và phân quyền
 */
public class NguoiDungDAO {
    
    /**
     * Xác thực đăng nhập
     */
    public NguoiDung authenticate(String tenDangNhap, String matKhau) throws SQLException {
        String sql = "SELECT nd.*, nv.HoTen as HoTenNV " +
                    "FROM NguoiDung nd " +
                    "LEFT JOIN NhanVien nv ON nd.MaNV = nv.MaNV " +
                    "WHERE nd.TenDangNhap = ? AND nd.MatKhau = ? AND nd.TrangThai = 1";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenDangNhap);
            stmt.setString(2, matKhau);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    NguoiDung user = mapResultSetToNguoiDung(rs);
                    
                    // Cập nhật thời gian đăng nhập
                    updateLastLogin(user.getMaND());
                    
                    return user;
                }
            }
        }
        return null;
    }
    
    /**
     * Cập nhật thời gian đăng nhập cuối
     */
    private void updateLastLogin(int maND) {
        String sql = "UPDATE NguoiDung SET LanDangNhapCuoi = GETDATE() WHERE MaND = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maND);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi cập nhật thời gian đăng nhập: " + e.getMessage());
        }
    }
    
    /**
     * Tìm người dùng theo ID
     */
    public NguoiDung findById(int maND) throws SQLException {
        String sql = "SELECT nd.*, nv.HoTen as HoTenNV " +
                    "FROM NguoiDung nd " +
                    "LEFT JOIN NhanVien nv ON nd.MaNV = nv.MaNV " +
                    "WHERE nd.MaND = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maND);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNguoiDung(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Tìm người dùng theo tên đăng nhập
     */
    public NguoiDung findByTenDangNhap(String tenDangNhap) throws SQLException {
        String sql = "SELECT nd.*, nv.HoTen as HoTenNV " +
                    "FROM NguoiDung nd " +
                    "LEFT JOIN NhanVien nv ON nd.MaNV = nv.MaNV " +
                    "WHERE nd.TenDangNhap = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenDangNhap);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNguoiDung(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Lấy danh sách tất cả người dùng
     */
    public List<NguoiDung> getAllNguoiDung() throws SQLException {
        List<NguoiDung> list = new ArrayList<>();
        String sql = "SELECT nd.*, nv.HoTen as HoTenNV " +
                    "FROM NguoiDung nd " +
                    "LEFT JOIN NhanVien nv ON nd.MaNV = nv.MaNV " +
                    "ORDER BY nd.VaiTro, nd.TenDangNhap";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToNguoiDung(rs));
            }
        }
        return list;
    }
    
    /**
     * Thêm người dùng mới
     */
    public boolean insert(NguoiDung nd) throws SQLException {
        String sql = "INSERT INTO NguoiDung (TenDangNhap, MatKhau, MaNV, VaiTro) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nd.getTenDangNhap());
            stmt.setString(2, nd.getMatKhau());
            stmt.setString(3, nd.getMaNV());
            stmt.setString(4, nd.getVaiTro());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật thông tin người dùng
     */
    public boolean update(NguoiDung nd) throws SQLException {
        String sql = "UPDATE NguoiDung SET MatKhau = ?, MaNV = ?, VaiTro = ?, TrangThai = ? WHERE MaND = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nd.getMatKhau());
            stmt.setString(2, nd.getMaNV());
            stmt.setString(3, nd.getVaiTro());
            stmt.setBoolean(4, nd.isTrangThai());
            stmt.setInt(5, nd.getMaND());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Đổi mật khẩu
     */
    public boolean changePassword(int maND, String matKhauMoi) throws SQLException {
        String sql = "UPDATE NguoiDung SET MatKhau = ? WHERE MaND = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, matKhauMoi);
            stmt.setInt(2, maND);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Vô hiệu hóa tài khoản
     */
    public boolean deactivate(int maND) throws SQLException {
        String sql = "UPDATE NguoiDung SET TrangThai = 0 WHERE MaND = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maND);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Kiểm tra tên đăng nhập đã tồn tại chưa
     */
    public boolean existsByTenDangNhap(String tenDangNhap) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NguoiDung WHERE TenDangNhap = ?";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tenDangNhap);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    /**
     * Ghi log hoạt động
     */
    public void logActivity(int maND, String hanhDong, String moTa) {
        String sql = "INSERT INTO LogHoatDong (MaND, HanhDong, MoTa) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getDB1Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, maND);
            stmt.setString(2, hanhDong);
            stmt.setNString(3, moTa);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Lỗi ghi log: " + e.getMessage());
        }
    }
    
    /**
     * Map ResultSet sang NguoiDung
     */
    private NguoiDung mapResultSetToNguoiDung(ResultSet rs) throws SQLException {
        NguoiDung nd = new NguoiDung();
        nd.setMaND(rs.getInt("MaND"));
        nd.setTenDangNhap(rs.getString("TenDangNhap"));
        nd.setMatKhau(rs.getString("MatKhau"));
        nd.setMaNV(rs.getString("MaNV"));
        nd.setVaiTro(rs.getString("VaiTro"));
        nd.setTrangThai(rs.getBoolean("TrangThai"));
        
        Timestamp ngayTao = rs.getTimestamp("NgayTao");
        if (ngayTao != null) {
            nd.setNgayTao(ngayTao.toLocalDateTime());
        }
        
        Timestamp lanDangNhapCuoi = rs.getTimestamp("LanDangNhapCuoi");
        if (lanDangNhapCuoi != null) {
            nd.setLanDangNhapCuoi(lanDangNhapCuoi.toLocalDateTime());
        }
        
        try {
            nd.setHoTenNV(rs.getNString("HoTenNV"));
        } catch (SQLException e) {
            // Column might not exist
        }
        
        return nd;
    }
}
