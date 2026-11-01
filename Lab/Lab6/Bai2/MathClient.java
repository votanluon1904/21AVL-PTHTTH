package Lab.Lab6.Bai2;

import java.rmi.Naming; // Import Naming de tim kiem
import java.util.Scanner; // Import Scanner de nhap tu ban phim

public class MathClient { // Lop client
    public static void main(String[] args) { // Ham chay chinh
        try { // Khoi try bat loi ket noi
            MathInterface stub = (MathInterface) Naming.lookup("rmi://localhost/Math"); // Ket noi toi doi tuong "Math"
            Scanner scanner = new Scanner(System.in); // Tao doi tuong scanner de doc ban phim
            System.out.print("Nhap so a: "); // Yeu cau nhap so thu nhat
            int a = scanner.nextInt(); // Doc so a
            System.out.print("Nhap so b: "); // Yeu cau nhap so thu hai
            int b = scanner.nextInt(); // Doc so b
            System.out.println("Tong cua a va b la: " + stub.add(a, b)); // Gui cho server xu ly va in ket qua
            scanner.close(); // Dong scanner
        } catch (Exception e) { // Bat loi
            e.printStackTrace(); // In loi
        } // Ket thuc catch
    } // Ket thuc main
}
