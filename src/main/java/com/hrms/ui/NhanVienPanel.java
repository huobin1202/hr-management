package com.hrms.ui;

import com.hrms.model.*;
import com.hrms.service.NhanVienService;
import com.hrms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel quản lý nhân viên - Thao tác trên DB1 (HR_INFO)
 */
public class NhanVienPanel extends JPanel {
    
    private NhanVienService nhanVienService;
    
    // Components
    private JTable tableNhanVien;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<PhongBan> cboPhongBan;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;
    
    // Form fields
    private JTextField txtMaNV, txtHoTen, txtCMND, txtSoDT, txtEmail, txtDiaChi;
    private JComboBox<String> cboGioiTinh;
    private JComboBox<PhongBan> cboPhongBanForm;
    private JComboBox<ChucVu> cboChucVu;
    private JSpinner spnNgaySinh, spnNgayVaoLam;
    
    // Salary fields (chỉ hiển thị cho Admin/Kế toán)
    private JTextField txtLuongCoBan, txtHeSoLuong, txtPhuCapCV, txtPhuCapKhac;
    private JPanel salaryPanel;
    
    private List<PhongBan> dsPhongBan;
    private List<ChucVu> dsChucVu;
    
    public NhanVienPanel() {
        nhanVienService = new NhanVienService();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top Panel - Search and Filter
        JPanel topPanel = createSearchPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center - Table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Right - Form
        JPanel formPanel = createFormPanel();
        add(formPanel, BorderLayout.EAST);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(new TitledBorder("Tìm kiếm & Lọc"));
        
        panel.add(new JLabel("Tìm kiếm:"));
        txtSearch = new JTextField(20);
        txtSearch.addActionListener(e -> searchNhanVien());
        panel.add(txtSearch);
        
        JButton btnSearch = new JButton("🔍 Tìm");
        btnSearch.addActionListener(e -> searchNhanVien());
        panel.add(btnSearch);
        
        panel.add(Box.createHorizontalStrut(20));
        
        panel.add(new JLabel("Phòng ban:"));
        cboPhongBan = new JComboBox<>();
        cboPhongBan.setPreferredSize(new Dimension(150, 25));
        cboPhongBan.addActionListener(e -> filterByPhongBan());
        panel.add(cboPhongBan);
        
        btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Danh sách Nhân viên (DB1: HR_INFO)"));
        
        // Table model
        String[] columns = {"Mã NV", "Họ tên", "Giới tính", "Ngày sinh", "SĐT", "Phòng ban", "Chức vụ", "Ngày vào làm"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableNhanVien = new JTable(tableModel);
        tableNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableNhanVien.setRowHeight(25);
        tableNhanVien.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        tableNhanVien.getColumnModel().getColumn(0).setPreferredWidth(60);
        tableNhanVien.getColumnModel().getColumn(1).setPreferredWidth(150);
        tableNhanVien.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableNhanVien.getColumnModel().getColumn(3).setPreferredWidth(90);
        tableNhanVien.getColumnModel().getColumn(4).setPreferredWidth(100);
        tableNhanVien.getColumnModel().getColumn(5).setPreferredWidth(120);
        tableNhanVien.getColumnModel().getColumn(6).setPreferredWidth(100);
        tableNhanVien.getColumnModel().getColumn(7).setPreferredWidth(90);
        
        tableNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                displaySelectedRow();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableNhanVien);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        btnAdd = new JButton("➕ Thêm mới");
        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.BLACK);
        btnAdd.addActionListener(e -> addNhanVien());
        
        btnEdit = new JButton("✏️ Sửa");
        btnEdit.setBackground(new Color(255, 193, 7));
        btnEdit.addActionListener(e -> editNhanVien());
        
        btnDelete = new JButton("🗑️ Xóa");
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.BLACK);
        btnDelete.addActionListener(e -> deleteNhanVien());
        
        // Chỉ Admin mới có quyền thêm/xóa
        boolean isAdmin = SessionManager.getInstance().isAdmin();
        btnAdd.setEnabled(isAdmin);
        btnDelete.setEnabled(isAdmin);
        btnEdit.setEnabled(isAdmin || SessionManager.getInstance().isKeToan());
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        
        if (!isAdmin) {
            JLabel lblNote = new JLabel("(Chỉ Admin mới có quyền thêm/xóa)");
            lblNote.setForeground(Color.GRAY);
            lblNote.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            buttonPanel.add(lblNote);
        }
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Thông tin chi tiết"));
        panel.setPreferredSize(new Dimension(320, 0));
        
        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Mã NV
        addFormField(fieldsPanel, gbc, row++, "Mã NV:", txtMaNV = new JTextField(15));
        txtMaNV.setEditable(false);
        
        // Họ tên
        addFormField(fieldsPanel, gbc, row++, "Họ tên:", txtHoTen = new JTextField(15));
        
        // Giới tính
        cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        addFormField(fieldsPanel, gbc, row++, "Giới tính:", cboGioiTinh);
        
        // Ngày sinh
        spnNgaySinh = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spnNgaySinh, "dd/MM/yyyy");
        spnNgaySinh.setEditor(dateEditor);
        addFormField(fieldsPanel, gbc, row++, "Ngày sinh:", spnNgaySinh);
        
        // CMND
        addFormField(fieldsPanel, gbc, row++, "CMND/CCCD:", txtCMND = new JTextField(15));
        
        // SĐT
        addFormField(fieldsPanel, gbc, row++, "Số ĐT:", txtSoDT = new JTextField(15));
        
        // Email
        addFormField(fieldsPanel, gbc, row++, "Email:", txtEmail = new JTextField(15));
        
        // Địa chỉ
        addFormField(fieldsPanel, gbc, row++, "Địa chỉ:", txtDiaChi = new JTextField(15));
        
        // Phòng ban
        cboPhongBanForm = new JComboBox<>();
        addFormField(fieldsPanel, gbc, row++, "Phòng ban:", cboPhongBanForm);
        
        // Chức vụ
        cboChucVu = new JComboBox<>();
        addFormField(fieldsPanel, gbc, row++, "Chức vụ:", cboChucVu);
        
        // Ngày vào làm
        spnNgayVaoLam = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor dateEditor2 = new JSpinner.DateEditor(spnNgayVaoLam, "dd/MM/yyyy");
        spnNgayVaoLam.setEditor(dateEditor2);
        addFormField(fieldsPanel, gbc, row++, "Ngày vào làm:", spnNgayVaoLam);
        
        panel.add(fieldsPanel);
        
        // Salary Panel (chỉ hiển thị nếu có quyền)
        if (SessionManager.getInstance().canAccessSalary()) {
            salaryPanel = new JPanel(new GridBagLayout());
            salaryPanel.setBorder(new TitledBorder("Thông tin lương (DB2: HR_SALARY)"));
            
            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.insets = new Insets(5, 5, 5, 5);
            gbc2.fill = GridBagConstraints.HORIZONTAL;
            gbc2.anchor = GridBagConstraints.WEST;
            
            int sRow = 0;
            addFormField(salaryPanel, gbc2, sRow++, "Lương cơ bản:", txtLuongCoBan = new JTextField(12));
            addFormField(salaryPanel, gbc2, sRow++, "Hệ số lương:", txtHeSoLuong = new JTextField(12));
            addFormField(salaryPanel, gbc2, sRow++, "Phụ cấp CV:", txtPhuCapCV = new JTextField(12));
            addFormField(salaryPanel, gbc2, sRow++, "Phụ cấp khác:", txtPhuCapKhac = new JTextField(12));
            
            panel.add(Box.createVerticalStrut(10));
            panel.add(salaryPanel);
        }
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnSave = new JButton("💾 Lưu");
        btnSave.setBackground(new Color(0, 123, 255));
        btnSave.setForeground(Color.BLACK);
        btnSave.addActionListener(e -> saveNhanVien());
        
        JButton btnClear = new JButton("🧹 Xóa form");
        btnClear.addActionListener(e -> clearForm());
        
        actionPanel.add(btnSave);
        actionPanel.add(btnClear);
        
        panel.add(Box.createVerticalStrut(10));
        panel.add(actionPanel);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
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
            // Load phòng ban
            dsPhongBan = nhanVienService.getAllPhongBan();
            cboPhongBan.removeAllItems();
            cboPhongBan.addItem(null); // All
            cboPhongBanForm.removeAllItems();
            for (PhongBan pb : dsPhongBan) {
                cboPhongBan.addItem(pb);
                cboPhongBanForm.addItem(pb);
            }
            
            // Load chức vụ
            dsChucVu = nhanVienService.getAllChucVu();
            cboChucVu.removeAllItems();
            for (ChucVu cv : dsChucVu) {
                cboChucVu.addItem(cv);
            }
            
            // Load nhân viên
            loadNhanVienTable(nhanVienService.getAllNhanVien());
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadNhanVienTable(List<NhanVien> list) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        for (NhanVien nv : list) {
            Object[] row = {
                nv.getMaNV(),
                nv.getHoTen(),
                nv.getGioiTinh(),
                nv.getNgaySinh() != null ? nv.getNgaySinh().format(formatter) : "",
                nv.getSoDienThoai(),
                nv.getTenPhongBan(),
                nv.getTenChucVu(),
                nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().format(formatter) : ""
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchNhanVien() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadData();
            return;
        }
        
        try {
            List<NhanVien> result = nhanVienService.searchByName(keyword);
            loadNhanVienTable(result);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tìm kiếm: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void filterByPhongBan() {
        PhongBan selected = (PhongBan) cboPhongBan.getSelectedItem();
        if (selected == null) {
            loadData();
            return;
        }
        
        try {
            List<NhanVien> result = nhanVienService.getByPhongBan(selected.getMaPB());
            loadNhanVienTable(result);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi lọc: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void displaySelectedRow() {
        int row = tableNhanVien.getSelectedRow();
        if (row < 0) return;
        
        String maNV = (String) tableModel.getValueAt(row, 0);
        
        try {
            NhanVienDayDu nv = nhanVienService.findNhanVienDayDu(maNV);
            if (nv != null) {
                fillForm(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void fillForm(NhanVienDayDu nv) {
        txtMaNV.setText(nv.getMaNV());
        txtHoTen.setText(nv.getHoTen());
        cboGioiTinh.setSelectedItem(nv.getGioiTinh());
        
        if (nv.getNgaySinh() != null) {
            spnNgaySinh.setValue(java.sql.Date.valueOf(nv.getNgaySinh()));
        }
        
        txtCMND.setText(nv.getCmnd());
        txtSoDT.setText(nv.getSoDienThoai());
        txtEmail.setText(nv.getEmail());
        txtDiaChi.setText(nv.getDiaChi());
        
        // Select phòng ban
        for (int i = 0; i < cboPhongBanForm.getItemCount(); i++) {
            PhongBan pb = cboPhongBanForm.getItemAt(i);
            if (pb != null && pb.getMaPB().equals(nv.getMaPB())) {
                cboPhongBanForm.setSelectedIndex(i);
                break;
            }
        }
        
        // Select chức vụ
        for (int i = 0; i < cboChucVu.getItemCount(); i++) {
            ChucVu cv = cboChucVu.getItemAt(i);
            if (cv != null && cv.getMaCV().equals(nv.getMaCV())) {
                cboChucVu.setSelectedIndex(i);
                break;
            }
        }
        
        if (nv.getNgayVaoLam() != null) {
            spnNgayVaoLam.setValue(java.sql.Date.valueOf(nv.getNgayVaoLam()));
        }
        
        // Fill salary info if available
        if (SessionManager.getInstance().canAccessSalary() && nv.getLuongCoBan() != null) {
            txtLuongCoBan.setText(nv.getLuongCoBan().toString());
            txtHeSoLuong.setText(nv.getHeSoLuong().toString());
            txtPhuCapCV.setText(nv.getPhuCapChucVu() != null ? nv.getPhuCapChucVu().toString() : "0");
            txtPhuCapKhac.setText(nv.getPhuCapKhac() != null ? nv.getPhuCapKhac().toString() : "0");
        }
    }
    
    private void clearForm() {
        txtMaNV.setText("");
        txtHoTen.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtCMND.setText("");
        txtSoDT.setText("");
        txtEmail.setText("");
        txtDiaChi.setText("");
        cboPhongBanForm.setSelectedIndex(0);
        cboChucVu.setSelectedIndex(0);
        
        if (SessionManager.getInstance().canAccessSalary()) {
            txtLuongCoBan.setText("");
            txtHeSoLuong.setText("");
            txtPhuCapCV.setText("");
            txtPhuCapKhac.setText("");
        }
        
        tableNhanVien.clearSelection();
    }
    
    private void addNhanVien() {
        if (!SessionManager.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Chỉ Admin mới có quyền thêm nhân viên!",
                "Lỗi phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        clearForm();
        
        // Tạo mã NV tự động
        try {
            String newMaNV = nhanVienService.generateMaNV();
            txtMaNV.setText(newMaNV);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        txtHoTen.requestFocus();
    }
    
    private void editNhanVien() {
        if (tableNhanVien.getSelectedRow() < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        txtHoTen.requestFocus();
    }
    
    private void deleteNhanVien() {
        if (!SessionManager.getInstance().isAdmin()) {
            JOptionPane.showMessageDialog(this, "Chỉ Admin mới có quyền xóa nhân viên!",
                "Lỗi phân quyền", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int row = tableNhanVien.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!",
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String maNV = (String) tableModel.getValueAt(row, 0);
        String hoTen = (String) tableModel.getValueAt(row, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa nhân viên: " + hoTen + " (" + maNV + ")?\n" +
            "Lưu ý: Thao tác này sẽ xóa dữ liệu ở cả DB1 và DB2!",
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean hardDelete = JOptionPane.showConfirmDialog(this,
                    "Xóa vĩnh viễn? (Chọn No để chỉ vô hiệu hóa)",
                    "Loại xóa", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
                
                nhanVienService.deleteNhanVien(maNV, hardDelete);
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                clearForm();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Lỗi xóa: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveNhanVien() {
        // Validate
        if (txtHoTen.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập họ tên!",
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            txtHoTen.requestFocus();
            return;
        }
        
        try {
            NhanVien nv = new NhanVien();
            nv.setMaNV(txtMaNV.getText().trim());
            nv.setHoTen(txtHoTen.getText().trim());
            nv.setGioiTinh((String) cboGioiTinh.getSelectedItem());
            
            java.util.Date ngaySinh = (java.util.Date) spnNgaySinh.getValue();
            nv.setNgaySinh(new java.sql.Date(ngaySinh.getTime()).toLocalDate());
            
            nv.setCmnd(txtCMND.getText().trim());
            nv.setSoDienThoai(txtSoDT.getText().trim());
            nv.setEmail(txtEmail.getText().trim());
            nv.setDiaChi(txtDiaChi.getText().trim());
            
            PhongBan pb = (PhongBan) cboPhongBanForm.getSelectedItem();
            if (pb != null) nv.setMaPB(pb.getMaPB());
            
            ChucVu cv = (ChucVu) cboChucVu.getSelectedItem();
            if (cv != null) nv.setMaCV(cv.getMaCV());
            
            java.util.Date ngayVaoLam = (java.util.Date) spnNgayVaoLam.getValue();
            nv.setNgayVaoLam(new java.sql.Date(ngayVaoLam.getTime()).toLocalDate());
            
            // Thông tin lương
            LuongNhanVien luong = null;
            if (SessionManager.getInstance().canAccessSalary()) {
                luong = new LuongNhanVien();
                luong.setMaNV(nv.getMaNV());
                
                String luongCoBan = txtLuongCoBan.getText().trim();
                luong.setLuongCoBan(luongCoBan.isEmpty() ? new BigDecimal("10000000") : new BigDecimal(luongCoBan));
                
                String heSo = txtHeSoLuong.getText().trim();
                luong.setHeSoLuong(heSo.isEmpty() ? BigDecimal.ONE : new BigDecimal(heSo));
                
                String phuCapCV = txtPhuCapCV.getText().trim();
                luong.setPhuCapChucVu(phuCapCV.isEmpty() ? BigDecimal.ZERO : new BigDecimal(phuCapCV));
                
                String phuCapKhac = txtPhuCapKhac.getText().trim();
                luong.setPhuCapKhac(phuCapKhac.isEmpty() ? BigDecimal.ZERO : new BigDecimal(phuCapKhac));
            }
            
            // Kiểm tra thêm mới hay cập nhật
            boolean isNew = tableNhanVien.getSelectedRow() < 0;
            
            if (isNew) {
                nhanVienService.addNhanVien(nv, luong);
                JOptionPane.showMessageDialog(this, 
                    "Thêm nhân viên thành công!\n" +
                    "Dữ liệu đã được lưu vào:\n" +
                    "- DB1 (HR_INFO): Thông tin chung\n" +
                    "- DB2 (HR_SALARY): Thông tin lương",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                nhanVienService.updateNhanVien(nv, luong);
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            }
            
            loadData();
            clearForm();
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số liệu không hợp lệ!",
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi lưu dữ liệu: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
