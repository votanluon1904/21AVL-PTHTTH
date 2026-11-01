package Lab.Lab6.Bai2;

import java.rmi.Naming; // Import thu vien Naming
import java.rmi.RemoteException; // Import thu vien ngoai le
import java.rmi.registry.LocateRegistry; // Import thu vien tao Registry
import java.rmi.server.UnicastRemoteObject; // Import doi tuong server RMI

public class MathServer extends UnicastRemoteObject implements MathInterface { // Lop server trien khai MathInterface
    protected MathServer() throws RemoteException { // Constructor cua lop server
        super(); // Goi khoi tao cua UnicastRemoteObject
    }

    @Override // Ghi de ham
    public int add(int a, int b) throws RemoteException { // Trien khai logic tinh tong
        return a + b; // Tra ve ket qua cong a va b
    }

    public static void main(String[] args) { // Ham khoi dong server
        try { // Khoi try bat loi
            LocateRegistry.createRegistry(1099); // Mo RMI registry tren cong 1099
            Naming.rebind("rmi://localhost/Math", new MathServer()); // Chay doi tuong va gan ten la "Math"
            System.out.println("May chu Tinh Toan da san sang."); // Thong bao may chu da mo (khong dau)
        } catch (Exception e) { // Xu ly neu co loi
            e.printStackTrace(); // In dong loi
        } // Ket thuc catch
    } // Ket thuc main
}
