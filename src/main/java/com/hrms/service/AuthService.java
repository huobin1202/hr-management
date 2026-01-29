package com.hrms.service;

import com.hrms.dao.NguoiDungDAO;
import com.hrms.model.NguoiDung;
import com.hrms.util.DatabaseConnection;
import com.hrms.util.SessionManager;

import java.sql.SQLException;
import java.util.List;

/**
 * Service class xử lý nghiệp vụ đăng nhập và phân quyền
 */
public class AuthService {
    
    private NguoiDungDAO nguoiDungDAO;
    
    public AuthService() {
        this.nguoiDungDAO = new NguoiDungDAO();
    }
    
    /**
     * Đăng nhập hệ thống
     */
    public NguoiDung login(String tenDangNhap, String matKhau) throws SQLException {
        NguoiDung user = nguoiDungDAO.authenticate(tenDangNhap, matKhau);
        
        if (user != null) {
            // Thiết lập session
            SessionManager.getInstance().login(user);
            
            // Ghi log
            nguoiDungDAO.logActivity(user.getMaND(), "LOGIN", "Đăng nhập thành công");
            
            return user;
        }
        
        return null;
    }
    
    /**
     * Đăng xuất
     */
    public void logout() {
        NguoiDung currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser != null) {
            nguoiDungDAO.logActivity(currentUser.getMaND(), "LOGOUT", "Đăng xuất");
        }
        SessionManager.getInstance().logout();
    }
    
    /**
     * Kiểm tra đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }
    
    /**
     * Lấy user hiện tại
     */
    public NguoiDung getCurrentUser() {
        return SessionManager.getInstance().getCurrentUser();
    }
    
    /**
     * Đổi mật khẩu
     */
    public boolean changePassword(String matKhauCu, String matKhauMoi) throws SQLException {
        NguoiDung currentUser = SessionManager.getInstance().getCurrentUser();
        if (currentUser == null) {
            throw new SQLException("Chưa đăng nhập!");
        }
        
        // Kiểm tra mật khẩu cũ
        if (!currentUser.getMatKhau().equals(matKhauCu)) {
            throw new SQLException("Mật khẩu cũ không đúng!");
        }
        
        // Cập nhật mật khẩu
        boolean result = nguoiDungDAO.changePassword(currentUser.getMaND(), matKhauMoi);
        
        if (result) {
            currentUser.setMatKhau(matKhauMoi);
            nguoiDungDAO.logActivity(currentUser.getMaND(), "CHANGE_PASSWORD", "Đổi mật khẩu thành công");
        }
        
        return result;
    }
    
    /**
     * Lấy danh sách người dùng (chỉ Admin)
     */
    public List<NguoiDung> getAllNguoiDung() throws SQLException {
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền xem danh sách người dùng!");
        }
        return nguoiDungDAO.getAllNguoiDung();
    }
    
    /**
     * Thêm người dùng mới (chỉ Admin)
     */
    public boolean addNguoiDung(NguoiDung nguoiDung) throws SQLException {
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền thêm người dùng!");
        }
        
        // Kiểm tra tên đăng nhập đã tồn tại
        if (nguoiDungDAO.existsByTenDangNhap(nguoiDung.getTenDangNhap())) {
            throw new SQLException("Tên đăng nhập đã tồn tại!");
        }
        
        boolean result = nguoiDungDAO.insert(nguoiDung);
        
        if (result) {
            NguoiDung admin = SessionManager.getInstance().getCurrentUser();
            nguoiDungDAO.logActivity(admin.getMaND(), "ADD_USER", 
                "Thêm người dùng: " + nguoiDung.getTenDangNhap());
        }
        
        return result;
    }
    
    /**
     * Cập nhật người dùng (chỉ Admin)
     */
    public boolean updateNguoiDung(NguoiDung nguoiDung) throws SQLException {
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền cập nhật người dùng!");
        }
        
        boolean result = nguoiDungDAO.update(nguoiDung);
        
        if (result) {
            NguoiDung admin = SessionManager.getInstance().getCurrentUser();
            nguoiDungDAO.logActivity(admin.getMaND(), "UPDATE_USER", 
                "Cập nhật người dùng: " + nguoiDung.getTenDangNhap());
        }
        
        return result;
    }
    
    /**
     * Vô hiệu hóa tài khoản (chỉ Admin)
     */
    public boolean deactivateUser(int maND) throws SQLException {
        if (!SessionManager.getInstance().isAdmin()) {
            throw new SQLException("Chỉ Admin mới có quyền vô hiệu hóa tài khoản!");
        }
        
        // Không cho phép vô hiệu hóa chính mình
        if (SessionManager.getInstance().getCurrentUser().getMaND() == maND) {
            throw new SQLException("Không thể vô hiệu hóa tài khoản của chính mình!");
        }
        
        return nguoiDungDAO.deactivate(maND);
    }
    
    /**
     * Kiểm tra quyền
     */
    public boolean hasPermission(String permission) {
        return SessionManager.getInstance().hasPermission(permission);
    }
    
    /**
     * Test kết nối với quyền của user cụ thể
     * Dùng để demo phân quyền
     */
    public String testDatabaseAccess(String sqlLogin, String sqlPassword) {
        StringBuilder result = new StringBuilder();
        
        // Test DB1
        try {
            java.sql.Connection conn1 = DatabaseConnection.getDB1Connection(sqlLogin, sqlPassword);
            conn1.close();
            result.append("✓ Truy cập DB1 (HR_INFO) thành công\n");
        } catch (SQLException e) {
            result.append("✗ Không thể truy cập DB1: ").append(e.getMessage()).append("\n");
        }
        
        // Test DB2
        try {
            java.sql.Connection conn2 = DatabaseConnection.getDB2Connection(sqlLogin, sqlPassword);
            conn2.close();
            result.append("✓ Truy cập DB2 (HR_SALARY) thành công\n");
        } catch (SQLException e) {
            result.append("✗ Không thể truy cập DB2: ").append(e.getMessage()).append("\n");
        }
        
        return result.toString();
    }
}
