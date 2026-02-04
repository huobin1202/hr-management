package com.hrms.ui;

import com.hrms.model.*;
import com.hrms.service.LuongService;
import com.hrms.service.NhanVienService;
import com.hrms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel quản lý lương - Thao tác trên DB2 (HR_SALARY)
 * Chỉ dành cho Admin và Kế toán
 */
public class LuongPanel extends JPanel {
    
    private LuongService luongService;
    private NhanVienService nhanVienService;
    
    // Components
    private JTable tableLuong;
    private JTable tableBangLuongThang;
    private DefaultTableModel luongTableModel;
    private DefaultTableModel bangLuongModel;
    
    private JComboBox<Integer> cboThang, cboNam;
    private JTextField txtSearchNV;
    private JButton btnTinhLuong, btnInBangLuong;
    
    // Form fields for salary adjustment
    private JTextField txtMaNV, txtHoTen, txtLuongCoBan, txtHeSoLuong;
    private JTextField txtPhuCapCV, txtPhuCapKhac, txtThuong, txtKhauTru, txtGhiChu;
    
    private DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");
    
    public LuongPanel() {
        luongService = new LuongService();
        nhanVienService = new NhanVienService();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header panel with role info
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        // Top - Bảng lương cơ bản
        JPanel luongCobanPanel = createLuongCobanPanel();
        splitPane.setTopComponent(luongCobanPanel);
        
        // Bottom - Bảng lương tháng
        JPanel bangLuongThangPanel = createBangLuongThangPanel();
        splitPane.setBottomComponent(bangLuongThangPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Right - Form điều chỉnh
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.EAST);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        // Info label
        JLabel lblInfo = new JLabel("📊 Quản lý Lương - Dữ liệu từ DB2: HR_SALARY");
        lblInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblInfo.setForeground(new Color(0, 100, 0));
        panel.add(lblInfo, BorderLayout.WEST);
        
        // Role indicator
        String role = SessionManager.getInstance().getCurrentUser().getVaiTro();
        JLabel lblRole = new JLabel("Vai trò: " + role);
        lblRole.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblRole.setForeground(Color.BLUE);
        panel.add(lblRole, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createLuongCobanPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Bảng lương cơ bản nhân viên"));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Tìm kiếm NV:"));
        txtSearchNV = new JTextField(15);
        txtSearchNV.addActionListener(e -> searchLuong());
        searchPanel.add(txtSearchNV);
        
        JButton btnSearch = new JButton("🔍");
        btnSearch.addActionListener(e -> searchLuong());
        searchPanel.add(btnSearch);
        
        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        searchPanel.add(btnRefresh);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Mã NV", "Họ tên", "Lương cơ bản", "Hệ số", "Phụ cấp CV", "Phụ cấp khác", "Ngày hiệu lực"};
        luongTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableLuong = new JTable(luongTableModel);
        tableLuong.setRowHeight(25);
        tableLuong.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Format currency columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tableLuong.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tableLuong.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tableLuong.getColumnModel().getColumn(5).setCellRenderer(rightRenderer);
        
        tableLuong.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedLuong();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableLuong);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBangLuongThangPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(new TitledBorder("Bảng lương theo tháng"));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        filterPanel.add(new JLabel("Tháng:"));
        cboThang = new JComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cboThang.addItem(i);
        }
        cboThang.setSelectedItem(LocalDate.now().getMonthValue());
        filterPanel.add(cboThang);
        
        filterPanel.add(new JLabel("Năm:"));
        cboNam = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear + 1; i++) {
            cboNam.addItem(i);
        }
        cboNam.setSelectedItem(currentYear);
        filterPanel.add(cboNam);
        
        JButton btnXem = new JButton("📋 Xem bảng lương");
        btnXem.addActionListener(e -> loadBangLuongThang());
        filterPanel.add(btnXem);
        
        filterPanel.add(Box.createHorizontalStrut(20));
        
        btnTinhLuong = new JButton("💰 Tính lương tháng");
        btnTinhLuong.setBackground(new Color(40, 167, 69));
        btnTinhLuong.setForeground(Color.BLACK);
        btnTinhLuong.addActionListener(e -> tinhLuongThang());
        btnTinhLuong.setEnabled(SessionManager.getInstance().isAdmin());
        filterPanel.add(btnTinhLuong);
        
        btnInBangLuong = new JButton("🖨️ In bảng lương");
        btnInBangLuong.addActionListener(e -> inBangLuong());
        filterPanel.add(btnInBangLuong);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Mã NV", "Họ tên", "Lương CB", "Hệ số", "Phụ cấp", "Thưởng", "Khấu trừ", "Thực lĩnh", "Trạng thái"};
        bangLuongModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableBangLuongThang = new JTable(bangLuongModel);
        tableBangLuongThang.setRowHeight(25);
        
        // Format currency columns
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        for (int i = 2; i <= 7; i++) {
            tableBangLuongThang.getColumnModel().getColumn(i).setCellRenderer(rightRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(tableBangLuongThang);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        summaryPanel.add(new JLabel("Tổng cộng: "));
        JLabel lblTongCong = new JLabel("0 VNĐ");
        lblTongCong.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTongCong.setForeground(new Color(0, 100, 0));
        summaryPanel.add(lblTongCong);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Điều chỉnh lương"));
        panel.setPreferredSize(new Dimension(280, 0));
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Mã NV (readonly)
        addField(fieldsPanel, gbc, row++, "Mã NV:", txtMaNV = new JTextField(12));
        txtMaNV.setEditable(false);
        
        // Họ tên (readonly)
        addField(fieldsPanel, gbc, row++, "Họ tên:", txtHoTen = new JTextField(12));
        txtHoTen.setEditable(false);
        
        // Lương cơ bản
        addField(fieldsPanel, gbc, row++, "Lương cơ bản:", txtLuongCoBan = new JTextField(12));
        
        // Hệ số lương
        addField(fieldsPanel, gbc, row++, "Hệ số lương:", txtHeSoLuong = new JTextField(12));
        
        // Phụ cấp chức vụ
        addField(fieldsPanel, gbc, row++, "Phụ cấp CV:", txtPhuCapCV = new JTextField(12));
        
        // Phụ cấp khác
        addField(fieldsPanel, gbc, row++, "Phụ cấp khác:", txtPhuCapKhac = new JTextField(12));
        
        // Separator
        gbc.gridx = 0; gbc.gridy = row++; gbc.gridwidth = 2;
        fieldsPanel.add(new JSeparator(), gbc);
        gbc.gridwidth = 1;
        
        // Thưởng (cho bảng lương tháng)
        addField(fieldsPanel, gbc, row++, "Thưởng:", txtThuong = new JTextField(12));
        
        // Khấu trừ
        addField(fieldsPanel, gbc, row++, "Khấu trừ:", txtKhauTru = new JTextField(12));
        
        // Ghi chú
        addField(fieldsPanel, gbc, row++, "Ghi chú:", txtGhiChu = new JTextField(12));
        
        panel.add(fieldsPanel);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnCapNhat = new JButton("💾 Cập nhật lương CB");
        btnCapNhat.setBackground(new Color(0, 123, 255));
        btnCapNhat.setForeground(Color.BLACK);
        btnCapNhat.addActionListener(e -> capNhatLuongCoBan());
        btnCapNhat.setEnabled(SessionManager.getInstance().isAdmin());
        
        JButton btnThemThuong = new JButton("🎁 Thêm thưởng");
        btnThemThuong.setBackground(new Color(255, 193, 7));
        btnThemThuong.addActionListener(e -> themThuong());
        
        JButton btnClear = new JButton("🧹 Xóa");
        btnClear.addActionListener(e -> clearForm());
        
        buttonPanel.add(btnCapNhat);
        buttonPanel.add(btnThemThuong);
        buttonPanel.add(btnClear);
        
        panel.add(buttonPanel);
        
        // Add statistics panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(new TitledBorder("Thống kê nhanh"));
        
        JLabel lblAvgSalary = new JLabel("Lương TB: -");
        JLabel lblMinSalary = new JLabel("Lương thấp nhất: -");
        JLabel lblMaxSalary = new JLabel("Lương cao nhất: -");
        
        statsPanel.add(lblAvgSalary);
        statsPanel.add(lblMinSalary);
        statsPanel.add(lblMaxSalary);
        
        panel.add(Box.createVerticalStrut(20));
        panel.add(statsPanel);
        
        return panel;
    }
    
    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }
    
    public void loadData() {
        try {
            List<LuongNhanVien> danhSachLuong = luongService.getAllLuongNhanVien();
            luongTableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            for (LuongNhanVien luong : danhSachLuong) {
                Object[] rowData = {
                    luong.getMaNV(),
                    luong.getHoTen(),
                    currencyFormat.format(luong.getLuongCoBan()),
                    luong.getHeSoLuong(),
                    currencyFormat.format(luong.getPhuCapChucVu() != null ? luong.getPhuCapChucVu() : BigDecimal.ZERO),
                    currencyFormat.format(luong.getPhuCapKhac() != null ? luong.getPhuCapKhac() : BigDecimal.ZERO),
                    luong.getNgayHieuLuc() != null ? luong.getNgayHieuLuc().format(formatter) : ""
                };
                luongTableModel.addRow(rowData);
            }
            
            // Load bảng lương tháng hiện tại
            loadBangLuongThang();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void searchLuong() {
        String keyword = txtSearchNV.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<LuongNhanVien> result = luongService.searchLuong(keyword);
            luongTableModel.setRowCount(0);
            
            for (LuongNhanVien luong : result) {
                Object[] rowData = {
                    luong.getMaNV(),
                    luong.getHoTen(),
                    currencyFormat.format(luong.getLuongCoBan()),
                    luong.getHeSoLuong(),
                    currencyFormat.format(luong.getPhuCapChucVu() != null ? luong.getPhuCapChucVu() : BigDecimal.ZERO),
                    currencyFormat.format(luong.getPhuCapKhac() != null ? luong.getPhuCapKhac() : BigDecimal.ZERO),
                    ""
                };
                luongTableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadBangLuongThang() {
        try {
            int thang = (Integer) cboThang.getSelectedItem();
            int nam = (Integer) cboNam.getSelectedItem();
            
            List<BangLuongThang> danhSach = luongService.getBangLuongThang(thang, nam);
            bangLuongModel.setRowCount(0);
            
            BigDecimal tongCong = BigDecimal.ZERO;
            
            for (BangLuongThang bl : danhSach) {
                Object[] rowData = {
                    bl.getMaNV(),
                    bl.getHoTen(),
                    currencyFormat.format(bl.getLuongCoBan()),
                    bl.getHeSoLuong(),
                    currencyFormat.format(bl.getTongPhuCap()),
                    currencyFormat.format(bl.getThuong() != null ? bl.getThuong() : BigDecimal.ZERO),
                    currencyFormat.format(bl.getKhauTru() != null ? bl.getKhauTru() : BigDecimal.ZERO),
                    currencyFormat.format(bl.getThucLinh()),
                    bl.getTrangThai()
                };
                bangLuongModel.addRow(rowData);
                tongCong = tongCong.add(bl.getThucLinh());
            }
            
            // Update summary (would need reference to label)
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải bảng lương: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displaySelectedLuong() {
        int row = tableLuong.getSelectedRow();
        if (row < 0) return;
        
        txtMaNV.setText((String) luongTableModel.getValueAt(row, 0));
        txtHoTen.setText((String) luongTableModel.getValueAt(row, 1));
        
        // Parse currency back to number
        String luongCB = ((String) luongTableModel.getValueAt(row, 2)).replace(",", "").replace(" VNĐ", "");
        txtLuongCoBan.setText(luongCB);
        
        txtHeSoLuong.setText(String.valueOf(luongTableModel.getValueAt(row, 3)));
        
        String phuCapCV = ((String) luongTableModel.getValueAt(row, 4)).replace(",", "").replace(" VNĐ", "");
        txtPhuCapCV.setText(phuCapCV);
        
        String phuCapKhac = ((String) luongTableModel.getValueAt(row, 5)).replace(",", "").replace(" VNĐ", "");
        txtPhuCapKhac.setText(phuCapKhac);
    }
    
    private void clearForm() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        txtLuongCoBan.setText("");
        txtHeSoLuong.setText("");
        txtPhuCapCV.setText("");
        txtPhuCapKhac.setText("");
        txtThuong.setText("");
        txtKhauTru.setText("");
        txtGhiChu.setText("");
        tableLuong.clearSelection();
    }
    
    private void capNhatLuongCoBan() {
        if (!SessionManager.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Chỉ Admin mới có quyền cập nhật lương cơ bản!",
                "Lỗi phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            LuongNhanVien luong = new LuongNhanVien();
            luong.setMaNV(maNV);
            luong.setLuongCoBan(new BigDecimal(txtLuongCoBan.getText().trim()));
            luong.setHeSoLuong(new BigDecimal(txtHeSoLuong.getText().trim()));
            luong.setPhuCapChucVu(new BigDecimal(txtPhuCapCV.getText().trim()));
            luong.setPhuCapKhac(new BigDecimal(txtPhuCapKhac.getText().trim()));
            
            luongService.updateLuong(luong);
            
            JOptionPane.showMessageDialog(this, "Cập nhật lương cơ bản thành công!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            loadData();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số liệu không hợp lệ!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi cập nhật: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void themThuong() {
        String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String thuongStr = txtThuong.getText().trim();
        if (thuongStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số tiền thưởng!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            BigDecimal soTien = new BigDecimal(thuongStr);
            String ghiChu = txtGhiChu.getText().trim();
            if (ghiChu.isEmpty()) ghiChu = "Thưởng";
            
            int thang = (Integer) cboThang.getSelectedItem();
            int nam = (Integer) cboNam.getSelectedItem();
            
            luongService.addThuong(maNV, thang, nam, soTien, ghiChu);
            
            JOptionPane.showMessageDialog(this, "Thêm thưởng thành công!",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            loadBangLuongThang();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số tiền không hợp lệ!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi thêm thưởng: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void tinhLuongThang() {
        if (!SessionManager.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Chỉ Admin mới có quyền tính lương!",
                "Lỗi phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int thang = (Integer) cboThang.getSelectedItem();
        int nam = (Integer) cboNam.getSelectedItem();
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Tính lương tháng " + thang + "/" + nam + " cho tất cả nhân viên?",
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                int count = luongService.tinhLuongThang(thang, nam);
                
                JOptionPane.showMessageDialog(this, 
                    "Đã tính lương cho " + count + " nhân viên!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                loadBangLuongThang();
                
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi tính lương: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void inBangLuong() {
        int thang = (Integer) cboThang.getSelectedItem();
        int nam = (Integer) cboNam.getSelectedItem();
        
        // Tạo dialog preview
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Bảng lương tháng " + thang + "/" + nam, true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        
        StringBuilder sb = new StringBuilder();
        sb.append("=" .repeat(70)).append("\n");
        sb.append("                    BẢNG LƯƠNG THÁNG ").append(thang).append("/").append(nam).append("\n");
        sb.append("=" .repeat(70)).append("\n\n");
        
        sb.append(String.format("%-10s %-20s %15s %15s %15s\n", 
            "Mã NV", "Họ tên", "Lương CB", "Phụ cấp", "Thực lĩnh"));
        sb.append("-".repeat(70)).append("\n");
        
        BigDecimal tongCong = BigDecimal.ZERO;
        for (int i = 0; i < bangLuongModel.getRowCount(); i++) {
            String maNV = (String) bangLuongModel.getValueAt(i, 0);
            String hoTen = (String) bangLuongModel.getValueAt(i, 1);
            String luongCB = (String) bangLuongModel.getValueAt(i, 2);
            String phuCap = (String) bangLuongModel.getValueAt(i, 4);
            String thucLinh = (String) bangLuongModel.getValueAt(i, 7);
            
            sb.append(String.format("%-10s %-20s %15s %15s %15s\n", 
                maNV, hoTen, luongCB, phuCap, thucLinh));
        }
        
        sb.append("-".repeat(70)).append("\n");
        sb.append("\n\nNgày in: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        sb.append("\nNgười lập: ").append(SessionManager.getInstance().getCurrentUser().getHoTen());
        
        textArea.setText(sb.toString());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel();
        JButton btnPrint = new JButton("🖨️ In");
        btnPrint.addActionListener(e -> {
            try {
                textArea.print();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi in: " + ex.getMessage());
            }
        });
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnPrint);
        buttonPanel.add(btnClose);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}
