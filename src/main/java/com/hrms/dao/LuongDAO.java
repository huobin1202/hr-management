package com.hrms.dao;

import com.hrms.model.LuongNhanVien;
import com.hrms.model.BangLuongThang;
import com.hrms.util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class cho các thao tác trên DB2 (HR_SALARY)
 * Quản lý thông tin lương thưởng (nhạy cảm)
 * Chỉ ADMIN và KẾ TOÁN mới có quyền sử dụng
 */
public class LuongDAO {
    
    /**
     * Lấy thông tin lương của nhân viên
     */
    public LuongNhanVien findByMaNV(String maNV) throws SQLException {
        String sql = "SELECT * FROM LuongNhanVien WHERE MaNV = ?";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLuong(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Lấy danh sách lương của tất cả nhân viên
     */
    public List<LuongNhanVien> getAllLuong() throws SQLException {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT * FROM LuongNhanVien ORDER BY MaNV";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(mapResultSetToLuong(rs));
            }
        }
        return list;
    }
    
    /**
     * Thêm thông tin lương mới
     */
    public boolean insert(LuongNhanVien luong) throws SQLException {
        String sql = "INSERT INTO LuongNhanVien (MaNV, LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, NgayHieuLuc, GhiChu) " +
                    "VALUES (?, ?, ?, ?, ?, GETDATE(), ?)";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, luong.getMaNV());
            stmt.setBigDecimal(2, luong.getLuongCoBan());
            stmt.setBigDecimal(3, luong.getHeSoLuong());
            stmt.setBigDecimal(4, luong.getPhuCapChucVu());
            stmt.setBigDecimal(5, luong.getPhuCapKhac());
            stmt.setString(6, luong.getGhiChu());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Thêm thông tin lương sử dụng connection từ transaction
     */
    public boolean insert(LuongNhanVien luong, Connection conn) throws SQLException {
        String sql = "INSERT INTO LuongNhanVien (MaNV, LuongCoBan, HeSoLuong, PhuCapChucVu, PhuCapKhac, NgayHieuLuc, GhiChu) " +
                    "VALUES (?, ?, ?, ?, ?, GETDATE(), ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, luong.getMaNV());
            stmt.setBigDecimal(2, luong.getLuongCoBan());
            stmt.setBigDecimal(3, luong.getHeSoLuong());
            stmt.setBigDecimal(4, luong.getPhuCapChucVu());
            stmt.setBigDecimal(5, luong.getPhuCapKhac());
            stmt.setString(6, luong.getGhiChu());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật thông tin lương
     */
    public boolean update(LuongNhanVien luong) throws SQLException {
        String sql = "UPDATE LuongNhanVien SET LuongCoBan = ?, HeSoLuong = ?, PhuCapChucVu = ?, " +
                    "PhuCapKhac = ?, NgayCapNhat = GETDATE(), GhiChu = ? WHERE MaNV = ?";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, luong.getLuongCoBan());
            stmt.setBigDecimal(2, luong.getHeSoLuong());
            stmt.setBigDecimal(3, luong.getPhuCapChucVu());
            stmt.setBigDecimal(4, luong.getPhuCapKhac());
            stmt.setString(5, luong.getGhiChu());
            stmt.setString(6, luong.getMaNV());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Cập nhật lương sử dụng connection từ transaction
     */
    public boolean update(LuongNhanVien luong, Connection conn) throws SQLException {
        String sql = "UPDATE LuongNhanVien SET LuongCoBan = ?, HeSoLuong = ?, PhuCapChucVu = ?, " +
                    "PhuCapKhac = ?, NgayCapNhat = GETDATE(), GhiChu = ? WHERE MaNV = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBigDecimal(1, luong.getLuongCoBan());
            stmt.setBigDecimal(2, luong.getHeSoLuong());
            stmt.setBigDecimal(3, luong.getPhuCapChucVu());
            stmt.setBigDecimal(4, luong.getPhuCapKhac());
            stmt.setString(5, luong.getGhiChu());
            stmt.setString(6, luong.getMaNV());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa thông tin lương
     */
    public boolean delete(String maNV, Connection conn) throws SQLException {
        // Xóa các bản ghi liên quan trước
        String[] deleteSqls = {
            "DELETE FROM Thuong WHERE MaNV = ?",
            "DELETE FROM BangLuongThang WHERE MaNV = ?",
            "DELETE FROM LichSuLuong WHERE MaNV = ?",
            "DELETE FROM LuongNhanVien WHERE MaNV = ?"
        };
        
        for (String sql : deleteSqls) {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, maNV);
                stmt.executeUpdate();
            }
        }
        return true;
    }
    
    /**
     * Kiểm tra nhân viên đã có thông tin lương chưa
     */
    public boolean existsByMaNV(String maNV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM LuongNhanVien WHERE MaNV = ?";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
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
    
    // ========== BẢNG LƯƠNG THÁNG ==========
    
    /**
     * Lấy bảng lương theo tháng/năm
     */
    public List<BangLuongThang> getBangLuongThang(int thang, int nam) throws SQLException {
        List<BangLuongThang> list = new ArrayList<>();
        String sql = "SELECT bl.*, nv.HoTen FROM BangLuongThang bl " +
                    "INNER JOIN HR_INFO.dbo.NhanVien nv ON bl.MaNV = nv.MaNV " +
                    "WHERE bl.Thang = ? AND bl.Nam = ? ORDER BY bl.MaNV";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BangLuongThang bl = mapResultSetToBangLuong(rs);
                    bl.setHoTen(rs.getNString("HoTen"));
                    list.add(bl);
                }
            }
        }
        return list;
    }
    
    /**
     * Lấy bảng lương của một nhân viên
     */
    public List<BangLuongThang> getBangLuongByNhanVien(String maNV) throws SQLException {
        List<BangLuongThang> list = new ArrayList<>();
        String sql = "SELECT * FROM BangLuongThang WHERE MaNV = ? ORDER BY Nam DESC, Thang DESC";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToBangLuong(rs));
                }
            }
        }
        return list;
    }
    
    /**
     * Tính và tạo bảng lương tháng cho nhân viên
     */
    public BangLuongThang tinhLuongThang(String maNV, int thang, int nam, int soNgayCong, 
                                         BigDecimal thuong) throws SQLException {
        // Lấy thông tin lương cơ bản
        LuongNhanVien luong = findByMaNV(maNV);
        if (luong == null) {
            throw new SQLException("Không tìm thấy thông tin lương của nhân viên: " + maNV);
        }
        
        BangLuongThang bangLuong = new BangLuongThang(maNV, thang, nam);
        bangLuong.setSoNgayCong(soNgayCong);
        bangLuong.setLuongCoBan(luong.getLuongCoBan());
        bangLuong.setHeSoLuong(luong.getHeSoLuong());
        bangLuong.setPhuCapChucVu(luong.getPhuCapChucVu());
        bangLuong.setPhuCapKhac(luong.getPhuCapKhac());
        bangLuong.setThuong(thuong != null ? thuong : BigDecimal.ZERO);
        
        // Tính tổng thu nhập
        BigDecimal tyLeCong = new BigDecimal(soNgayCong).divide(new BigDecimal(22), 4, BigDecimal.ROUND_HALF_UP);
        BigDecimal luongTheoHeSo = luong.getLuongCoBan().multiply(luong.getHeSoLuong());
        BigDecimal tongThuNhap = luongTheoHeSo.add(luong.getPhuCapChucVu())
                                              .add(luong.getPhuCapKhac())
                                              .multiply(tyLeCong)
                                              .add(bangLuong.getThuong());
        bangLuong.setTongThuNhap(tongThuNhap);
        
        // Tính khấu trừ
        BigDecimal bhxh = luong.getLuongCoBan().multiply(new BigDecimal("0.08"));
        BigDecimal bhyt = luong.getLuongCoBan().multiply(new BigDecimal("0.015"));
        BigDecimal bhtn = luong.getLuongCoBan().multiply(new BigDecimal("0.01"));
        
        bangLuong.setKhauTruBHXH(bhxh);
        bangLuong.setKhauTruBHYT(bhyt);
        bangLuong.setKhauTruBHTN(bhtn);
        
        // Tính thuế TNCN đơn giản
        BigDecimal giamTruGiaCanh = new BigDecimal("11000000");
        BigDecimal thuNhapChiuThue = tongThuNhap.subtract(bhxh).subtract(bhyt).subtract(bhtn).subtract(giamTruGiaCanh);
        BigDecimal thueTNCN = BigDecimal.ZERO;
        if (thuNhapChiuThue.compareTo(BigDecimal.ZERO) > 0) {
            thueTNCN = thuNhapChiuThue.multiply(new BigDecimal("0.1"));
        }
        bangLuong.setKhauTruThueTNCN(thueTNCN);
        
        // Tổng khấu trừ và lương thực nhận
        BigDecimal tongKhauTru = bhxh.add(bhyt).add(bhtn).add(thueTNCN);
        bangLuong.setTongKhauTru(tongKhauTru);
        bangLuong.setLuongThucNhan(tongThuNhap.subtract(tongKhauTru));
        
        return bangLuong;
    }
    
    /**
     * Lưu bảng lương tháng
     */
    public boolean saveBangLuongThang(BangLuongThang bl) throws SQLException {
        // Kiểm tra đã tồn tại chưa
        String checkSql = "SELECT COUNT(*) FROM BangLuongThang WHERE MaNV = ? AND Thang = ? AND Nam = ?";
        boolean exists = false;
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            
            checkStmt.setString(1, bl.getMaNV());
            checkStmt.setInt(2, bl.getThang());
            checkStmt.setInt(3, bl.getNam());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    exists = rs.getInt(1) > 0;
                }
            }
        }
        
        if (exists) {
            return updateBangLuongThang(bl);
        } else {
            return insertBangLuongThang(bl);
        }
    }
    
    private boolean insertBangLuongThang(BangLuongThang bl) throws SQLException {
        String sql = "INSERT INTO BangLuongThang (MaNV, Thang, Nam, SoNgayCong, LuongCoBan, HeSoLuong, " +
                    "PhuCapChucVu, PhuCapKhac, Thuong, KhauTruBHXH, KhauTruBHYT, KhauTruBHTN, " +
                    "KhauTruThueTNCN, TongThuNhap, TongKhauTru, LuongThucNhan) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setStatementParams(stmt, bl);
            return stmt.executeUpdate() > 0;
        }
    }
    
    private boolean updateBangLuongThang(BangLuongThang bl) throws SQLException {
        String sql = "UPDATE BangLuongThang SET SoNgayCong = ?, LuongCoBan = ?, HeSoLuong = ?, " +
                    "PhuCapChucVu = ?, PhuCapKhac = ?, Thuong = ?, KhauTruBHXH = ?, KhauTruBHYT = ?, " +
                    "KhauTruBHTN = ?, KhauTruThueTNCN = ?, TongThuNhap = ?, TongKhauTru = ?, " +
                    "LuongThucNhan = ?, TrangThai = N'Chờ duyệt' " +
                    "WHERE MaNV = ? AND Thang = ? AND Nam = ?";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, bl.getSoNgayCong());
            stmt.setBigDecimal(2, bl.getLuongCoBan());
            stmt.setBigDecimal(3, bl.getHeSoLuong());
            stmt.setBigDecimal(4, bl.getPhuCapChucVu());
            stmt.setBigDecimal(5, bl.getPhuCapKhac());
            stmt.setBigDecimal(6, bl.getThuong());
            stmt.setBigDecimal(7, bl.getKhauTruBHXH());
            stmt.setBigDecimal(8, bl.getKhauTruBHYT());
            stmt.setBigDecimal(9, bl.getKhauTruBHTN());
            stmt.setBigDecimal(10, bl.getKhauTruThueTNCN());
            stmt.setBigDecimal(11, bl.getTongThuNhap());
            stmt.setBigDecimal(12, bl.getTongKhauTru());
            stmt.setBigDecimal(13, bl.getLuongThucNhan());
            stmt.setString(14, bl.getMaNV());
            stmt.setInt(15, bl.getThang());
            stmt.setInt(16, bl.getNam());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    private void setStatementParams(PreparedStatement stmt, BangLuongThang bl) throws SQLException {
        stmt.setString(1, bl.getMaNV());
        stmt.setInt(2, bl.getThang());
        stmt.setInt(3, bl.getNam());
        stmt.setInt(4, bl.getSoNgayCong());
        stmt.setBigDecimal(5, bl.getLuongCoBan());
        stmt.setBigDecimal(6, bl.getHeSoLuong());
        stmt.setBigDecimal(7, bl.getPhuCapChucVu());
        stmt.setBigDecimal(8, bl.getPhuCapKhac());
        stmt.setBigDecimal(9, bl.getThuong());
        stmt.setBigDecimal(10, bl.getKhauTruBHXH());
        stmt.setBigDecimal(11, bl.getKhauTruBHYT());
        stmt.setBigDecimal(12, bl.getKhauTruBHTN());
        stmt.setBigDecimal(13, bl.getKhauTruThueTNCN());
        stmt.setBigDecimal(14, bl.getTongThuNhap());
        stmt.setBigDecimal(15, bl.getTongKhauTru());
        stmt.setBigDecimal(16, bl.getLuongThucNhan());
    }
    
    /**
     * Duyệt bảng lương
     */
    public boolean duyetBangLuong(int maBangLuong, String nguoiDuyet) throws SQLException {
        String sql = "UPDATE BangLuongThang SET TrangThai = N'Đã duyệt', NgayDuyet = GETDATE(), NguoiDuyet = ? " +
                    "WHERE MaBangLuong = ?";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nguoiDuyet);
            stmt.setInt(2, maBangLuong);
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Tính tổng quỹ lương theo tháng
     */
    public BigDecimal getTongQuyLuong(int thang, int nam) throws SQLException {
        String sql = "SELECT SUM(LuongThucNhan) FROM BangLuongThang WHERE Thang = ? AND Nam = ?";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, thang);
            stmt.setInt(2, nam);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Map ResultSet sang LuongNhanVien
     */
    private LuongNhanVien mapResultSetToLuong(ResultSet rs) throws SQLException {
        LuongNhanVien luong = new LuongNhanVien();
        luong.setMaNV(rs.getString("MaNV"));
        luong.setLuongCoBan(rs.getBigDecimal("LuongCoBan"));
        luong.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
        luong.setPhuCapChucVu(rs.getBigDecimal("PhuCapChucVu"));
        luong.setPhuCapKhac(rs.getBigDecimal("PhuCapKhac"));
        
        Date ngayHieuLuc = rs.getDate("NgayHieuLuc");
        if (ngayHieuLuc != null) {
            luong.setNgayHieuLuc(ngayHieuLuc.toLocalDate());
        }
        
        luong.setGhiChu(rs.getNString("GhiChu"));
        return luong;
    }
    
    /**
     * Map ResultSet sang BangLuongThang
     */
    private BangLuongThang mapResultSetToBangLuong(ResultSet rs) throws SQLException {
        BangLuongThang bl = new BangLuongThang();
        bl.setMaBangLuong(rs.getInt("MaBangLuong"));
        bl.setMaNV(rs.getString("MaNV"));
        bl.setThang(rs.getInt("Thang"));
        bl.setNam(rs.getInt("Nam"));
        bl.setSoNgayCong(rs.getInt("SoNgayCong"));
        bl.setLuongCoBan(rs.getBigDecimal("LuongCoBan"));
        bl.setHeSoLuong(rs.getBigDecimal("HeSoLuong"));
        bl.setPhuCapChucVu(rs.getBigDecimal("PhuCapChucVu"));
        bl.setPhuCapKhac(rs.getBigDecimal("PhuCapKhac"));
        bl.setThuong(rs.getBigDecimal("Thuong"));
        bl.setKhauTruBHXH(rs.getBigDecimal("KhauTruBHXH"));
        bl.setKhauTruBHYT(rs.getBigDecimal("KhauTruBHYT"));
        bl.setKhauTruBHTN(rs.getBigDecimal("KhauTruBHTN"));
        bl.setKhauTruThueTNCN(rs.getBigDecimal("KhauTruThueTNCN"));
        bl.setTongThuNhap(rs.getBigDecimal("TongThuNhap"));
        bl.setTongKhauTru(rs.getBigDecimal("TongKhauTru"));
        bl.setLuongThucNhan(rs.getBigDecimal("LuongThucNhan"));
        bl.setTrangThai(rs.getNString("TrangThai"));
        bl.setNguoiDuyet(rs.getString("NguoiDuyet"));
        return bl;
    }
    
    /**
     * Đếm số bản ghi lương
     */
    public int countRecords() throws SQLException {
        String sql = "SELECT COUNT(*) FROM LuongNhanVien";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Lấy danh sách lương với họ tên (join DB1)
     */
    public List<LuongNhanVien> getAllLuongWithName() throws SQLException {
        List<LuongNhanVien> list = new ArrayList<>();
        // Cross-database query
        String sql = "SELECT l.*, n.HoTen FROM LuongNhanVien l " +
                    "INNER JOIN HR_INFO.dbo.NhanVien n ON l.MaNV = n.MaNV " +
                    "WHERE n.TrangThai = 1 ORDER BY l.MaNV";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                LuongNhanVien luong = mapResultSetToLuong(rs);
                luong.setHoTen(rs.getNString("HoTen"));
                list.add(luong);
            }
        }
        return list;
    }
    
    /**
     * Tìm kiếm lương theo tên nhân viên
     */
    public List<LuongNhanVien> searchByName(String keyword) throws SQLException {
        List<LuongNhanVien> list = new ArrayList<>();
        String sql = "SELECT l.*, n.HoTen FROM LuongNhanVien l " +
                    "INNER JOIN HR_INFO.dbo.NhanVien n ON l.MaNV = n.MaNV " +
                    "WHERE n.TrangThai = 1 AND (n.HoTen LIKE ? OR l.MaNV LIKE ?) " +
                    "ORDER BY l.MaNV";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LuongNhanVien luong = mapResultSetToLuong(rs);
                    luong.setHoTen(rs.getNString("HoTen"));
                    list.add(luong);
                }
            }
        }
        return list;
    }
    
    /**
     * Thêm thưởng cho nhân viên
     */
    public boolean addThuong(String maNV, int thang, int nam, BigDecimal soTien, String ghiChu) throws SQLException {
        String sql = "INSERT INTO Thuong (MaNV, SoTien, LoaiThuong, NgayThuong, GhiChu) VALUES (?, ?, ?, GETDATE(), ?)";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, maNV);
            stmt.setBigDecimal(2, soTien);
            stmt.setString(3, ghiChu);
            stmt.setString(4, "Thưởng tháng " + thang + "/" + nam);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Tính lương tháng cho tất cả nhân viên
     */
    public int tinhLuongThangAll(int thang, int nam) throws SQLException {
        int count = 0;
        List<LuongNhanVien> dsLuong = getAllLuong();
        
        for (LuongNhanVien luong : dsLuong) {
            try {
                BangLuongThang bl = tinhLuongThang(luong.getMaNV(), thang, nam, 22, BigDecimal.ZERO);
                if (saveBangLuongThang(bl)) {
                    count++;
                }
            } catch (SQLException e) {
                System.err.println("Lỗi tính lương cho NV " + luong.getMaNV() + ": " + e.getMessage());
            }
        }
        
        return count;
    }
    
    /**
     * Lấy thống kê lương
     */
    public java.util.Map<String, BigDecimal> getLuongStatistics() throws SQLException {
        java.util.Map<String, BigDecimal> stats = new java.util.HashMap<>();
        
        String sql = "SELECT SUM(LuongCoBan * HeSoLuong + ISNULL(PhuCapChucVu, 0) + ISNULL(PhuCapKhac, 0)) as total, " +
                    "AVG(LuongCoBan * HeSoLuong + ISNULL(PhuCapChucVu, 0) + ISNULL(PhuCapKhac, 0)) as average, " +
                    "MIN(LuongCoBan * HeSoLuong + ISNULL(PhuCapChucVu, 0) + ISNULL(PhuCapKhac, 0)) as min_salary, " +
                    "MAX(LuongCoBan * HeSoLuong + ISNULL(PhuCapChucVu, 0) + ISNULL(PhuCapKhac, 0)) as max_salary " +
                    "FROM LuongNhanVien";
        
        try (Connection conn = DatabaseConnection.getDB2Connection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                stats.put("total", rs.getBigDecimal("total") != null ? rs.getBigDecimal("total") : BigDecimal.ZERO);
                stats.put("average", rs.getBigDecimal("average") != null ? rs.getBigDecimal("average") : BigDecimal.ZERO);
                stats.put("min", rs.getBigDecimal("min_salary") != null ? rs.getBigDecimal("min_salary") : BigDecimal.ZERO);
                stats.put("max", rs.getBigDecimal("max_salary") != null ? rs.getBigDecimal("max_salary") : BigDecimal.ZERO);
            }
        }
        
        return stats;
    }
    
    /**
     * Test connection to DB2
     */
    public boolean testConnection() throws SQLException {
        try (Connection conn = DatabaseConnection.getDB2Connection()) {
            return conn != null && !conn.isClosed();
        }
    }
}
