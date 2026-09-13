/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab2.bai2;

/**
 *
 * @author votan
 */
public class Main {
    public static void main(String[] args) {
        //tao 2 sinh vien
        SinhVien sv1 = new SinhVien(
                "Vo Tan Luon",
                2004,
                "Dong Thap",
                "SV01",
                "Cong Nghe Thong Tin",
                7
        );
        SinhVien sv2 = new SinhVien(
        "Nguyen Thanh Tung",
                2004,
                "Dong Thap",
                "SV02",
                "Ky Thuat Co Khi",
                8
        );
        //tao 2 giang vien
        GiangVien gv1 = new GiangVien(
        "Nguyen Van A",
                1970,
                "Ho Chi Minh",
                "GV01",
                "Lap Trinh Java",
                50000,
                2.5
        );
        GiangVien gv2 = new GiangVien(
        "Le Tan B",
                1967,
                "Ho Chi Minh",
                "GV02",
                "Mang May Tinh",
                60000,
                2.2
        );
        //hien thi sinh vien
        System.out.println("===== THONG TIN SINH VIEN =====");
        System.out.println("Sinh vien 1: ");
        sv1.hienThi();
        System.out.println("Sinh vien 2: ");
        sv2.hienThi();
        //hien thi giang vien
        System.out.println("===== THONG TIN GIANG VIEN =====");
        System.out.println("Giang vien 1: ");
        gv1.hienThi();
        System.out.println("Giang vien 2: ");
        gv2.hienThi();
    }
}
