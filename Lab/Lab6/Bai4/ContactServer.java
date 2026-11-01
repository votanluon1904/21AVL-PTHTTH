package Lab.Lab6.Bai4;

import java.rmi.Naming; // Ho tro dang ky ten tren RMI
import java.rmi.RemoteException; // Import ngoai le
import java.rmi.registry.LocateRegistry; // Import cong cu tao registry
import java.rmi.server.UnicastRemoteObject; // Lop co so de lang nghe tu xa
import java.util.HashMap; // Khai bao thu vien HashMap

public class ContactServer extends UnicastRemoteObject implements ContactInterface { // Lop may chu phuc vu danh ba
    private HashMap<String, String> contacts; // Cau truc du lieu map de luu ten va so dien thoai

    protected ContactServer() throws RemoteException { // Constructor khoi tao
        super(); // Goi constructor cua lop cha UnicastRemoteObject
        contacts = new HashMap<>(); // Cap phat bo nho cho HashMap
    }

    @Override // Ghi de phuong thuc them
    public void addContact(String name, String phone) throws RemoteException { // Trien khai them danh ba
        contacts.put(name, phone); // Dua ten va so vao map
        System.out.println("Da them lien he: " + name); // In log thong bao ra console may chu (khong dau)
    }

    @Override // Ghi de phuong thuc tim kiem
    public String findContact(String name) throws RemoteException { // Trien khai tim kiem
        return contacts.getOrDefault(name, "Khong tim thay lien he nay."); // Tra ve so dien thoai hoac bao khong co
                                                                           // (khong dau)
    }

    @Override // Ghi de phuong thuc xoa
    public boolean deleteContact(String name) throws RemoteException { // Trien khai viec xoa
        if (contacts.containsKey(name)) { // Kiem tra neu danh ba co ten do
            contacts.remove(name); // Xoa phan tu khoi bo nho
            return true; // Bao xoa thanh cong
        } // Ket thuc if
        return false; // Tra ve that bai neu khong tim thay
    }

    @Override // Ghi de phuong thuc cap nhat
    public boolean updateContact(String name, String newPhone) throws RemoteException { // Trien khai chuc nang sua
        if (contacts.containsKey(name)) { // Kiem tra xem ten co ton tai de sua khong
            contacts.put(name, newPhone); // Ghi de so dien thoai cu bang so moi
            System.out.println("Da cap nhat lien he: " + name); // In log server (khong dau)
            return true; // Bao cap nhat thanh cong
        } // Ket thuc if
        return false; // Tra ve that bai neu khong ton tai lien he
    }

    public static void main(String[] args) { // Ham khoi dong may chu
        try { // Bat dau khoi bao ve loi
            LocateRegistry.createRegistry(1099); // Mo RMI registry tren cong mac dinh 1099
            Naming.rebind("rmi://localhost/Contact", new ContactServer()); // Dang ky doi tuong vao mang voi ten Contact
            System.out.println("May chu Danh Ba san sang."); // Thong bao may chu da hoat dong (khong dau)
        } catch (Exception e) { // Bat ngoai le neu mang bi loi
            e.printStackTrace(); // In chi tiet noi dung loi
        } // Ket thuc try-catch
    } // Ket thuc main
}