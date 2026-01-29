package com.hrms.service;

import com.hrms.dao.LuongDAO;
import com.hrms.model.BangLuongThang;
import com.hrms.model.LuongNhanVien;
import com.hrms.util.SessionManager;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Service class xử lý nghiệp vụ lương thưởng
 * Chỉ Admin và Kế toán mới có quyền sử dụng
 */
public class LuongService {
    
    private LuongDAO luongDAO;
    
    public LuongService() {
        this.luongDAO = new LuongDAO();
    }
    
    /**
     * Kiểm tra quyền truy cập
     */
    private void checkSalaryAccess() throws SQLException {
        if (!SessionManager.getInstance().canAccessSalary()) {
            throw new SQLException("Bạn không có quyền truy cập thông tin lương thưởng!");
        }
    }
    
    /**
     * Lấy thông tin lương của nhân viên
     */
    public LuongNhanVien getLuongByMaNV(String maNV) throws SQLException {
        checkSalaryAccess();
        return luongDAO.findByMaNV(maNV);
    }
    
    /**
     * Lấy danh sách lương của tất cả nhân viên
     */
    public List<LuongNhanVien> getAllLuong() throws SQLException {
        checkSalaryAccess();
        return luongDAO.getAllLuong();
    }
    
    /**
     * Cập nhật thông tin lương
     */
    public boolean updateLuong(LuongNhanVien luong) throws SQLException {
        checkSalaryAccess();
        
        if (!SessionManager.getInstance().isAdmin() && !SessionManager.getInstance().isKeToan()) {
            throw new SQLException("Bạn không có quyền cập nhật thông tin lương!");
        }
        
        return luongDAO.update(luong);
    }
    
    /**
     * Lấy bảng lương theo tháng/năm
     */
    public List<BangLuongThang> getBangLuongThang(int thang, int nam) throws SQLException {
        checkSalaryAccess();
        return luongDAO.getBangLuongThang(thang, nam);
    }
    
    /**
     * Lấy lịch sử bảng lương của một nhân viên
     */
    public List<BangLuongThang> getLichSuLuong(String maNV) throws SQLException {
        checkSalaryAccess();
        return luongDAO.getBangLuongByNhanVien(maNV);
    }
    
    /**
     * Tính lương tháng cho nhân viên
     */
    public BangLuongThang tinhLuongThang(String maNV, int thang, int nam, 
                                         int soNgayCong, BigDecimal thuong) throws SQLException {
        checkSalaryAccess();
        return luongDAO.tinhLuongThang(maNV, thang, nam, soNgayCong, thuong);
    }
    
    /**
     * Lưu bảng lương tháng
     */
    public boolean saveBangLuongThang(BangLuongThang bangLuong) throws SQLException {
        checkSalaryAccess();
        return luongDAO.saveBangLuongThang(bangLuong);
    }
    
    /**
     * Tính lương tháng cho tất cả nhân viên
     */
    public int tinhLuongChoTatCa(int thang, int nam, int soNgayCong) throws SQLException {
        checkSalaryAccess();
        
        List<LuongNhanVien> dsLuong = luongDAO.getAllLuong();
        int count = 0;
        
        for (LuongNhanVien luong : dsLuong) {
            try {
                BangLuongThang bl = tinhLuongThang(luong.getMaNV(), thang, nam, soNgayCong, BigDecimal.ZERO);
                if (luongDAO.saveBangLuongThang(bl)) {
                    count++;
                }
            } catch (SQLException e) {
                System.err.println("Lỗi tính lương cho NV " + luong.getMaNV() + ": " + e.getMessage());
            }
        }
        
        return count;
    }
    
    /**
     * Duyệt bảng lương
     */
    public boolean duyetBangLuong(int maBangLuong) throws SQLException {
        checkSalaryAccess();
        
        if (!SessionManager.getInstance().isAdmin() && !SessionManager.getInstance().isKeToan()) {
            throw new SQLException("Bạn không có quyền duyệt bảng lương!");
        }
        
        String nguoiDuyet = SessionManager.getInstance().getCurrentUser().getTenDangNhap();
        return luongDAO.duyetBangLuong(maBangLuong, nguoiDuyet);
    }
    
    /**
     * Tính tổng quỹ lương tháng
     */
    public BigDecimal getTongQuyLuong(int thang, int nam) throws SQLException {
        checkSalaryAccess();
        return luongDAO.getTongQuyLuong(thang, nam);
    }
    
    /**
     * Thống kê lương theo phòng ban
     * (Có thể mở rộng thêm các phương thức thống kê khác)
     */
    public BigDecimal getTongLuongTheoPhongBan(String maPB, int thang, int nam) throws SQLException {
        checkSalaryAccess();
        // TODO: Implement cross-database query
        return BigDecimal.ZERO;
    }
    
    /**
     * Lấy danh sách lương nhân viên có họ tên
     */
    public List<LuongNhanVien> getAllLuongNhanVien() throws SQLException {
        checkSalaryAccess();
        return luongDAO.getAllLuongWithName();
    }
    
    /**
     * Tìm kiếm lương theo tên nhân viên
     */
    public List<LuongNhanVien> searchLuong(String keyword) throws SQLException {
        checkSalaryAccess();
        return luongDAO.searchByName(keyword);
    }
    
    /**
     * Thêm thưởng cho nhân viên
     */
    public boolean addThuong(String maNV, int thang, int nam, BigDecimal soTien, String ghiChu) throws SQLException {
        checkSalaryAccess();
        return luongDAO.addThuong(maNV, thang, nam, soTien, ghiChu);
    }
    
    /**
     * Tính lương tháng cho tất cả nhân viên
     */
    public int tinhLuongThang(int thang, int nam) throws SQLException {
        checkSalaryAccess();
        
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền tính lương!");
        }
        
        return luongDAO.tinhLuongThangAll(thang, nam);
    }
    
    /**
     * Lấy thống kê lương
     */
    public java.util.Map<String, BigDecimal> getLuongStatistics() throws SQLException {
        checkSalaryAccess();
        return luongDAO.getLuongStatistics();
    }
    
    /**
     * Test connection to DB2
     */
    public boolean testConnection() throws SQLException {
        return luongDAO.testConnection();
    }
}
