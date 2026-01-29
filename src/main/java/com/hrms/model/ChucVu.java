package com.hrms.model;

/**
 * Entity class đại diện cho Chức vụ trong DB1 (HR_INFO)
 */
public class ChucVu {
    private String maCV;
    private String tenCV;
    private String moTa;
    private boolean trangThai;

    // Constructors
    public ChucVu() {
        this.trangThai = true;
    }

    public ChucVu(String maCV, String tenCV) {
        this();
        this.maCV = maCV;
        this.tenCV = tenCV;
    }

    // Getters and Setters
    public String getMaCV() {
        return maCV;
    }

    public void setMaCV(String maCV) {
        this.maCV = maCV;
    }

    public String getTenCV() {
        return tenCV;
    }

    public void setTenCV(String tenCV) {
        this.tenCV = tenCV;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return tenCV;
    }
}
