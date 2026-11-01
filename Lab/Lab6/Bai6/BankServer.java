package Lab.Lab6.Bai6;

import java.rmi.Naming; // Kiem tra thu vien ten
import java.rmi.RemoteException; // Import loi
import java.rmi.registry.LocateRegistry; // Khoi chay quan ly server
import java.rmi.server.UnicastRemoteObject; // Import base server

public class BankServer extends UnicastRemoteObject implements BankInterface { // Trien khai interface
    private double balance; // Bien rieng tu hien tai cua so du

    protected BankServer() throws RemoteException { // Constructor setup
        super(); // Link voi system
        balance = 500000.0; // Khoi tao mac dinh so du la 500.000 (VND)
    }

    @Override // Cai de so du
    public double getBalance() throws RemoteException { // Phuong thuc check
        return balance; // Tra tien ra ngoai
    }

    @Override // Cai de nap
    public void deposit(double amount) throws RemoteException { // Phuong thuc nop them tien
        balance += amount; // Cong doi vao balance
        System.out.println("Khach hang vua gui them: " + amount); // Ghi log cho server biet
    }

    @Override // Cai de rut
    public boolean withdraw(double amount) throws RemoteException { // Phuong thuc rut tien co the bi tu choi
        if (amount <= balance) { // Kiem tra xem du hay khong
            balance -= amount; // Neu du thi tru tien
            System.out.println("Khach hang vua rut: " + amount); // Ghi log
            return true; // Chop nhat giao dich true
        } // End if
        System.out.println("Khach hang rut tien that bai do khong du so du."); // Ghi log failure
        return false; // Tra ve failure
    }

    public static void main(String[] args) { // Chay trinh chu
        try { // Try zone
            LocateRegistry.createRegistry(1099); // tao post 1099
            Naming.rebind("rmi://localhost/Bank", new BankServer()); // public len mang RMI name Bank
            System.out.println("Ngan hang ngam da bat, san sang giao dich."); // Noti console
        } catch (Exception e) { // Catch
            e.printStackTrace(); // Loi chi tiet
        } // End
    } // End main
}
