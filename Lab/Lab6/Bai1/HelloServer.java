package Lab.Lab6.Bai1;

import java.rmi.Naming; // Import thu vien ho tro dang ky ten doi tuong
import java.rmi.RemoteException; // Import ngoai le RMI
import java.rmi.registry.LocateRegistry; // Import thu vien tao Registry
import java.rmi.server.UnicastRemoteObject; // Import lop co so cho cac doi tuong RMI

public class HelloServer extends UnicastRemoteObject implements HelloInterface { // Tao lop server ke thua
                                                                                 // UnicastRemoteObject va trien khai
                                                                                 // interface
    protected HelloServer() throws RemoteException { // Khoi tao constructor mac dinh
        super(); // Goi constructor cua lop cha de xu ly giao tiep mang
    }

    @Override // Ghi de phuong thuc
    public String sayHello() throws RemoteException { // Trien khai logic cho phuong thuc sayHello
        return "Hello, World!"; // Tra ve chuoi Hello, World! cho client
    }

    public static void main(String[] args) { // Phuong thuc chay chinh cua server
        try { // Bat dau khoi xu ly ngoai le
            LocateRegistry.createRegistry(1099); // Tao mot RMI registry lang nghe tren cong 1099
            Naming.rebind("rmi://localhost/Hello", new HelloServer()); // Dang ky doi tuong HelloServer vao registry voi
                                                                       // ten "Hello"
            System.out.println("May chu Hello da san sang."); // In thong bao may chu da hoat dong (khong dau)
        } catch (Exception e) { // Bat ngoai le neu co loi xay ra
            e.printStackTrace(); // In chi tiet loi ra man hinh
        } // Ket thuc khoi catch
    } // Ket thuc ham main
}