package Lab.Lab6.Bai6;

import java.rmi.Remote; // Su dung Remote RMI
import java.rmi.RemoteException; // Su dung Remote exception

public interface BankInterface extends Remote { // Tao Bank Interface
    double getBalance() throws RemoteException; // So du

    void deposit(double amount) throws RemoteException; // Nop vao

    boolean withdraw(double amount) throws RemoteException; // Rut ra va tra ve xem thanh cong khong
}
