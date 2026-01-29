package com.hrms.ui;

import com.hrms.service.LuongService;
import com.hrms.service.NhanVienService;
import com.hrms.util.SessionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Map;

/**
 * Panel báo cáo thống kê
 * Kết hợp dữ liệu từ cả DB1 và DB2
 */
public class BaoCaoPanel extends JPanel {
    
    private NhanVienService nhanVienService;
    private LuongService luongService;
    
    private JComboBox<Integer> cboNam;
    private JTable tableThongKe;
    private DefaultTableModel tableModel;
    
    private JLabel lblTongNV, lblTongLuong, lblLuongTB, lblNVMoi;
    
    private DecimalFormat currencyFormat = new DecimalFormat("#,### VNĐ");
    private DecimalFormat numberFormat = new DecimalFormat("#,###");
    
    public BaoCaoPanel() {
        nhanVienService = new NhanVienService();
        luongService = new LuongService();
        initComponents();
        loadData();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel lblTitle = new JLabel("📊 BÁO CÁO THỐNG KÊ NHÂN SỰ & LƯƠNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(new Color(0, 51, 102));
        headerPanel.add(lblTitle, BorderLayout.WEST);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Năm:"));
        cboNam = new JComboBox<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            cboNam.addItem(i);
        }
        cboNam.setSelectedItem(currentYear);
        cboNam.addActionListener(e -> loadData());
        filterPanel.add(cboNam);
        
        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        filterPanel.add(btnRefresh);
        
        JButton btnExport = new JButton("📤 Xuất báo cáo");
        btnExport.addActionListener(e -> exportReport());
        filterPanel.add(btnExport);
        
        headerPanel.add(filterPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        
        // Summary cards
        JPanel summaryPanel = createSummaryPanel();
        mainPanel.add(summaryPanel, BorderLayout.NORTH);
        
        // Charts and tables
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Thống kê theo phòng ban
        JPanel phongBanPanel = createPhongBanStatsPanel();
        tabbedPane.addTab("📈 Theo Phòng ban", phongBanPanel);
        
        // Tab 2: Thống kê lương theo tháng
        JPanel luongThangPanel = createLuongThangPanel();
        tabbedPane.addTab("💰 Lương theo Tháng", luongThangPanel);
        
        // Tab 3: Thống kê nhân sự
        JPanel nhanSuPanel = createNhanSuStatsPanel();
        tabbedPane.addTab("👥 Biến động Nhân sự", nhanSuPanel);
        
        // Tab 4: Phân tích dữ liệu phân mảnh
        JPanel fragmentPanel = createFragmentAnalysisPanel();
        tabbedPane.addTab("🗄️ Phân tích Phân mảnh", fragmentPanel);
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Card 1: Tổng nhân viên
        JPanel card1 = createSummaryCard("👥 Tổng nhân viên", "0", new Color(0, 123, 255));
        lblTongNV = (JLabel) ((JPanel) card1.getComponent(1)).getComponent(0);
        panel.add(card1);
        
        // Card 2: Tổng quỹ lương
        JPanel card2 = createSummaryCard("💵 Tổng quỹ lương/tháng", "0 VNĐ", new Color(40, 167, 69));
        lblTongLuong = (JLabel) ((JPanel) card2.getComponent(1)).getComponent(0);
        panel.add(card2);
        
        // Card 3: Lương trung bình
        JPanel card3 = createSummaryCard("📊 Lương trung bình", "0 VNĐ", new Color(255, 193, 7));
        lblLuongTB = (JLabel) ((JPanel) card3.getComponent(1)).getComponent(0);
        panel.add(card3);
        
        // Card 4: NV mới trong năm
        JPanel card4 = createSummaryCard("🆕 NV mới trong năm", "0", new Color(220, 53, 69));
        lblNVMoi = (JLabel) ((JPanel) card4.getComponent(1)).getComponent(0);
        panel.add(card4);
        
        return panel;
    }
    
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTitle.setForeground(Color.GRAY);
        card.add(lblTitle, BorderLayout.NORTH);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        valuePanel.setBackground(Color.WHITE);
        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(color);
        valuePanel.add(lblValue);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createPhongBanStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Table
        String[] columns = {"Phòng ban", "Số NV", "Tổng lương", "Lương TB", "Lương cao nhất", "Lương thấp nhất"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableThongKe = new JTable(tableModel);
        tableThongKe.setRowHeight(30);
        tableThongKe.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(tableThongKe);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Chart placeholder (simple bar representation)
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSimpleBarChart(g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(0, 200));
        chartPanel.setBorder(new TitledBorder("Biểu đồ số lượng nhân viên theo phòng ban"));
        chartPanel.setBackground(Color.WHITE);
        panel.add(chartPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void drawSimpleBarChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = g.getClipBounds().width;
        int height = g.getClipBounds().height;
        int padding = 50;
        int chartWidth = width - 2 * padding;
        int chartHeight = height - 2 * padding;
        
        // Draw axes
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, height - padding, width - padding, height - padding); // X
        g2d.drawLine(padding, padding, padding, height - padding); // Y
        
        // Draw bars based on table data
        int numBars = tableModel.getRowCount();
        if (numBars == 0) {
            g2d.drawString("Không có dữ liệu", width / 2 - 50, height / 2);
            return;
        }
        
        int barWidth = Math.max(30, chartWidth / (numBars * 2));
        int maxValue = 1;
        
        // Find max value
        for (int i = 0; i < numBars; i++) {
            int value = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            if (value > maxValue) maxValue = value;
        }
        
        Color[] colors = {
            new Color(0, 123, 255), new Color(40, 167, 69), new Color(255, 193, 7),
            new Color(220, 53, 69), new Color(111, 66, 193), new Color(23, 162, 184)
        };
        
        for (int i = 0; i < numBars; i++) {
            String label = (String) tableModel.getValueAt(i, 0);
            int value = Integer.parseInt(tableModel.getValueAt(i, 1).toString());
            
            int barHeight = (int) ((double) value / maxValue * chartHeight * 0.8);
            int x = padding + i * (barWidth + 20) + 20;
            int y = height - padding - barHeight;
            
            g2d.setColor(colors[i % colors.length]);
            g2d.fillRect(x, y, barWidth, barHeight);
            
            // Label
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            String shortLabel = label.length() > 8 ? label.substring(0, 8) + ".." : label;
            g2d.drawString(shortLabel, x, height - padding + 15);
            g2d.drawString(String.valueOf(value), x + barWidth / 4, y - 5);
        }
    }
    
    private JPanel createLuongThangPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Tháng", "Số NV được tính lương", "Tổng chi lương", "Tổng thưởng", "Tổng khấu trừ"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        JTable table = new JTable(model);
        table.setRowHeight(28);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load data
        try {
            int nam = (Integer) cboNam.getSelectedItem();
            
            // Mock data for now - in real app, get from luongService
            for (int thang = 1; thang <= LocalDate.now().getMonthValue(); thang++) {
                model.addRow(new Object[]{
                    "Tháng " + thang,
                    "-",
                    "-",
                    "-",
                    "-"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return panel;
    }
    
    private JPanel createNhanSuStatsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        String[] columns = {"Tháng", "NV mới", "NV nghỉ việc", "Biến động"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        JTable table = new JTable(model);
        table.setRowHeight(28);
        
        // Mock data
        for (int i = 1; i <= 12; i++) {
            model.addRow(new Object[]{
                "Tháng " + i, 0, 0, "0"
            });
        }
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFragmentAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(new Color(245, 245, 245));
        
        StringBuilder sb = new StringBuilder();
        sb.append("╔═══════════════════════════════════════════════════════════════════════════╗\n");
        sb.append("║               PHÂN TÍCH CẤU TRÚC PHÂN MẢNH DỌC                           ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════════════════════╣\n");
        sb.append("║                                                                           ║\n");
        sb.append("║  ┌─────────────────────────────┐    ┌─────────────────────────────┐      ║\n");
        sb.append("║  │     DB1: HR_INFO            │    │     DB2: HR_SALARY          │      ║\n");
        sb.append("║  │   (Thông tin công khai)     │    │   (Thông tin bảo mật)       │      ║\n");
        sb.append("║  ├─────────────────────────────┤    ├─────────────────────────────┤      ║\n");
        sb.append("║  │ • NhanVien (thông tin chung)│    │ • LuongNhanVien             │      ║\n");
        sb.append("║  │ • PhongBan                  │    │ • BangLuongThang            │      ║\n");
        sb.append("║  │ • ChucVu                    │    │ • LichSuLuong               │      ║\n");
        sb.append("║  │ • NguoiDung                 │    │ • Thuong                    │      ║\n");
        sb.append("║  │ • LogHoatDong               │    │                             │      ║\n");
        sb.append("║  └──────────────┬──────────────┘    └──────────────┬──────────────┘      ║\n");
        sb.append("║                 │                                  │                      ║\n");
        sb.append("║                 │      ┌──────────────────┐        │                      ║\n");
        sb.append("║                 └──────┤  MaNV (Khóa chính)├───────┘                      ║\n");
        sb.append("║                        │  Liên kết 2 DB   │                              ║\n");
        sb.append("║                        └──────────────────┘                              ║\n");
        sb.append("║                                                                           ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════════════════════╣\n");
        sb.append("║  PHÂN QUYỀN TRUY CẬP:                                                    ║\n");
        sb.append("║  ────────────────────                                                    ║\n");
        sb.append("║  • Admin     : Toàn quyền cả 2 database                                  ║\n");
        sb.append("║  • Kế toán   : Truy cập DB1 + DB2 (xem và sửa lương)                     ║\n");
        sb.append("║  • Nhân viên : Chỉ DB1 (xem thông tin công khai)                         ║\n");
        sb.append("║  • IT        : Chỉ DB1 (quản lý người dùng)                              ║\n");
        sb.append("║                                                                           ║\n");
        sb.append("╠═══════════════════════════════════════════════════════════════════════════╣\n");
        sb.append("║  ƯU ĐIỂM PHÂN MẢNH DỌC:                                                  ║\n");
        sb.append("║  ─────────────────────                                                   ║\n");
        sb.append("║  ✓ Bảo mật: Dữ liệu lương tách biệt, kiểm soát truy cập độc lập         ║\n");
        sb.append("║  ✓ Hiệu năng: Query nhanh hơn do bảng nhỏ hơn                            ║\n");
        sb.append("║  ✓ Bảo trì: Dễ backup/restore theo nhóm dữ liệu                          ║\n");
        sb.append("║  ✓ Mở rộng: Có thể scale từng DB riêng biệt                              ║\n");
        sb.append("║                                                                           ║\n");
        sb.append("╚═══════════════════════════════════════════════════════════════════════════╝\n");
        
        textArea.setText(sb.toString());
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel with connection test
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.setBorder(new TitledBorder("Kiểm tra kết nối"));
        
        JButton btnTestDB1 = new JButton("🔌 Test DB1");
        btnTestDB1.addActionListener(e -> testConnection("DB1"));
        
        JButton btnTestDB2 = new JButton("🔌 Test DB2");
        btnTestDB2.addActionListener(e -> testConnection("DB2"));
        
        JButton btnTestIntegrity = new JButton("✅ Kiểm tra toàn vẹn");
        btnTestIntegrity.addActionListener(e -> checkIntegrity());
        
        bottomPanel.add(btnTestDB1);
        bottomPanel.add(btnTestDB2);
        bottomPanel.add(btnTestIntegrity);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void testConnection(String db) {
        try {
            boolean success = db.equals("DB1") ? 
                nhanVienService.testConnection() : 
                luongService.testConnection();
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Kết nối " + db + " thành công!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Kết nối " + db + " thất bại!",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi kết nối " + db + ": " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void checkIntegrity() {
        try {
            Map<String, Object> result = nhanVienService.checkDataIntegrity();
            
            StringBuilder sb = new StringBuilder();
            sb.append("KẾT QUẢ KIỂM TRA TOÀN VẸN DỮ LIỆU:\n\n");
            sb.append("Tổng NV trong DB1: ").append(result.get("totalDB1")).append("\n");
            sb.append("Tổng NV trong DB2: ").append(result.get("totalDB2")).append("\n");
            sb.append("NV có trong DB1 nhưng không có trong DB2: ").append(result.get("missingInDB2")).append("\n");
            sb.append("NV có trong DB2 nhưng không có trong DB1: ").append(result.get("missingInDB1")).append("\n");
            
            boolean isValid = (int) result.get("missingInDB2") == 0 && (int) result.get("missingInDB1") == 0;
            
            if (isValid) {
                sb.append("\n✅ Dữ liệu đồng bộ tốt!");
            } else {
                sb.append("\n⚠️ Có sự không đồng bộ giữa 2 database!");
            }
            
            JOptionPane.showMessageDialog(this, sb.toString(),
                "Kết quả kiểm tra", isValid ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi kiểm tra: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void loadData() {
        try {
            // Load summary statistics
            int tongNV = nhanVienService.countTotalNhanVien();
            lblTongNV.setText(numberFormat.format(tongNV));
            
            if (SessionManager.getInstance().canAccessSalary()) {
                Map<String, BigDecimal> luongStats = luongService.getLuongStatistics();
                
                lblTongLuong.setText(currencyFormat.format(luongStats.get("total")));
                lblLuongTB.setText(currencyFormat.format(luongStats.get("average")));
            } else {
                lblTongLuong.setText("Không có quyền xem");
                lblLuongTB.setText("Không có quyền xem");
            }
            
            int nam = (Integer) cboNam.getSelectedItem();
            int nvMoi = nhanVienService.countNewEmployeesInYear(nam);
            lblNVMoi.setText(numberFormat.format(nvMoi));
            
            // Load phòng ban stats
            loadPhongBanStats();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu báo cáo: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadPhongBanStats() throws SQLException {
        tableModel.setRowCount(0);
        
        var stats = nhanVienService.getStatsByPhongBan();
        
        for (var stat : stats) {
            tableModel.addRow(new Object[]{
                stat.get("tenPB"),
                stat.get("soNV"),
                stat.get("tongLuong") != null ? currencyFormat.format(stat.get("tongLuong")) : "N/A",
                stat.get("luongTB") != null ? currencyFormat.format(stat.get("luongTB")) : "N/A",
                stat.get("luongMax") != null ? currencyFormat.format(stat.get("luongMax")) : "N/A",
                stat.get("luongMin") != null ? currencyFormat.format(stat.get("luongMin")) : "N/A"
            });
        }
        
        // Repaint chart
        repaint();
    }
    
    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Lưu báo cáo");
        fileChooser.setSelectedFile(new java.io.File("BaoCao_NhanSu_" + LocalDate.now() + ".txt"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File file = fileChooser.getSelectedFile();
                java.io.PrintWriter writer = new java.io.PrintWriter(file);
                
                writer.println("BÁO CÁO THỐNG KÊ NHÂN SỰ & LƯƠNG");
                writer.println("Ngày: " + LocalDate.now());
                writer.println("Người xuất: " + SessionManager.getInstance().getCurrentUser().getHoTen());
                writer.println("=".repeat(50));
                writer.println();
                
                writer.println("TỔNG QUAN:");
                writer.println("- Tổng nhân viên: " + lblTongNV.getText());
                writer.println("- Tổng quỹ lương: " + lblTongLuong.getText());
                writer.println("- Lương trung bình: " + lblLuongTB.getText());
                writer.println("- NV mới trong năm: " + lblNVMoi.getText());
                writer.println();
                
                writer.println("THỐNG KÊ THEO PHÒNG BAN:");
                writer.println("-".repeat(50));
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    writer.printf("%-15s: %s NV, Lương TB: %s%n",
                        tableModel.getValueAt(i, 0),
                        tableModel.getValueAt(i, 1),
                        tableModel.getValueAt(i, 3));
                }
                
                writer.close();
                
                JOptionPane.showMessageDialog(this, "Xuất báo cáo thành công!\n" + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi xuất báo cáo: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
