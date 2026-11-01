package Lab.Lab6.Bai3;

import java.rmi.Remote; // Import lop Remote
import java.rmi.RemoteException; // Import lop xu ly loi

public interface PrimeInterface extends Remote { // Interface kiem tra nguyen to
    boolean isPrime(int n) throws RemoteException; // Ham kiem tra tra ve boolean
}
