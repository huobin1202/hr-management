package com.hrms;

import com.hrms.ui.LoginFrame;
import javax.swing.*;
import java.awt.*;

/**
 * Main Application Entry Point
 * Hệ thống Quản lý Lương & Hồ sơ Nhân sự
 * Minh họa kỹ thuật phân mảnh dọc CSDL
 */
public class MainApp {
    
    public static void main(String[] args) {
        // Thiết lập Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Thiết lập font mặc định hỗ trợ tiếng Việt
            Font defaultFont = new Font("Segoe UI", Font.PLAIN, 13);
            UIManager.put("Label.font", defaultFont);
            UIManager.put("Button.font", defaultFont);
            UIManager.put("TextField.font", defaultFont);
            UIManager.put("TextArea.font", defaultFont);
            UIManager.put("ComboBox.font", defaultFont);
            UIManager.put("Table.font", defaultFont);
            UIManager.put("TableHeader.font", defaultFont.deriveFont(Font.BOLD));
            UIManager.put("TabbedPane.font", defaultFont);
            UIManager.put("Menu.font", defaultFont);
            UIManager.put("MenuItem.font", defaultFont);
            UIManager.put("OptionPane.messageFont", defaultFont);
            UIManager.put("OptionPane.buttonFont", defaultFont);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Khởi chạy ứng dụng trong Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
