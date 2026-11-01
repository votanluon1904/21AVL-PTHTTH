package Lab.Lab6.Bai3;

import java.rmi.Naming; // Import Naming
import java.rmi.RemoteException; // Import loi
import java.rmi.registry.LocateRegistry; // Import Registry
import java.rmi.server.UnicastRemoteObject; // Import UnicastRemoteObject

public class PrimeServer extends UnicastRemoteObject implements PrimeInterface { // Server xu ly prime
    protected PrimeServer() throws RemoteException { // Constructor tao object
        super(); // Goi lop cha
    }

    @Override // Ghi de phuong thuc interface
    public boolean isPrime(int n) throws RemoteException { // Thuc thi thuat toan prime
        if (n <= 1)
            return false; // So nho hon hoac bang 1 khong phai so nguyen to
        for (int i = 2; i <= Math.sqrt(n); i++) { // Vong lap kiem tra uoc so den can bac hai
            if (n % i == 0)
                return false; // Neu chia het cho bat ky so nao thi tra ve false
        } // Ket thuc vong lap
        return true; // Neu khong co uoc so nao thi dung la so nguyen to
    }

    public static void main(String[] args) { // Ham main
        try { // Bat loi
            LocateRegistry.createRegistry(1099); // Mo registry
            Naming.rebind("rmi://localhost/Prime", new PrimeServer()); // Dang ky dich vu voi ten "Prime"
            System.out.println("May chu Kiem Tra Nguyen To san sang."); // In thong bao khong dau
        } catch (Exception e) { // Xu ly ngoai le
            e.printStackTrace(); // In chi tiet
        } // Dong catch
    } // Dong main
}
