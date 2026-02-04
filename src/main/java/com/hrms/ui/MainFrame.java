package com.hrms.ui;

import com.hrms.model.NguoiDung;
import com.hrms.service.AuthService;
import com.hrms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Frame chính của ứng dụng
 */
public class MainFrame extends JFrame {
    
    private NguoiDung currentUser;
    private AuthService authService;
    
    private JTabbedPane tabbedPane;
    private JLabel lblUserInfo;
    private JLabel lblRole;
    
    // Panels
    private NhanVienPanel nhanVienPanel;
    private LuongPanel luongPanel;
    private BaoCaoPanel baoCaoPanel;
    private PhanQuyenDemoPanel phanQuyenPanel;
    
    public MainFrame(NguoiDung user) {
        this.currentUser = user;
        this.authService = new AuthService();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Hệ thống Quản lý Lương & Hồ sơ Nhân sự - Phân mảnh dọc CSDL");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        
        // Menu Bar
        createMenuBar();
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        // Tạo các panel
        nhanVienPanel = new NhanVienPanel();
        tabbedPane.addTab("📋 Quản lý Nhân viên", nhanVienPanel);
        
        // Tab Lương chỉ hiển thị nếu có quyền
        if (SessionManager.getInstance().canAccessSalary()) {
            luongPanel = new LuongPanel();
            tabbedPane.addTab("💰 Quản lý Lương", luongPanel);
        }
        
        baoCaoPanel = new BaoCaoPanel();
        tabbedPane.addTab("📊 Báo cáo", baoCaoPanel);
        
        phanQuyenPanel = new PhanQuyenDemoPanel();
        tabbedPane.addTab("🔒 Demo Phân quyền", phanQuyenPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Status Bar
        JPanel statusBar = createStatusBar();
        mainPanel.add(statusBar, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleLogout();
            }
        });
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // Menu Hệ thống
        JMenu menuSystem = new JMenu("Hệ thống");
        
        JMenuItem miRefresh = new JMenuItem("Làm mới dữ liệu");
        miRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
        miRefresh.addActionListener(e -> refreshData());
        
        JMenuItem miChangePassword = new JMenuItem("Đổi mật khẩu");
        miChangePassword.addActionListener(e -> showChangePasswordDialog());
        
        JMenuItem miLogout = new JMenuItem("Đăng xuất");
        miLogout.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        miLogout.addActionListener(e -> handleLogout());
        
        JMenuItem miExit = new JMenuItem("Thoát");
        miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        miExit.addActionListener(e -> System.exit(0));
        
        menuSystem.add(miRefresh);
        menuSystem.add(miChangePassword);
        menuSystem.addSeparator();
        menuSystem.add(miLogout);
        menuSystem.add(miExit);
        
        // Menu Quản lý (chỉ Admin)
        JMenu menuManage = new JMenu("Quản lý");
        
        JMenuItem miNhanVien = new JMenuItem("Nhân viên");
        miNhanVien.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        
        menuManage.add(miNhanVien);
        
        if (SessionManager.getInstance().canAccessSalary()) {
            JMenuItem miLuong = new JMenuItem("Lương thưởng");
            miLuong.addActionListener(e -> tabbedPane.setSelectedIndex(1));
            menuManage.add(miLuong);
        }
        
        // Menu Trợ giúp
        JMenu menuHelp = new JMenu("Trợ giúp");
        
        JMenuItem miAbout = new JMenuItem("Về chương trình");
        miAbout.addActionListener(e -> showAboutDialog());
        
        JMenuItem miGuide = new JMenuItem("Hướng dẫn sử dụng");
        miGuide.addActionListener(e -> showUserGuide());
        
        menuHelp.add(miGuide);
        menuHelp.add(miAbout);
        
        menuBar.add(menuSystem);
        menuBar.add(menuManage);
        menuBar.add(menuHelp);
        
        setJMenuBar(menuBar);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 73, 94));
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Title
        JLabel lblTitle = new JLabel("🏢 HỆ THỐNG QUẢN LÝ LƯƠNG & HỒ SƠ NHÂN SỰ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.BLACK);
        
        // User Info Panel
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        String roleIcon = getRoleIcon(currentUser.getVaiTro());
        lblUserInfo = new JLabel(roleIcon + " " + currentUser.getTenDangNhap());
        lblUserInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserInfo.setForeground(Color.BLACK);
        
        lblRole = new JLabel("(" + currentUser.getTenVaiTro() + ")");
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRole.setForeground(new Color(189, 195, 199));
        
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(e -> handleLogout());
        
        userPanel.add(lblUserInfo);
        userPanel.add(Box.createHorizontalStrut(5));
        userPanel.add(lblRole);
        userPanel.add(Box.createHorizontalStrut(15));
        userPanel.add(btnLogout);
        
        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private String getRoleIcon(String role) {
        switch (role) {
            case "ADMIN": return "👑";
            case "KETOAN": return "💼";
            case "NHANVIEN": return "👤";
            default: return "👤";
        }
    }
    
    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(new Color(236, 240, 241));
        statusBar.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        JLabel lblDb1 = new JLabel("DB1: HR_INFO (Thông tin chung)");
        lblDb1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDb1.setForeground(new Color(39, 174, 96));
        
        String db2Status = SessionManager.getInstance().canAccessSalary() 
            ? "DB2: HR_SALARY (Lương - Có quyền truy cập)" 
            : "DB2: HR_SALARY (Lương - Không có quyền)";
        JLabel lblDb2 = new JLabel(db2Status);
        lblDb2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDb2.setForeground(SessionManager.getInstance().canAccessSalary() 
            ? new Color(39, 174, 96) : new Color(192, 57, 43));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(lblDb1);
        leftPanel.add(lblDb2);
        
        JLabel lblCopyright = new JLabel("© 2026 - Đồ án CSDL Phân tán");
        lblCopyright.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblCopyright.setForeground(new Color(127, 140, 141));
        
        statusBar.add(leftPanel, BorderLayout.WEST);
        statusBar.add(lblCopyright, BorderLayout.EAST);
        
        return statusBar;
    }
    
    private void refreshData() {
        // Refresh tất cả các panel
        if (nhanVienPanel != null) {
            nhanVienPanel.loadData();
        }
        if (luongPanel != null) {
            luongPanel.loadData();
        }
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu!", 
            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JPasswordField txtOldPass = new JPasswordField();
        JPasswordField txtNewPass = new JPasswordField();
        JPasswordField txtConfirmPass = new JPasswordField();
        
        panel.add(new JLabel("Mật khẩu cũ:"));
        panel.add(txtOldPass);
        panel.add(new JLabel("Mật khẩu mới:"));
        panel.add(txtNewPass);
        panel.add(new JLabel("Xác nhận:"));
        panel.add(txtConfirmPass);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Đổi mật khẩu",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String oldPass = new String(txtOldPass.getPassword());
            String newPass = new String(txtNewPass.getPassword());
            String confirmPass = new String(txtConfirmPass.getPassword());
            
            if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(this, "Mật khẩu xác nhận không khớp!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                if (authService.changePassword(oldPass, newPass)) {
                    JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đăng xuất?", "Xác nhận",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            authService.logout();
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
            this.dispose();
        }
    }
    
    private void showAboutDialog() {
        String message = """
            HỆ THỐNG QUẢN LÝ LƯƠNG & HỒ SƠ NHÂN SỰ
            ========================================
            
            Đồ án môn học: Cơ sở dữ liệu phân tán
            
            Kỹ thuật: Phân mảnh dọc (Vertical Fragmentation)
            - DB1 (HR_INFO): Thông tin chung nhân sự
            - DB2 (HR_SALARY): Thông tin lương thưởng (nhạy cảm)
            
            Mục tiêu:
            • Minh họa phân mảnh dọc tách biệt dữ liệu
            • Demo phân quyền truy cập CSDL
            • Đảm bảo tính trong suốt với người dùng
            • Đồng bộ thao tác giữa 2 CSDL
            
            Phiên bản: 1.0
            © 2026
            """;
        
        JOptionPane.showMessageDialog(this, message, "Về chương trình",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showUserGuide() {
        String guide = """
            HƯỚNG DẪN SỬ DỤNG
            =================
            
            1. PHÂN QUYỀN TRUY CẬP:
               • Admin: Toàn quyền cả 2 CSDL
               • Kế toán: Xem DB1 + Quản lý lương DB2
               • Nhân viên: Chỉ xem thông tin chung DB1
               • IT: Quản trị DB1, KHÔNG thấy lương
            
            2. QUẢN LÝ NHÂN VIÊN:
               • Xem danh sách nhân viên
               • Thêm/Sửa/Xóa (chỉ Admin)
               • Tìm kiếm theo tên, phòng ban
            
            3. QUẢN LÝ LƯƠNG (Admin/Kế toán):
               • Xem thông tin lương nhân viên
               • Tính lương tháng
               • Duyệt bảng lương
            
            4. DEMO PHÂN QUYỀN:
               • Test kết nối với các user SQL khác nhau
               • Xem user nào có quyền truy cập DB nào
            
            5. PHÍM TẮT:
               • F5: Làm mới dữ liệu
               • Ctrl+L: Đăng xuất
               • Ctrl+Q: Thoát
            """;
        
        JTextArea textArea = new JTextArea(guide);
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Hướng dẫn sử dụng",
            JOptionPane.INFORMATION_MESSAGE);
    }
}
