package Lab.Lab6.Bai6;

import java.rmi.Naming; // Nhap lay the hien
import java.util.Scanner; // Khai bao input

public class BankClient { // Client run
    public static void main(String[] args) { // Start point
        try { // Try process
            BankInterface stub = (BankInterface) Naming.lookup("rmi://localhost/Bank"); // Tim may chu tai hien Ngan
                                                                                        // hang
            Scanner sc = new Scanner(System.in); // Scanner
            while (true) { // Chay he thong banking vong lap
                System.out.println("\n--- MENU NGAN HANG ---"); // Title
                System.out.println("1. Kiem tra so du  2. Gui tien  3. Rut tien  4. Thoat"); // Options option string
                                                                                             // khong dau
                System.out.print("Chon giao dich: "); // Nhap vao
                int choice = sc.nextInt(); // Parse int choice
                if (choice == 1) { // Choice 1
                    System.out.println("So du hien tai: " + stub.getBalance()); // Go ham Get Balance tu xa
                } else if (choice == 2) { // Choice 2
                    System.out.print("Nhap so tien muon gui: "); // Msg prompt
                    double amount = sc.nextDouble(); // Parse double input
                    stub.deposit(amount); // Commit vao ham RMI deposit
                    System.out.println("Gui tien thanh cong!"); // Print report
                } else if (choice == 3) { // Choice 3
                    System.out.print("Nhap so tien muon rut: "); // Msg prompt
                    double amount = sc.nextDouble(); // Parse double withdraw
                    if (stub.withdraw(amount)) { // Commit vao RMI va lay booleang return
                        System.out.println("Rut tien thanh cong!"); // Report thanh cong
                    } else { // Truong hop tien am
                        System.out.println("Loi: So du khong du."); // Report khong the rut
                    } // Dong if-else
                } else if (choice == 4) { // Choice 4
                    break; // Halt loop
                } // Dong chuoi lenh dieu kien
            } // Ket thuc while loop
            sc.close(); // Giai phong resources
        } catch (Exception e) { // Bat loi
            e.printStackTrace(); // Dump exception log
        } // Ket thuc thu nghiem
    } // Dong method
}
