package com.hrms.model;

/**
 * Entity class đại diện cho Phòng ban trong DB1 (HR_INFO)
 */
public class PhongBan {
    private String maPB;
    private String tenPB;
    private String diaChi;
    private String soDienThoai;
    private java.time.LocalDate ngayThanhLap;
    private boolean trangThai;

    // Constructors
    public PhongBan() {
        this.trangThai = true;
    }

    public PhongBan(String maPB, String tenPB) {
        this();
        this.maPB = maPB;
        this.tenPB = tenPB;
    }

    // Getters and Setters
    public String getMaPB() {
        return maPB;
    }

    public void setMaPB(String maPB) {
        this.maPB = maPB;
    }

    public String getTenPB() {
        return tenPB;
    }

    public void setTenPB(String tenPB) {
        this.tenPB = tenPB;
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

    public java.time.LocalDate getNgayThanhLap() {
        return ngayThanhLap;
    }

    public void setNgayThanhLap(java.time.LocalDate ngayThanhLap) {
        this.ngayThanhLap = ngayThanhLap;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenPB;
    }
}
