package com.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class đại diện cho Thông tin lương trong DB2 (HR_SALARY)
 * Chứa thông tin nhạy cảm về tài chính
 */
public class LuongNhanVien {
    private String maNV;
    private BigDecimal luongCoBan;
    private BigDecimal heSoLuong;
    private BigDecimal phuCapChucVu;
    private BigDecimal phuCapKhac;
    private LocalDate ngayHieuLuc;
    private LocalDateTime ngayCapNhat;
    private String ghiChu;
    
    // Thông tin bổ sung từ join với DB1
    private String hoTen;

    // Constructors
    public LuongNhanVien() {
        this.heSoLuong = BigDecimal.ONE;
        this.phuCapChucVu = BigDecimal.ZERO;
        this.phuCapKhac = BigDecimal.ZERO;
    }

    public LuongNhanVien(String maNV, BigDecimal luongCoBan) {
        this();
        this.maNV = maNV;
        this.luongCoBan = luongCoBan;
    }

    public LuongNhanVien(String maNV, BigDecimal luongCoBan, BigDecimal heSoLuong,
                        BigDecimal phuCapChucVu, BigDecimal phuCapKhac) {
        this.maNV = maNV;
        this.luongCoBan = luongCoBan;
        this.heSoLuong = heSoLuong;
        this.phuCapChucVu = phuCapChucVu;
        this.phuCapKhac = phuCapKhac;
    }

    // Tính tổng lương
    public BigDecimal getTongLuong() {
        BigDecimal luongTheoHeSo = luongCoBan.multiply(heSoLuong);
        return luongTheoHeSo.add(phuCapChucVu).add(phuCapKhac);
    }

    // Getters and Setters
    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
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

    public LocalDate getNgayHieuLuc() {
        return ngayHieuLuc;
    }

    public void setNgayHieuLuc(LocalDate ngayHieuLuc) {
        this.ngayHieuLuc = ngayHieuLuc;
    }

    public LocalDateTime getNgayCapNhat() {
        return ngayCapNhat;
    }

    public void setNgayCapNhat(LocalDateTime ngayCapNhat) {
        this.ngayCapNhat = ngayCapNhat;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
    
    public String getHoTen() {
        return hoTen;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    @Override
    public String toString() {
        return "LuongNhanVien{" +
                "maNV='" + maNV + '\'' +
                ", luongCoBan=" + luongCoBan +
                ", heSoLuong=" + heSoLuong +
                ", tongLuong=" + getTongLuong() +
                '}';
    }
}
