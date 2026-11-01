package Lab.Lab6.Bai1;

import java.rmi.Naming; // Import thu vien de tim kiem doi tuong tu xa

public class HelloClient { // Tao lop client
    public static void main(String[] args) { // Phuong thuc chinh cua client
        try { // Bat dau khoi xu ly ngoai le
            HelloInterface stub = (HelloInterface) Naming.lookup("rmi://localhost/Hello"); // Tim doi tuong co ten
                                                                                           // "Hello" tu server va ep
                                                                                           // kieu
            System.out.println("Phan hoi tu server: " + stub.sayHello()); // Goi phuong thuc tu xa va in ket qua ra man
                                                                          // hinh
        } catch (Exception e) { // Bat cac loi ket noi hoac RMI
            e.printStackTrace(); // In chi tiet loi
        } // Ket thuc catch
    } // Ket thuc ham main
}