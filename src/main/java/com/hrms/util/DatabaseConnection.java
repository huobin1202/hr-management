package com.hrms.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class quản lý kết nối đến 2 CSDL phân mảnh
 * - DB1 (HR_INFO): Thông tin chung
 * - DB2 (HR_SALARY): Lương thưởng (nhạy cảm)
 */
public class DatabaseConnection {
    
    private static Properties properties = new Properties();
    private static boolean initialized = false;
    
    // Connection strings cho các loại user khác nhau
    private static String db1Url;
    private static String db2Url;
    
    // Thông tin đăng nhập mặc định (Admin)
    private static String defaultUsername;
    private static String defaultPassword;
    
    // Thông tin đăng nhập hiện tại của session
    private static String currentUsername;
    private static String currentPassword;
    private static String currentRole;
    
    static {
        loadProperties();
    }
    
    /**
     * Load cấu hình từ file properties
     */
    private static void loadProperties() {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                System.err.println("Không tìm thấy file database.properties, sử dụng cấu hình mặc định");
                setDefaultProperties();
            } else {
                properties.load(input);
            }
            buildConnectionUrls();
            initialized = true;
        } catch (IOException e) {
            System.err.println("Lỗi đọc file cấu hình: " + e.getMessage());
            setDefaultProperties();
            buildConnectionUrls();
        }
    }
    
    /**
     * Thiết lập cấu hình mặc định
     */
    private static void setDefaultProperties() {
        properties.setProperty("db1.server", "localhost");
        properties.setProperty("db1.port", "1433");
        properties.setProperty("db1.database", "HR_INFO");
        properties.setProperty("db1.username", "admin_login");
        properties.setProperty("db1.password", "Admin@123456");
        
        properties.setProperty("db2.server", "localhost");
        properties.setProperty("db2.port", "1433");
        properties.setProperty("db2.database", "HR_SALARY");
        properties.setProperty("db2.username", "admin_login");
        properties.setProperty("db2.password", "Admin@123456");
    }
    
    /**
     * Xây dựng URL kết nối
     */
    private static void buildConnectionUrls() {
        String db1Server = properties.getProperty("db1.server", "localhost");
        String db1Port = properties.getProperty("db1.port", "1433");
        String db1Database = properties.getProperty("db1.database", "HR_INFO");
        
        String db2Server = properties.getProperty("db2.server", "localhost");
        String db2Port = properties.getProperty("db2.port", "1433");
        String db2Database = properties.getProperty("db2.database", "HR_SALARY");
        
        db1Url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true",
                db1Server, db1Port, db1Database);
        db2Url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=true;trustServerCertificate=true",
                db2Server, db2Port, db2Database);
        
        defaultUsername = properties.getProperty("db1.username");
        defaultPassword = properties.getProperty("db1.password");
    }
    
    /**
     * Thiết lập thông tin đăng nhập cho session
     */
    public static void setSessionCredentials(String username, String password, String role) {
        currentUsername = username;
        currentPassword = password;
        currentRole = role;
    }
    
    /**
     * Lấy thông tin vai trò hiện tại
     */
    public static String getCurrentRole() {
        return currentRole;
    }
    
    /**
     * Kiểm tra user hiện tại có quyền truy cập DB2 không
     */
    public static boolean canAccessDB2() {
        return "ADMIN".equals(currentRole) || "KETOAN".equals(currentRole);
    }
    
    /**
     * Lấy kết nối đến DB1 (HR_INFO - Thông tin chung)
     * Tất cả user đều có thể kết nối
     */
    public static Connection getDB1Connection() throws SQLException {
        String username = currentUsername != null ? currentUsername : defaultUsername;
        String password = currentPassword != null ? currentPassword : defaultPassword;
        
        // Map vai trò sang SQL login
        String sqlLogin = mapRoleToSqlLogin(currentRole);
        String sqlPassword = mapRoleToSqlPassword(currentRole);
        
        if (sqlLogin != null) {
            return DriverManager.getConnection(db1Url, sqlLogin, sqlPassword);
        }
        return DriverManager.getConnection(db1Url, username, password);
    }
    
    /**
     * Lấy kết nối đến DB2 (HR_SALARY - Lương thưởng)
     * Chỉ Admin và Kế toán mới có quyền
     */
    public static Connection getDB2Connection() throws SQLException {
        if (!canAccessDB2()) {
            throw new SQLException("Bạn không có quyền truy cập CSDL Lương thưởng!");
        }
        
        String sqlLogin = mapRoleToSqlLogin(currentRole);
        String sqlPassword = mapRoleToSqlPassword(currentRole);
        
        if (sqlLogin != null) {
            return DriverManager.getConnection(db2Url, sqlLogin, sqlPassword);
        }
        
        String username = currentUsername != null ? currentUsername : defaultUsername;
        String password = currentPassword != null ? currentPassword : defaultPassword;
        return DriverManager.getConnection(db2Url, username, password);
    }
    
    /**
     * Lấy kết nối với thông tin login tùy chỉnh (dùng cho test phân quyền)
     */
    public static Connection getDB1Connection(String sqlLogin, String sqlPassword) throws SQLException {
        return DriverManager.getConnection(db1Url, sqlLogin, sqlPassword);
    }
    
    public static Connection getDB2Connection(String sqlLogin, String sqlPassword) throws SQLException {
        return DriverManager.getConnection(db2Url, sqlLogin, sqlPassword);
    }
    
    /**
     * Map vai trò ứng dụng sang SQL Server login
     */
    private static String mapRoleToSqlLogin(String role) {
        if (role == null) return "admin_login";
        switch (role) {
            case "ADMIN": return "admin_login";
            case "NHANVIEN": return "nhanvien_login";
            case "KETOAN": return "ketoan_login";
            default: return "admin_login";
        }
    }
    
    private static String mapRoleToSqlPassword(String role) {
        if (role == null) return "Admin@123456";
        switch (role) {
            case "ADMIN": return "Admin@123456";
            case "NHANVIEN": return "NhanVien@123";
            case "KETOAN": return "KeToan@123456";
            default: return "Admin@123456";
        }
    }
    
    /**
     * Đóng kết nối an toàn
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Lỗi đóng kết nối: " + e.getMessage());
            }
        }
    }
    
    /**
     * Kiểm tra kết nối đến cả 2 CSDL
     */
    public static boolean testConnections() {
        boolean db1Ok = false, db2Ok = false;
        
        try (Connection conn1 = DriverManager.getConnection(db1Url, defaultUsername, defaultPassword)) {
            db1Ok = conn1 != null && !conn1.isClosed();
            System.out.println("✓ Kết nối DB1 (HR_INFO) thành công");
        } catch (SQLException e) {
            System.err.println("✗ Lỗi kết nối DB1: " + e.getMessage());
        }
        
        try (Connection conn2 = DriverManager.getConnection(db2Url, defaultUsername, defaultPassword)) {
            db2Ok = conn2 != null && !conn2.isClosed();
            System.out.println("✓ Kết nối DB2 (HR_SALARY) thành công");
        } catch (SQLException e) {
            System.err.println("✗ Lỗi kết nối DB2: " + e.getMessage());
        }
        
        return db1Ok && db2Ok;
    }
    
    /**
     * Reset session về trạng thái mặc định
     */
    public static void resetSession() {
        currentUsername = null;
        currentPassword = null;
        currentRole = null;
    }
    
    public static String getDb1Url() {
        return db1Url;
    }
    
    public static String getDb2Url() {
        return db2Url;
    }
    
    /**
     * Lấy connection theo tên database (DB1 hoặc DB2)
     * Dùng cho các trường hợp cần linh hoạt chọn database
     */
    public static Connection getConnection(String dbName) throws SQLException {
        if ("DB1".equalsIgnoreCase(dbName) || "HR_INFO".equalsIgnoreCase(dbName)) {
            return getDB1Connection();
        } else if ("DB2".equalsIgnoreCase(dbName) || "HR_SALARY".equalsIgnoreCase(dbName)) {
            return getDB2Connection();
        } else {
            throw new SQLException("Tên database không hợp lệ: " + dbName + ". Sử dụng DB1 hoặc DB2.");
        }
    }
}
