package Lab.Lab6.Bai5;

import java.rmi.Naming; // Thu vien tao ten
import java.rmi.RemoteException; // Thu vien quan ly loi mang
import java.rmi.registry.LocateRegistry; // Thu vien thiet lap cong
import java.rmi.server.UnicastRemoteObject; // Lop cha cua object mang
import java.util.Scanner; // Thu vien nhap lieu phan hoi

public class ChatServer extends UnicastRemoteObject implements ChatInterface { // Class Server the hien giao dien
    private transient Scanner scanner; // Tao scanner an de doc reply ban phim tu server

    protected ChatServer() throws RemoteException { // Constructor server
        super(); // Khai bao len cha
        scanner = new Scanner(System.in); // Setup scanner nhap data tu console server
    }

    @Override // Ghi de tinh nang gui
    public String sendMessage(String message) throws RemoteException { // Client day len message
        System.out.println("Client noi: " + message); // Hien thi tin nhan cua client tren man hinh server
        System.out.print("Phan hoi cua ban: "); // Thuc giuc admin server tra loi
        return scanner.nextLine(); // Doc text va return de tra nguoc lai client
    }

    public static void main(String[] args) { // Khoi tao main
        try { // Xu ly block an toan
            LocateRegistry.createRegistry(1099); // Dinh hinh port registry 1099
            Naming.rebind("rmi://localhost/Chat", new ChatServer()); // Link ten Chat
            System.out.println("May chu Chat san sang. Cho tin nhan tu client..."); // Thong bao da xong
        } catch (Exception e) { // Bat exceptions
            e.printStackTrace(); // Show warning
        } // Dong catch
    } // Dong main
}
