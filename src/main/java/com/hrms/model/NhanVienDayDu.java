package com.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO class kết hợp thông tin từ cả DB1 và DB2
 * Dùng để hiển thị đầy đủ thông tin nhân viên cho Admin
 */
public class NhanVienDayDu {
    // Thông tin từ DB1
    private String maNV;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String cmnd;
    private String diaChi;
    private String soDienThoai;
    private String email;
    private String maPB;
    private String maCV;
    private String tenPhongBan;
    private String tenChucVu;
    private LocalDate ngayVaoLam;
    private boolean trangThai;
    
    // Thông tin từ DB2
    private BigDecimal luongCoBan;
    private BigDecimal heSoLuong;
    private BigDecimal phuCapChucVu;
    private BigDecimal phuCapKhac;
    private BigDecimal tongLuong;

    // Constructors
    public NhanVienDayDu() {
    }

    public NhanVienDayDu(NhanVien nv, LuongNhanVien luong) {
        if (nv != null) {
            this.maNV = nv.getMaNV();
            this.hoTen = nv.getHoTen();
            this.ngaySinh = nv.getNgaySinh();
            this.gioiTinh = nv.getGioiTinh();
            this.cmnd = nv.getCmnd();
            this.diaChi = nv.getDiaChi();
            this.soDienThoai = nv.getSoDienThoai();
            this.email = nv.getEmail();
            this.maPB = nv.getMaPB();
            this.maCV = nv.getMaCV();
            this.tenPhongBan = nv.getTenPhongBan();
            this.tenChucVu = nv.getTenChucVu();
            this.ngayVaoLam = nv.getNgayVaoLam();
            this.trangThai = nv.isTrangThai();
        }
        
        if (luong != null) {
            this.luongCoBan = luong.getLuongCoBan();
            this.heSoLuong = luong.getHeSoLuong();
            this.phuCapChucVu = luong.getPhuCapChucVu();
            this.phuCapKhac = luong.getPhuCapKhac();
            this.tongLuong = luong.getTongLuong();
        }
    }

    // Getters and Setters
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getCmnd() {
        return cmnd;
    }

    public void setCmnd(String cmnd) {
        this.cmnd = cmnd;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMaPB() {
        return maPB;
    }

    public void setMaPB(String maPB) {
        this.maPB = maPB;
    }

    public String getMaCV() {
        return maCV;
    }

    public void setMaCV(String maCV) {
        this.maCV = maCV;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    public String getTenChucVu() {
        return tenChucVu;
    }

    public void setTenChucVu(String tenChucVu) {
        this.tenChucVu = tenChucVu;
    }

    public LocalDate getNgayVaoLam() {
        return ngayVaoLam;
    }

    public void setNgayVaoLam(LocalDate ngayVaoLam) {
        this.ngayVaoLam = ngayVaoLam;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getLuongCoBan() {
        return luongCoBan;
    }

    public void setLuongCoBan(BigDecimal luongCoBan) {
        this.luongCoBan = luongCoBan;
    }

    public BigDecimal getHeSoLuong() {
        return heSoLuong;
    }

    public void setHeSoLuong(BigDecimal heSoLuong) {
        this.heSoLuong = heSoLuong;
    }

    public BigDecimal getPhuCapChucVu() {
        return phuCapChucVu;
    }

    public void setPhuCapChucVu(BigDecimal phuCapChucVu) {
        this.phuCapChucVu = phuCapChucVu;
    }

    public BigDecimal getPhuCapKhac() {
        return phuCapKhac;
    }

    public void setPhuCapKhac(BigDecimal phuCapKhac) {
        this.phuCapKhac = phuCapKhac;
    }

    public BigDecimal getTongLuong() {
        return tongLuong;
    }

    public void setTongLuong(BigDecimal tongLuong) {
        this.tongLuong = tongLuong;
    }

    @Override
    public String toString() {
        return "NhanVienDayDu{" +
                "maNV='" + maNV + '\'' +
                ", hoTen='" + hoTen + '\'' +
                ", phongBan='" + tenPhongBan + '\'' +
                ", tongLuong=" + tongLuong +
                '}';
    }
}
