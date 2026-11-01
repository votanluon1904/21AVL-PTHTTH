package Lab.Lab6.Bai1;

import java.rmi.Remote; // Import interface Remote cua RMI
import java.rmi.RemoteException; // Import ngoai le RemoteException

public interface HelloInterface extends Remote { // Tao interface HelloInterface ke thua tu Remote de ho tro goi tu xa
    String sayHello() throws RemoteException; // Khai bao phuong thuc sayHello tra ve String, co the nem ra loi RMI
}