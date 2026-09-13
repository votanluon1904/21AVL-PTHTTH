/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab2.bai2;

/**
 *
 * @author votan
 */
public class GiangVien extends Nguoi {
    private String maGiangVien;
    private String chuyenMon;
    private double luongCoBan;
    private double heSoLuong;
    //constructor
    public GiangVien(String hoTen, int namSinh, String diaChi, String maGiangVien, String chuyenMon, double luongCoBan, double heSoLuong){
        super(hoTen, namSinh, diaChi);
        this.maGiangVien = maGiangVien;
        this.chuyenMon = chuyenMon;
        this.luongCoBan = luongCoBan;
        this.heSoLuong = heSoLuong;
    }
    //getter
    public String getMaGiangVien(){
        return maGiangVien;
    }
    public String getChuyenMon(){
        return chuyenMon;
    }
    public double getLuongCoBan(){
        return luongCoBan;
    }
    public double heSoLuong(){
        return heSoLuong;
    }
    //setter 
    public void setMaGiangVien(String maGiangVien){
        this.maGiangVien = maGiangVien;
    }
    public void setChuyenMon(String chuyenMon){
        this.chuyenMon = chuyenMon;
    }
    public void setLuongCoBan(double luongCoBan){
        this.luongCoBan = luongCoBan;
    }
    public void setHeSoLuong(double heSoLuong){
        this.heSoLuong = heSoLuong;
    }
    //tinh luong
    public double tinhLuong(){
        return luongCoBan * heSoLuong;
    }
    //ghi de phuong thuc hienThi()
    @Override
    public void hienThi(){
        super.hienThi();
        System.out.println("Ma Giang Vien: "+maGiangVien);
        System.out.println("Chuyen Mon: "+chuyenMon);
        System.out.printf("Luong Co Ban: %,.0f\n", luongCoBan);
        System.out.println("He So Luong: "+heSoLuong);
        System.out.printf("Luong: %,.0f\n", tinhLuong());
        System.out.println("----------------------------");
    }
}
