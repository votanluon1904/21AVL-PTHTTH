/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab2.bai1;

/**
 *
 * @author votan
 */
public class Main {

    public static void main(String[] args) {
        //tao san pham 1
        SanPham sp1 = new SanPham(
                "Laptop",
                "SP01",
                1500000,
                10
        );
        //tao san pham 2
        SanPham sp2 = new SanPham(
                "Dien Thoai",
                "SP02",
                1000000,
                7
        );
        //hien thi thong tin ban dau
        System.out.println("===== THONG TIN BAN DAU =====");
        System.out.println("San Pham 1: ");
        sp1.hienThi();
        System.out.println("San Pham 2: ");
        sp2.hienThi();
        //nhap them hang cho sp1
        System.out.println("===== NHAP THEM HANG =====");
        System.out.println("Truoc khi nhap: ");
        sp1.hienThi();
        System.out.println("Nhap them 5 san pham: ");
        sp1.nhapHang(5);
        System.out.println("Sau khi nhap: ");
        sp1.hienThi();
        //ban hang thanh cong
        System.out.println("===== BAN HANG THANH CONG =====");
        System.out.println("Truoc khi ban: ");
        sp1.hienThi();
        System.out.println("Ban 3 san pham: ");
        boolean ketQua1 = sp1.banHang(3);
        System.out.println("Ket qua: "+ketQua1);
        System.out.println("Sau khi ban: ");
        sp1.hienThi();
        //ban so luong lon hon ton kho
        System.out.println("===== BAN QUA SO LUONG TON KHO =====");
        System.out.println("Truoc khi ban: ");
        sp1.hienThi();
        System.out.println("Thu ban 100 san pham: ");
        boolean ketQua2 = sp1.banHang(100);
        System.out.println("Ket qua: "+ketQua2);
        System.out.println("Sau khi ban: ");
        sp1.hienThi();
    }
}
