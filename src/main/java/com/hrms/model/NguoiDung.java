package com.hrms.model;

import java.time.LocalDateTime;

/**
 * Entity class đại diện cho Người dùng hệ thống trong DB1 (HR_INFO)
 */
public class NguoiDung {
    private int maND;
    private String tenDangNhap;
    private String matKhau;
    private String maNV;
    private String vaiTro; // "ADMIN", "NHANVIEN", "KETOAN"
    private boolean trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime lanDangNhapCuoi;
    
    // Thông tin bổ sung
    private String hoTenNV;

    // Constructors
    public NguoiDung() {
        this.trangThai = true;
    }

    public NguoiDung(String tenDangNhap, String matKhau, String vaiTro) {
        this();
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
    }

    // Kiểm tra quyền
    public boolean isAdmin() {
        return "ADMIN".equals(vaiTro);
    }

    public boolean isKeToan() {
        return "KETOAN".equals(vaiTro);
    }

    public boolean isNhanVien() {
        return "NHANVIEN".equals(vaiTro);
    }

    public boolean canAccessSalary() {
        return isAdmin() || isKeToan();
    }

    // Getters and Setters
    public int getMaND() {
        return maND;
    }

    public void setMaND(int maND) {
        this.maND = maND;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getLanDangNhapCuoi() {
        return lanDangNhapCuoi;
    }

    public void setLanDangNhapCuoi(LocalDateTime lanDangNhapCuoi) {
        this.lanDangNhapCuoi = lanDangNhapCuoi;
    }

    public String getHoTenNV() {
        return hoTenNV;
    }

    public void setHoTenNV(String hoTenNV) {
        this.hoTenNV = hoTenNV;
    }
    
    /**
     * Alias method for UI compatibility
     */
    public String getHoTen() {
        return hoTenNV != null ? hoTenNV : tenDangNhap;
    }
    
    /**
     * Get mã người dùng (alias for maND)
     */
    public int getMaNguoiDung() {
        return maND;
    }

    public String getTenVaiTro() {
        switch (vaiTro) {
            case "ADMIN": return "Quản trị viên";
            case "KETOAN": return "Kế toán";
            case "NHANVIEN": return "Nhân viên";
            default: return vaiTro;
        }
    }

    @Override
    public String toString() {
        return "NguoiDung{" +
                "tenDangNhap='" + tenDangNhap + '\'' +
                ", vaiTro='" + vaiTro + '\'' +
                ", trangThai=" + trangThai +
                '}';
    }
}
