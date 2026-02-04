package com.hrms.ui;

import com.hrms.model.NguoiDung;
import com.hrms.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Form đăng nhập hệ thống
 */
public class LoginFrame extends JFrame {
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnExit;
    private JLabel lblStatus;
    
    private AuthService authService;
    
    public LoginFrame() {
        authService = new AuthService();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Đăng nhập - Hệ thống Quản lý Lương & Hồ sơ Nhân sự");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Main panel với gradient background
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(66, 133, 244);
                Color color2 = new Color(52, 103, 190);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel lblTitle = new JLabel("HỆ THỐNG QUẢN LÝ LƯƠNG & NHÂN SỰ");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.BLACK);
        titlePanel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Phân mảnh dọc CSDL - Đồ án môn học");
        lblSubtitle.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblSubtitle.setForeground(new Color(200, 220, 255));
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        headerPanel.setOpaque(false);
        headerPanel.add(titlePanel);
        
        JPanel subtitlePanel = new JPanel();
        subtitlePanel.setOpaque(false);
        subtitlePanel.add(lblSubtitle);
        headerPanel.add(subtitlePanel);
        
        // Login Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setOpaque(false);
        formPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username
        JLabel lblUsername = new JLabel("Tên đăng nhập:");
        lblUsername.setForeground(Color.BLACK);
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(lblUsername, gbc);
        
        txtUsername = new JTextField(20);
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setPreferredSize(new Dimension(200, 35));
        txtUsername.setMinimumSize(new Dimension(200, 35));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1;
        formPanel.add(txtUsername, gbc);
        
        // Password
        JLabel lblPassword = new JLabel("Mật khẩu:");
        lblPassword.setForeground(Color.BLACK);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(lblPassword, gbc);
        
        txtPassword = new JPasswordField(20);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setPreferredSize(new Dimension(200, 35));
        txtPassword.setMinimumSize(new Dimension(200, 35));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1;
        formPanel.add(txtPassword, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setOpaque(false);
        
        btnLogin = new JButton("Đăng nhập");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogin.setPreferredSize(new Dimension(120, 35));
        btnLogin.setBackground(new Color(40, 167, 69));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFocusPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnExit = new JButton("Thoát");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExit.setPreferredSize(new Dimension(120, 35));
        btnExit.setBackground(new Color(220, 53, 69));
        btnExit.setForeground(Color.BLACK);
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);
        
        // Status Label
        lblStatus = new JLabel(" ");
        lblStatus.setForeground(new Color(255, 200, 200));
        lblStatus.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        formPanel.add(lblStatus, gbc);
        
        // Info Panel
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 2, 2));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JLabel lblInfo = new JLabel("Tài khoản demo:", SwingConstants.CENTER);
        lblInfo.setForeground(new Color(200, 220, 255));
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        
        JLabel lblAdmin = new JLabel("Admin: admin / admin123", SwingConstants.CENTER);
        lblAdmin.setForeground(new Color(200, 220, 255));
        lblAdmin.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel lblKetoan = new JLabel("Kế toán: ketoan / ketoan123", SwingConstants.CENTER);
        lblKetoan.setForeground(new Color(200, 220, 255));
        lblKetoan.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        JLabel lblNhanvien = new JLabel("Nhân viên: nhanvien / nhanvien123", SwingConstants.CENTER);
        lblNhanvien.setForeground(new Color(200, 220, 255));
        lblNhanvien.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        infoPanel.add(lblInfo);
        infoPanel.add(lblAdmin);
        infoPanel.add(lblKetoan);
        infoPanel.add(lblNhanvien);
        
        // Add to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Event handlers
        btnLogin.addActionListener(e -> handleLogin());
        btnExit.addActionListener(e -> System.exit(0));
        
        // Enter key to login
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
                }
            }
        });
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }
        
        lblStatus.setText("Đang đăng nhập...");
        btnLogin.setEnabled(false);
        
        // Thực hiện đăng nhập trong background thread
        SwingWorker<NguoiDung, Void> worker = new SwingWorker<>() {
            @Override
            protected NguoiDung doInBackground() throws Exception {
                return authService.login(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    NguoiDung user = get();
                    if (user != null) {
                        lblStatus.setText("Đăng nhập thành công!");
                        
                        // Mở màn hình chính
                        SwingUtilities.invokeLater(() -> {
                            MainFrame mainFrame = new MainFrame(user);
                            mainFrame.setVisible(true);
                            LoginFrame.this.dispose();
                        });
                    } else {
                        lblStatus.setText("Sai tên đăng nhập hoặc mật khẩu!");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception e) {
                    lblStatus.setText("Lỗi kết nối: " + e.getMessage());
                    e.printStackTrace();
                } finally {
                    btnLogin.setEnabled(true);
                }
            }
        };
        
        worker.execute();
    }
}
