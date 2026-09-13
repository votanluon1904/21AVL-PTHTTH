/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab2.bai1;

/**
 *
 * @author votan
 */
public class SanPham {
    //thuoc tinh
    private String tenSanPham;
    private String maSanPham;
    private double donGia;
    private int soLuong;
    //constructor day du tham so
    public SanPham(String tenSanPham, String maSanPham, double donGia, int soLuong){
        this.tenSanPham = tenSanPham;
        this.maSanPham = maSanPham;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }
    //Getter
    public String tenSanPham(){
        return tenSanPham;
    }
    public String maSanPham(){
        return maSanPham;
    }
    public double donGia(){
        return donGia;
    }
    public int soLuong(){
        return soLuong;
    }
    //tinh tien
    public double tinhThanhTien(){
        return donGia * soLuong;
    }
    //nhap hang
    public void nhapHang(int soLuongNhap){
        if(soLuongNhap > 0 ){
            soLuong = soLuong + soLuongNhap;
            System.out.println("Nhap hang thanh cong!");
        }else{
            System.out.println("So luong nhap phai lon hon 0!");
        }
    }
    public boolean banHang(int soLuongBan){
        if(soLuongBan <= 0){
            System.out.println("So luong ban phai lon hon 0!");
            return false;
        }
        if(soLuongBan > soLuong){
            System.out.println("Khong du hang trong kho de ban!");
            return false;
        }
        soLuong = soLuong - soLuongBan;
        System.out.println("Ban thanh cong!");
        return true;
    }
    //hien thi thong tin
    public void hienThi(){
        System.out.println("Ten San Pham: "+tenSanPham);
        System.out.println("Ma San Pham: "+maSanPham);
//        System.out.println("Don Gia: "+donGia);
        System.out.printf("Don Gia: %,.0f\n", donGia);
        System.out.println("So Luong Ton Kho: "+soLuong);
//        System.out.println("Thanh Tien: "+tinhThanhTien());
        System.out.printf("Thanh Tien: %,.0f\n", tinhThanhTien());
        System.out.println("----------------------------");
    }
}
