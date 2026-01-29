package com.hrms.service;

import com.hrms.dao.NhanVienDAO;
import com.hrms.dao.LuongDAO;
import com.hrms.model.*;
import com.hrms.util.DistributedTransactionManager;
import com.hrms.util.SessionManager;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class xử lý nghiệp vụ nhân viên
 * Quản lý thao tác phân tán trên cả 2 CSDL
 */
public class NhanVienService {
    
    private NhanVienDAO nhanVienDAO;
    private LuongDAO luongDAO;
    
    public NhanVienService() {
        this.nhanVienDAO = new NhanVienDAO();
        this.luongDAO = new LuongDAO();
    }
    
    /**
     * Lấy danh sách nhân viên (chỉ thông tin chung từ DB1)
     */
    public List<NhanVien> getAllNhanVien() throws SQLException {
        return nhanVienDAO.getAllNhanVien();
    }
    
    /**
     * Lấy danh sách nhân viên đầy đủ (bao gồm lương từ DB2)
     * Chỉ Admin và Kế toán mới có quyền
     */
    public List<NhanVienDayDu> getAllNhanVienDayDu() throws SQLException {
        if (!SessionManager.getInstance().canAccessSalary()) {
            throw new SQLException("Bạn không có quyền xem thông tin lương!");
        }
        
        List<NhanVienDayDu> result = new ArrayList<>();
        List<NhanVien> dsNhanVien = nhanVienDAO.getAllNhanVien();
        
        for (NhanVien nv : dsNhanVien) {
            LuongNhanVien luong = null;
            try {
                luong = luongDAO.findByMaNV(nv.getMaNV());
            } catch (SQLException e) {
                // Nếu không có quyền truy cập DB2, bỏ qua thông tin lương
            }
            result.add(new NhanVienDayDu(nv, luong));
        }
        
        return result;
    }
    
    /**
     * Tìm nhân viên theo mã
     */
    public NhanVien findByMaNV(String maNV) throws SQLException {
        return nhanVienDAO.findByMaNV(maNV);
    }
    
    /**
     * Tìm nhân viên đầy đủ thông tin theo mã
     */
    public NhanVienDayDu findNhanVienDayDu(String maNV) throws SQLException {
        NhanVien nv = nhanVienDAO.findByMaNV(maNV);
        if (nv == null) return null;
        
        LuongNhanVien luong = null;
        if (SessionManager.getInstance().canAccessSalary()) {
            try {
                luong = luongDAO.findByMaNV(maNV);
            } catch (SQLException e) {
                // Ignore
            }
        }
        
        return new NhanVienDayDu(nv, luong);
    }
    
    /**
     * Tìm kiếm nhân viên theo tên
     */
    public List<NhanVien> searchByName(String keyword) throws SQLException {
        return nhanVienDAO.searchByName(keyword);
    }
    
    /**
     * Lấy danh sách nhân viên theo phòng ban
     */
    public List<NhanVien> getByPhongBan(String maPB) throws SQLException {
        return nhanVienDAO.getByPhongBan(maPB);
    }
    
    /**
     * THÊM NHÂN VIÊN MỚI - DISTRIBUTED TRANSACTION
     * Thêm vào cả DB1 (thông tin chung) và DB2 (lương)
     * Đảm bảo tính toàn vẹn: hoặc cả 2 DB thành công, hoặc rollback tất cả
     */
    public boolean addNhanVien(NhanVien nv, LuongNhanVien luong) throws SQLException {
        // Kiểm tra quyền
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền thêm nhân viên!");
        }
        
        // Kiểm tra mã nhân viên đã tồn tại chưa
        if (nhanVienDAO.existsByMaNV(nv.getMaNV())) {
            throw new SQLException("Mã nhân viên đã tồn tại: " + nv.getMaNV());
        }
        
        // Sử dụng Distributed Transaction
        DistributedTransactionManager txManager = new DistributedTransactionManager();
        try {
            txManager.beginTransaction();
            
            // 1. Thêm vào DB1 (Thông tin chung)
            nhanVienDAO.insert(nv, txManager.getDB1Connection());
            
            // 2. Thêm vào DB2 (Thông tin lương)
            luong.setMaNV(nv.getMaNV());
            luongDAO.insert(luong, txManager.getDB2Connection());
            
            // Commit cả 2
            txManager.commit();
            return true;
            
        } catch (SQLException e) {
            txManager.rollback();
            throw new SQLException("Lỗi thêm nhân viên: " + e.getMessage());
        }
    }
    
    /**
     * CẬP NHẬT NHÂN VIÊN - DISTRIBUTED TRANSACTION
     */
    public boolean updateNhanVien(NhanVien nv, LuongNhanVien luong) throws SQLException {
        // Kiểm tra quyền
        if (!SessionManager.getInstance().isAdmin() && !SessionManager.getInstance().isKeToan()) {
            throw new SQLException("Bạn không có quyền cập nhật thông tin nhân viên!");
        }
        
        DistributedTransactionManager txManager = new DistributedTransactionManager();
        try {
            txManager.beginTransaction();
            
            // 1. Cập nhật DB1
            nhanVienDAO.update(nv, txManager.getDB1Connection());
            
            // 2. Cập nhật DB2 (nếu có quyền và có thông tin lương)
            if (luong != null && SessionManager.getInstance().canAccessSalary()) {
                luong.setMaNV(nv.getMaNV());
                luongDAO.update(luong, txManager.getDB2Connection());
            }
            
            txManager.commit();
            return true;
            
        } catch (SQLException e) {
            txManager.rollback();
            throw new SQLException("Lỗi cập nhật nhân viên: " + e.getMessage());
        }
    }
    
    /**
     * XÓA NHÂN VIÊN - DISTRIBUTED TRANSACTION
     * Xóa từ cả DB1 và DB2
     */
    public boolean deleteNhanVien(String maNV, boolean hardDelete) throws SQLException {
        // Kiểm tra quyền
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền xóa nhân viên!");
        }
        
        DistributedTransactionManager txManager = new DistributedTransactionManager();
        try {
            txManager.beginTransaction();
            
            if (hardDelete) {
                // Xóa vĩnh viễn: Xóa từ DB2 trước, sau đó DB1
                luongDAO.delete(maNV, txManager.getDB2Connection());
                nhanVienDAO.hardDelete(maNV, txManager.getDB1Connection());
            } else {
                // Soft delete: chỉ cập nhật trạng thái ở DB1
                nhanVienDAO.softDelete(maNV, txManager.getDB1Connection());
            }
            
            txManager.commit();
            return true;
            
        } catch (SQLException e) {
            txManager.rollback();
            throw new SQLException("Lỗi xóa nhân viên: " + e.getMessage());
        }
    }
    
    /**
     * Tạo mã nhân viên tự động
     */
    public String generateMaNV() throws SQLException {
        return nhanVienDAO.generateMaNV();
    }
    
    /**
     * Lấy danh sách phòng ban
     */
    public List<PhongBan> getAllPhongBan() throws SQLException {
        return nhanVienDAO.getAllPhongBan();
    }
    
    /**
     * Lấy danh sách chức vụ
     */
    public List<ChucVu> getAllChucVu() throws SQLException {
        return nhanVienDAO.getAllChucVu();
    }
    
    /**
     * Đếm tổng số nhân viên
     */
    public int countActiveEmployees() throws SQLException {
        return nhanVienDAO.countActiveEmployees();
    }
    
    /**
     * Kiểm tra tính toàn vẹn dữ liệu giữa 2 CSDL
     */
    public java.util.Map<String, Object> checkDataIntegrity() throws SQLException {
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền kiểm tra toàn vẹn dữ liệu!");
        }
        
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        int totalDB1 = nhanVienDAO.countActiveEmployees();
        int totalDB2 = luongDAO.countRecords();
        int missingInDB2 = 0;
        int missingInDB1 = 0;
        
        List<NhanVien> dsNhanVien = nhanVienDAO.getAllNhanVien();
        
        for (NhanVien nv : dsNhanVien) {
            try {
                if (!luongDAO.existsByMaNV(nv.getMaNV())) {
                    missingInDB2++;
                }
            } catch (SQLException e) {
                // Ignore
            }
        }
        
        result.put("totalDB1", totalDB1);
        result.put("totalDB2", totalDB2);
        result.put("missingInDB2", missingInDB2);
        result.put("missingInDB1", missingInDB1);
        
        return result;
    }
    
    /**
     * Đếm tổng số nhân viên
     */
    public int countTotalNhanVien() throws SQLException {
        return nhanVienDAO.countActiveEmployees();
    }
    
    /**
     * Đếm số nhân viên mới trong năm
     */
    public int countNewEmployeesInYear(int nam) throws SQLException {
        return nhanVienDAO.countNewEmployeesInYear(nam);
    }
    
    /**
     * Lấy thống kê theo phòng ban
     */
    public List<java.util.Map<String, Object>> getStatsByPhongBan() throws SQLException {
        return nhanVienDAO.getStatsByPhongBan();
    }
    
    /**
     * Test connection to DB1
     */
    public boolean testConnection() throws SQLException {
        return nhanVienDAO.testConnection();
    }
}
