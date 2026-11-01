package Lab.Lab6.Bai5;

import java.rmi.Naming; // RMI Naming lib
import java.util.Scanner; // Khai bao IO 

public class ChatClient { // Dinh nghia lop
    public static void main(String[] args) { // Ham thuc hien
        try { // Try bat dau
            ChatInterface stub = (ChatInterface) Naming.lookup("rmi://localhost/Chat"); // Tra cuu may chu Chat
            Scanner sc = new Scanner(System.in); // Scanner client
            System.out.println("Da ket noi voi may chu. Nhap 'thoat' de dung."); // Bao hieu da connected
            while (true) { // Chay tu dong liec tuc
                System.out.print("Ban: "); // Hien ky hieu ban
                String msg = sc.nextLine(); // Nguoi dung go
                if (msg.equalsIgnoreCase("thoat"))
                    break; // Huy chat neu go thoat
                String response = stub.sendMessage(msg); // Push len server, rmi se dong bo cho reply
                System.out.println("Server tra loi: " + response); // Show rep tu server
            } // Close while
            sc.close(); // Stop scanner
        } catch (Exception e) { // Kiem tra
            e.printStackTrace(); // Loi ket noi
        } // Close
    } // Close main
}