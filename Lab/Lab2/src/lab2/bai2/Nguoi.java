/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab2.bai2;

/**
 *
 * @author votan
 */
public class Nguoi {
    private String hoTen;
    private int namSinh;
    private String diaChi;
    //constructor day du tham so
    public Nguoi(String hoTen, int namSinh, String diaChi){
        this.hoTen = hoTen;
        this.namSinh = namSinh;
        this.diaChi = diaChi;
    }
    //getter
    public String getHoTen(){
        return hoTen;
    }
    public int getNamSinh(){
        return namSinh;
    }
    public String getDiaChi(){
        return diaChi;
    }
    //setter
    public void setHoTen(String hoTen){
        this.hoTen = hoTen;
    }
    public void setNamSinh(int namSinh){
        this.namSinh = namSinh;
    }
    public void setDiaChi(String diaChi){
        this.diaChi = diaChi;
    }
    //tinh tuoi
    public int tinhTuoi(){
        int namHienTai = 2026;
        return namHienTai - namSinh;
    }
    //hien thi
    public void hienThi(){
        System.out.println("Ho Ten: "+hoTen);
        System.out.println("Nam Sinh: "+namSinh);
        System.out.println("Dia Chi: "+diaChi);
        System.out.println("Tuoi: "+tinhTuoi());
    }
}
