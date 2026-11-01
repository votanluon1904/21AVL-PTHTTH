package Lab.Lab6.Bai2;

import java.rmi.Remote; // Import thu vien Remote
import java.rmi.RemoteException; // Import thu vien xu ly ngoai le

public interface MathInterface extends Remote { // Tao interface tinh toan tu xa
    int add(int a, int b) throws RemoteException; // Khai bao ham cong hai so nguyen, nem ra ngoai le RMI
}
