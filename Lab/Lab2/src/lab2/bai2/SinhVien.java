/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab2.bai2;

/**
 *
 * @author votan
 */
public class SinhVien extends Nguoi{
    private String maSinhVien;
    private String nganhHoc;
    private double diemTrungBinh;
    //constructor
    public SinhVien(String hoTen, int namSinh, String diaChi, String maSinhVien, String nganhHoc, double diemTrungBinh){
        super(hoTen, namSinh, diaChi);
        this.maSinhVien = maSinhVien;
        this.nganhHoc = nganhHoc;
        this.diemTrungBinh = diemTrungBinh;
    }
    //getter
    public String getMaSinhVien(){
        return maSinhVien;
    }
    public String getNganhHoc(){
        return nganhHoc;
    }
    public double getDiemTrungBinh(){
        return diemTrungBinh;
    }
    //setter
    public void setMaSinhVien(String maSinhVien){
        this.maSinhVien = maSinhVien;
    }
    public void setNganhHoc(String nganhHoc){
        this.nganhHoc = nganhHoc;
    }
    public void setDiemTrungBinh(double diemTrungBinh){
        this.diemTrungBinh = diemTrungBinh;
    }
    //xep loai
    public String xepLoai(){
        if(diemTrungBinh >= 8.5){
            return "Gioi!";
        }else if(diemTrungBinh >= 7.0){
            return "Kha!";
        }else if(diemTrungBinh >= 5.0){
            return "Trung binh!";
        }else{
            return "Yeu!";
        }
    }
    //ghi de phuong thuc hienThi()
    @Override
    public void hienThi(){
        //goi phuong thuc cua lop cha
        super.hienThi();
        System.out.println("Ma Sinh Vien: "+maSinhVien);
        System.out.println("Nganh Hoc: "+nganhHoc);
        System.out.println("Diem Trunh Binh: "+diemTrungBinh);
        System.out.println("Xep Loai: "+xepLoai());
        System.out.println("-------------------------------");
    }
}
