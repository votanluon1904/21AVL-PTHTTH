package Lab.Lab6.Bai5;

import java.rmi.Remote; // Khai bao lop Remote
import java.rmi.RemoteException; // Khai bao loi Remote

public interface ChatInterface extends Remote { // Tao interface cho ung dung Chat
    String sendMessage(String message) throws RemoteException; // Ham gui tin nhan tu client, server se tra ve chuoi
                                                               // phan hoi
}
