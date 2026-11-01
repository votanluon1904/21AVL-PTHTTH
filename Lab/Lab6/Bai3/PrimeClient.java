package Lab.Lab6.Bai3;

import java.rmi.Naming; // Naming lookup
import java.util.Scanner; // Khai bao doc ban phim

public class PrimeClient { // Lop client
    public static void main(String[] args) { // Ham main
        try { // Try khoi
            PrimeInterface stub = (PrimeInterface) Naming.lookup("rmi://localhost/Prime"); // Tra cuu RMI "Prime"
            Scanner sc = new Scanner(System.in); // Khoi tao scanner
            System.out.print("Nhap mot so nguyen: "); // Yeu cau nhap vao
            int n = sc.nextInt(); // Doc so nguyen n
            if (stub.isPrime(n)) { // Neu server tra ve true
                System.out.println(n + " la so nguyen to."); // In ra man hinh la so nguyen to
            } else { // Neu server tra ve false
                System.out.println(n + " khong phai so nguyen to."); // In thong bao nguoc lai
            } // Ket thuc if
            sc.close(); // Dong resource
        } catch (Exception e) { // Bat cac the loai loi
            e.printStackTrace(); // In loi
        } // Dong catch
    } // Dong main
}
