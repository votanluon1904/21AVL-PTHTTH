package Lab.Lab6.Bai9;

import java.rmi.Remote; // Thu vien interface rmi remote
import java.rmi.RemoteException; // Thu vien ngoai le 

public interface AuctionInterface extends Remote { // Dinh nghia cac chuc nang tu xa cua ung dung dau gia
    void addProduct(String productName, double startingPrice) throws RemoteException; // Seller them san pham

    String getProducts() throws RemoteException; // Bidder lay danh sach cac san pham

    boolean placeBid(String productName, double bidAmount) throws RemoteException; // Bidder dua ra gia thau
}
