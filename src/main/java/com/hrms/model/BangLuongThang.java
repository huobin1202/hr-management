package com.hrms.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class đại diện cho Bảng lương tháng trong DB2 (HR_SALARY)
 */
public class BangLuongThang {
    private int maBangLuong;
    private String maNV;
    private int thang;
    private int nam;
    private int soNgayCong;
    private int soNgayNghiPhep;
    private int soNgayNghiKhongLuong;
    private BigDecimal luongCoBan;
    private BigDecimal heSoLuong;
    private BigDecimal phuCapChucVu;
    private BigDecimal phuCapKhac;
    private BigDecimal thuong;
    private BigDecimal khauTruBHXH;
    private BigDecimal khauTruBHYT;
    private BigDecimal khauTruBHTN;
    private BigDecimal khauTruThueTNCN;
    private BigDecimal khauTruKhac;
    private BigDecimal tongThuNhap;
    private BigDecimal tongKhauTru;
    private BigDecimal luongThucNhan;
    private String trangThai; // "Chờ duyệt", "Đã duyệt", "Đã thanh toán"
    private LocalDateTime ngayTao;
    private LocalDateTime ngayDuyet;
    private String nguoiDuyet;
    
    // Thông tin bổ sung từ join
    private String hoTenNV;
    private String tenPhongBan;

    // Constructors
    public BangLuongThang() {
        this.soNgayCong = 22;
        this.soNgayNghiPhep = 0;
        this.soNgayNghiKhongLuong = 0;
        this.thuong = BigDecimal.ZERO;
        this.khauTruKhac = BigDecimal.ZERO;
        this.trangThai = "Chờ duyệt";
    }

    public BangLuongThang(String maNV, int thang, int nam) {
        this();
        this.maNV = maNV;
        this.thang = thang;
        this.nam = nam;
    }

    // Getters and Setters
    public int getMaBangLuong() {
        return maBangLuong;
    }

    public void setMaBangLuong(int maBangLuong) {
        this.maBangLuong = maBangLuong;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public int getThang() {
        return thang;
    }

    public void setThang(int thang) {
        this.thang = thang;
    }

    public int getNam() {
        return nam;
    }

    public void setNam(int nam) {
        this.nam = nam;
    }

    public int getSoNgayCong() {
        return soNgayCong;
    }

    public void setSoNgayCong(int soNgayCong) {
        this.soNgayCong = soNgayCong;
    }

    public int getSoNgayNghiPhep() {
        return soNgayNghiPhep;
    }

    public void setSoNgayNghiPhep(int soNgayNghiPhep) {
        this.soNgayNghiPhep = soNgayNghiPhep;
    }

    public int getSoNgayNghiKhongLuong() {
        return soNgayNghiKhongLuong;
    }

    public void setSoNgayNghiKhongLuong(int soNgayNghiKhongLuong) {
        this.soNgayNghiKhongLuong = soNgayNghiKhongLuong;
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

    public BigDecimal getThuong() {
        return thuong;
    }

    public void setThuong(BigDecimal thuong) {
        this.thuong = thuong;
    }

    public BigDecimal getKhauTruBHXH() {
        return khauTruBHXH;
    }

    public void setKhauTruBHXH(BigDecimal khauTruBHXH) {
        this.khauTruBHXH = khauTruBHXH;
    }

    public BigDecimal getKhauTruBHYT() {
        return khauTruBHYT;
    }

    public void setKhauTruBHYT(BigDecimal khauTruBHYT) {
        this.khauTruBHYT = khauTruBHYT;
    }

    public BigDecimal getKhauTruBHTN() {
        return khauTruBHTN;
    }

    public void setKhauTruBHTN(BigDecimal khauTruBHTN) {
        this.khauTruBHTN = khauTruBHTN;
    }

    public BigDecimal getKhauTruThueTNCN() {
        return khauTruThueTNCN;
    }

    public void setKhauTruThueTNCN(BigDecimal khauTruThueTNCN) {
        this.khauTruThueTNCN = khauTruThueTNCN;
    }

    public BigDecimal getKhauTruKhac() {
        return khauTruKhac;
    }

    public void setKhauTruKhac(BigDecimal khauTruKhac) {
        this.khauTruKhac = khauTruKhac;
    }

    public BigDecimal getTongThuNhap() {
        return tongThuNhap;
    }

    public void setTongThuNhap(BigDecimal tongThuNhap) {
        this.tongThuNhap = tongThuNhap;
    }

    public BigDecimal getTongKhauTru() {
        return tongKhauTru;
    }

    public void setTongKhauTru(BigDecimal tongKhauTru) {
        this.tongKhauTru = tongKhauTru;
    }

    public BigDecimal getLuongThucNhan() {
        return luongThucNhan;
    }

    public void setLuongThucNhan(BigDecimal luongThucNhan) {
        this.luongThucNhan = luongThucNhan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public LocalDateTime getNgayDuyet() {
        return ngayDuyet;
    }

    public void setNgayDuyet(LocalDateTime ngayDuyet) {
        this.ngayDuyet = ngayDuyet;
    }

    public String getNguoiDuyet() {
        return nguoiDuyet;
    }

    public void setNguoiDuyet(String nguoiDuyet) {
        this.nguoiDuyet = nguoiDuyet;
    }

    public String getHoTenNV() {
        return hoTenNV;
    }

    public void setHoTenNV(String hoTenNV) {
        this.hoTenNV = hoTenNV;
    }

    public String getTenPhongBan() {
        return tenPhongBan;
    }

    public void setTenPhongBan(String tenPhongBan) {
        this.tenPhongBan = tenPhongBan;
    }

    public String getKyLuong() {
        return String.format("%02d/%d", thang, nam);
    }
    
    /**
     * Tính tổng phụ cấp
     */
    public BigDecimal getTongPhuCap() {
        BigDecimal pc1 = phuCapChucVu != null ? phuCapChucVu : BigDecimal.ZERO;
        BigDecimal pc2 = phuCapKhac != null ? phuCapKhac : BigDecimal.ZERO;
        return pc1.add(pc2);
    }
    
    /**
     * Alias for luongThucNhan for UI compatibility
     */
    public BigDecimal getThucLinh() {
        return luongThucNhan != null ? luongThucNhan : BigDecimal.ZERO;
    }
    
    /**
     * Get họ tên for display
     */
    public String getHoTen() {
        return hoTenNV;
    }
    
    public void setHoTen(String hoTen) {
        this.hoTenNV = hoTen;
    }
    
    /**
     * Alias for khấu trừ (tổng)
     */
    public BigDecimal getKhauTru() {
        return tongKhauTru != null ? tongKhauTru : BigDecimal.ZERO;
    }

    @Override
    public String toString() {
        return "BangLuongThang{" +
                "maNV='" + maNV + '\'' +
                ", kyLuong=" + getKyLuong() +
                ", luongThucNhan=" + luongThucNhan +
                ", trangThai='" + trangThai + '\'' +
                '}';
    }
}
