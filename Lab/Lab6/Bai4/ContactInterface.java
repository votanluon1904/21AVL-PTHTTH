package Lab.Lab6.Bai4;

import java.rmi.Remote; // Thư vien ho tro RMI Remote
import java.rmi.RemoteException; // Thư vien bat loi RMI

public interface ContactInterface extends Remote { // Tao giao dien danh ba ke thua Remote
    void addContact(String name, String phone) throws RemoteException; // Phuong thuc them danh ba moi

    String findContact(String name) throws RemoteException; // Phuong thuc tim kiem danh ba

    boolean deleteContact(String name) throws RemoteException; // Phuong thuc xoa danh ba, tra ve ket qua dung/sai

    boolean updateContact(String name, String newPhone) throws RemoteException; // Phuong thuc sua (cap nhat) danh ba
}
