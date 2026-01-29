package com.hrms.util;

import com.hrms.model.NguoiDung;

/**
 * Quản lý session đăng nhập của người dùng
 */
public class SessionManager {
    
    private static SessionManager instance;
    private NguoiDung currentUser;
    private long loginTime;
    private static final long SESSION_TIMEOUT = 30 * 60 * 1000; // 30 phút
    
    private SessionManager() {
    }
    
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Đăng nhập và tạo session
     */
    public void login(NguoiDung user) {
        this.currentUser = user;
        this.loginTime = System.currentTimeMillis();
        
        // Cập nhật credentials cho DatabaseConnection
        DatabaseConnection.setSessionCredentials(
            user.getTenDangNhap(),
            user.getMatKhau(),
            user.getVaiTro()
        );
    }
    
    /**
     * Đăng xuất và xóa session
     */
    public void logout() {
        this.currentUser = null;
        this.loginTime = 0;
        DatabaseConnection.resetSession();
    }
    
    /**
     * Kiểm tra đã đăng nhập chưa
     */
    public boolean isLoggedIn() {
        if (currentUser == null) return false;
        
        // Kiểm tra timeout
        if (System.currentTimeMillis() - loginTime > SESSION_TIMEOUT) {
            logout();
            return false;
        }
        
        return true;
    }
    
    /**
     * Lấy user hiện tại
     */
    public NguoiDung getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Kiểm tra có quyền admin không
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Kiểm tra có quyền kế toán không
     */
    public boolean isKeToan() {
        return currentUser != null && currentUser.isKeToan();
    }
    
    /**
     * Kiểm tra có quyền xem lương không
     */
    public boolean canAccessSalary() {
        return currentUser != null && currentUser.canAccessSalary();
    }
    
    /**
     * Kiểm tra quyền theo tên
     */
    public boolean hasPermission(String permission) {
        if (currentUser == null) return false;
        
        switch (permission) {
            case "VIEW_SALARY":
                return canAccessSalary();
            case "MANAGE_EMPLOYEE":
                return isAdmin();
            case "MANAGE_SALARY":
                return isAdmin() || isKeToan();
            case "VIEW_REPORTS":
                return true; // Tất cả đều xem được báo cáo cơ bản
            default:
                return false;
        }
    }
    
    /**
     * Gia hạn session
     */
    public void extendSession() {
        if (isLoggedIn()) {
            this.loginTime = System.currentTimeMillis();
        }
    }
    
    /**
     * Lấy thời gian còn lại của session (milliseconds)
     */
    public long getSessionTimeRemaining() {
        if (!isLoggedIn()) return 0;
        long elapsed = System.currentTimeMillis() - loginTime;
        return Math.max(0, SESSION_TIMEOUT - elapsed);
    }
}
