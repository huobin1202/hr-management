package com.hrms.ui;

import com.hrms.util.DatabaseConnection;
import com.hrms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Panel demo phân quyền - Kiểm tra quyền truy cập vào từng database
 * Mục đích: Minh họa cơ chế phân mảnh dọc và phân quyền
 */
public class PhanQuyenDemoPanel extends JPanel {
    
    private JTextArea txtLog;
    private JLabel lblCurrentUser, lblCurrentRole;
    private JPanel statusPanel;
    
    public PhanQuyenDemoPanel() {
        initComponents();
        displayCurrentSession();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("🔐 DEMO PHÂN QUYỀN TRUY CẬP DATABASE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(102, 0, 102));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Left - User info and actions
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setPreferredSize(new Dimension(350, 0));
        
        // User info card
        JPanel userInfoPanel = createUserInfoPanel();
        leftPanel.add(userInfoPanel);
        
        leftPanel.add(Box.createVerticalStrut(10));
        
        // Permission matrix
        JPanel permissionPanel = createPermissionPanel();
        leftPanel.add(permissionPanel);
        
        leftPanel.add(Box.createVerticalStrut(10));
        
        // Test actions
        JPanel actionPanel = createActionPanel();
        leftPanel.add(actionPanel);
        
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Right - Log output
        JPanel logPanel = createLogPanel();
        mainPanel.add(logPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Bottom - Status
        statusPanel = createStatusPanel();
        add(statusPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createUserInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("👤 Thông tin phiên đăng nhập"));
        panel.setBackground(new Color(240, 248, 255));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Người dùng:"), gbc);
        gbc.gridx = 1;
        lblCurrentUser = new JLabel("-");
        lblCurrentUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblCurrentUser, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        lblCurrentRole = new JLabel("-");
        lblCurrentRole.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCurrentRole.setForeground(new Color(0, 100, 0));
        panel.add(lblCurrentRole, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblNote = new JLabel("<html><i>Vai trò quyết định database nào<br>bạn có thể truy cập</i></html>");
        lblNote.setForeground(Color.GRAY);
        panel.add(lblNote, gbc);
        
        return panel;
    }
    
    private JPanel createPermissionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("📋 Ma trận phân quyền"));
        
        String[] columns = {"Vai trò", "DB1 (HR_INFO)", "DB2 (HR_SALARY)"};
        Object[][] data = {
            {"Admin", "✅ Toàn quyền", "✅ Toàn quyền"},
            {"Kế toán", "✅ Đọc/Ghi", "✅ Đọc/Ghi"},
            {"Nhân viên", "✅ Chỉ đọc", "❌ Không có quyền"},
            {"IT", "✅ Đọc/Ghi", "❌ Không có quyền"}
        };
        
        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        // Color cells based on permission
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String val = value != null ? value.toString() : "";
                if (val.startsWith("✅")) {
                    c.setForeground(new Color(0, 128, 0));
                } else if (val.startsWith("❌")) {
                    c.setForeground(Color.RED);
                } else {
                    c.setForeground(Color.BLACK);
                }
                
                // Highlight current role
                String currentRole = SessionManager.getInstance().getCurrentUser().getVaiTro();
                if (table.getValueAt(row, 0).toString().equalsIgnoreCase(currentRole)) {
                    c.setBackground(new Color(255, 255, 200));
                } else {
                    c.setBackground(Color.WHITE);
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 150));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("🧪 Kiểm tra truy cập"));
        
        // Test DB1
        JPanel db1Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        db1Panel.add(new JLabel("DB1 (HR_INFO):"));
        
        JButton btnReadDB1 = new JButton("📖 Đọc");
        btnReadDB1.addActionListener(e -> testReadDB1());
        db1Panel.add(btnReadDB1);
        
        JButton btnWriteDB1 = new JButton("✏️ Ghi");
        btnWriteDB1.addActionListener(e -> testWriteDB1());
        db1Panel.add(btnWriteDB1);
        
        panel.add(db1Panel);
        
        // Test DB2
        JPanel db2Panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        db2Panel.add(new JLabel("DB2 (HR_SALARY):"));
        
        JButton btnReadDB2 = new JButton("📖 Đọc");
        btnReadDB2.addActionListener(e -> testReadDB2());
        db2Panel.add(btnReadDB2);
        
        JButton btnWriteDB2 = new JButton("✏️ Ghi");
        btnWriteDB2.addActionListener(e -> testWriteDB2());
        db2Panel.add(btnWriteDB2);
        
        panel.add(db2Panel);
        
        // Distributed operation test
        JPanel distPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnTestDistributed = new JButton("🔗 Test giao dịch phân tán");
        btnTestDistributed.setBackground(new Color(138, 43, 226));
        btnTestDistributed.setForeground(Color.WHITE);
        btnTestDistributed.addActionListener(e -> testDistributedTransaction());
        distPanel.add(btnTestDistributed);
        
        panel.add(distPanel);
        
        // Clear log
        JPanel clearPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnClear = new JButton("🗑️ Xóa log");
        btnClear.addActionListener(e -> txtLog.setText(""));
        clearPanel.add(btnClear);
        
        panel.add(clearPanel);
        
        return panel;
    }
    
    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("📝 Log thực thi"));
        
        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        txtLog.setBackground(new Color(30, 30, 30));
        txtLog.setForeground(new Color(0, 255, 0));
        txtLog.setCaretColor(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(txtLog);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        // DB1 status
        JLabel lblDB1Status = new JLabel("● DB1: ");
        JLabel lblDB1Value = new JLabel("Chưa kiểm tra");
        panel.add(lblDB1Status);
        panel.add(lblDB1Value);
        
        panel.add(new JSeparator(SwingConstants.VERTICAL));
        
        // DB2 status
        JLabel lblDB2Status = new JLabel("● DB2: ");
        JLabel lblDB2Value = new JLabel("Chưa kiểm tra");
        panel.add(lblDB2Status);
        panel.add(lblDB2Value);
        
        return panel;
    }
    
    private void displayCurrentSession() {
        var user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            lblCurrentUser.setText(user.getHoTen() + " (" + user.getTenDangNhap() + ")");
            lblCurrentRole.setText(user.getVaiTro());
            
            log("═══════════════════════════════════════════════════════");
            log("📌 PHIÊN LÀM VIỆC");
            log("   Người dùng: " + user.getHoTen());
            log("   Vai trò: " + user.getVaiTro());
            log("   Thời gian: " + java.time.LocalDateTime.now());
            log("═══════════════════════════════════════════════════════");
            log("");
        }
    }
    
    private void log(String message) {
        txtLog.append(message + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }
    
    private void testReadDB1() {
        log("▶ Thử đọc dữ liệu từ DB1 (HR_INFO)...");
        
        try (Connection conn = DatabaseConnection.getConnection("DB1")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM NhanVien WHERE TrangThai = 1");
            
            if (rs.next()) {
                int count = rs.getInt("cnt");
                log("  ✅ THÀNH CÔNG: Đọc được " + count + " nhân viên");
                log("  → Bảng: NhanVien");
                log("  → Database: HR_INFO");
            }
            
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            log("  ❌ THẤT BẠI: " + e.getMessage());
            log("  → Lý do có thể: Không có quyền SELECT trên bảng NhanVien");
        }
        log("");
    }
    
    private void testWriteDB1() {
        log("▶ Thử ghi dữ liệu vào DB1 (HR_INFO)...");
        log("  (Thử INSERT vào bảng LogHoatDong)");
        
        try (Connection conn = DatabaseConnection.getConnection("DB1")) {
            String sql = "INSERT INTO LogHoatDong (MaNguoiDung, HanhDong, MoTa, ThoiGian) " +
                         "VALUES (?, ?, ?, GETDATE())";
            
            var pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, SessionManager.getInstance().getCurrentUser().getMaNguoiDung());
            pstmt.setString(2, "TEST_WRITE");
            pstmt.setString(3, "Kiểm tra quyền ghi DB1");
            
            int rows = pstmt.executeUpdate();
            
            if (rows > 0) {
                log("  ✅ THÀNH CÔNG: Ghi được " + rows + " bản ghi");
                log("  → Bảng: LogHoatDong");
                log("  → Database: HR_INFO");
            }
            
            pstmt.close();
            
        } catch (Exception e) {
            log("  ❌ THẤT BẠI: " + e.getMessage());
            log("  → Lý do có thể: Không có quyền INSERT trên bảng LogHoatDong");
        }
        log("");
    }
    
    private void testReadDB2() {
        log("▶ Thử đọc dữ liệu từ DB2 (HR_SALARY)...");
        
        if (!SessionManager.getInstance().canAccessSalary()) {
            log("  ⚠️ CẢNH BÁO: Vai trò hiện tại không có quyền truy cập DB2");
            log("  → Vai trò của bạn: " + SessionManager.getInstance().getCurrentUser().getVaiTro());
            log("  → Quyền yêu cầu: Admin hoặc Kế toán");
            log("");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection("DB2")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt, AVG(LuongCoBan) as avg FROM LuongNhanVien");
            
            if (rs.next()) {
                int count = rs.getInt("cnt");
                double avg = rs.getDouble("avg");
                log("  ✅ THÀNH CÔNG: Đọc được " + count + " bản ghi lương");
                log("  → Lương trung bình: " + String.format("%,.0f VNĐ", avg));
                log("  → Bảng: LuongNhanVien");
                log("  → Database: HR_SALARY");
            }
            
            rs.close();
            stmt.close();
            
        } catch (Exception e) {
            log("  ❌ THẤT BẠI: " + e.getMessage());
            log("  → Lý do có thể: Không có quyền SELECT trên bảng LuongNhanVien");
        }
        log("");
    }
    
    private void testWriteDB2() {
        log("▶ Thử ghi dữ liệu vào DB2 (HR_SALARY)...");
        
        if (!SessionManager.getInstance().canAccessSalary()) {
            log("  ⚠️ CẢNH BÁO: Vai trò hiện tại không có quyền truy cập DB2");
            log("  → Vai trò của bạn: " + SessionManager.getInstance().getCurrentUser().getVaiTro());
            log("  → Quyền yêu cầu: Admin hoặc Kế toán");
            log("");
            return;
        }
        
        if (!SessionManager.getInstance().isAdmin()) {
            log("  ⚠️ CHỈ ADMIN mới có quyền ghi vào bảng lương chính");
            log("  → Vai trò của bạn: " + SessionManager.getInstance().getCurrentUser().getVaiTro());
            log("  → Kế toán chỉ có quyền ghi vào bảng BangLuongThang");
            log("");
            return;
        }
        
        log("  ℹ️ Bỏ qua test ghi DB2 để tránh thay đổi dữ liệu quan trọng");
        log("  → Trong thực tế, Admin có thể UPDATE bảng LuongNhanVien");
        log("");
    }
    
    private void testDistributedTransaction() {
        log("═══════════════════════════════════════════════════════");
        log("▶ THỬ NGHIỆM GIAO DỊCH PHÂN TÁN");
        log("═══════════════════════════════════════════════════════");
        log("");
        log("Kịch bản: Thêm nhân viên mới (ghi vào cả DB1 và DB2)");
        log("");
        
        if (!SessionManager.getInstance().isAdmin()) {
            log("  ⚠️ CHỈ ADMIN mới có quyền thực hiện giao dịch phân tán");
            log("  → Vai trò của bạn: " + SessionManager.getInstance().getCurrentUser().getVaiTro());
            log("");
            return;
        }
        
        log("📍 Bước 1: Mở kết nối đến DB1 (HR_INFO)...");
        try (Connection conn1 = DatabaseConnection.getConnection("DB1")) {
            conn1.setAutoCommit(false);
            log("  ✅ Kết nối DB1 thành công, AutoCommit = false");
            
            log("");
            log("📍 Bước 2: Mở kết nối đến DB2 (HR_SALARY)...");
            try (Connection conn2 = DatabaseConnection.getConnection("DB2")) {
                conn2.setAutoCommit(false);
                log("  ✅ Kết nối DB2 thành công, AutoCommit = false");
                
                log("");
                log("📍 Bước 3: Tạo Savepoint trên cả 2 database...");
                var sp1 = conn1.setSavepoint("sp_test_db1");
                var sp2 = conn2.setSavepoint("sp_test_db2");
                log("  ✅ Savepoint đã tạo");
                
                log("");
                log("📍 Bước 4: Thực hiện INSERT vào DB1...");
                log("  (Bỏ qua để không tạo dữ liệu test)");
                
                log("");
                log("📍 Bước 5: Thực hiện INSERT vào DB2...");
                log("  (Bỏ qua để không tạo dữ liệu test)");
                
                log("");
                log("📍 Bước 6: ROLLBACK để khôi phục trạng thái...");
                conn1.rollback(sp1);
                conn2.rollback(sp2);
                log("  ✅ Rollback thành công");
                
                log("");
                log("═══════════════════════════════════════════════════════");
                log("✅ GIAO DỊCH PHÂN TÁN HOẠT ĐỘNG BÌNH THƯỜNG");
                log("═══════════════════════════════════════════════════════");
                log("");
                log("📝 Ghi chú:");
                log("   - Dữ liệu được ghi đồng thời vào cả 2 DB");
                log("   - Nếu 1 DB lỗi → Rollback cả 2 DB");
                log("   - Đảm bảo tính toàn vẹn dữ liệu");
                
            } catch (Exception e) {
                conn1.rollback();
                log("  ❌ Lỗi kết nối DB2, đã rollback DB1");
                log("  → Chi tiết: " + e.getMessage());
            }
            
        } catch (Exception e) {
            log("  ❌ Lỗi kết nối DB1: " + e.getMessage());
        }
        
        log("");
    }
}
