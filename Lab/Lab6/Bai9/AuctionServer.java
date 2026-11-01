package Lab.Lab6.Bai9;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class AuctionServer extends UnicastRemoteObject implements AuctionInterface {
    private HashMap<String, Double> auctionItems;

    protected AuctionServer() throws RemoteException {
        super();
        auctionItems = new HashMap<>();
    }

    @Override
    public synchronized void addProduct(String productName, double startingPrice) throws RemoteException {
        auctionItems.put(productName, startingPrice);
        System.out.println("Da them san pham dau gia: " + productName + " voi gia khoi diem: " + startingPrice);
    }

    @Override
    public synchronized String getProducts() throws RemoteException {
        if (auctionItems.isEmpty())
            return "Hien khong co san pham nao dang dau gia.";
        StringBuilder sb = new StringBuilder();
        for (String item : auctionItems.keySet()) {
            sb.append("San pham: ").append(item).append(" | Gia hien tai: ").append(auctionItems.get(item))
                    .append("\n");
        }
        return sb.toString();
    }

    @Override
    public synchronized boolean placeBid(String productName, double bidAmount) throws RemoteException {
        if (auctionItems.containsKey(productName)) {
            double currentPrice = auctionItems.get(productName);
            if (bidAmount > currentPrice) {
                auctionItems.put(productName, bidAmount);
                System.out.println("Cap nhat gia! San pham " + productName + " co gia moi la " + bidAmount);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            Naming.rebind("rmi://localhost/Auction", new AuctionServer());
            System.out.println("May chu Dau Gia da khoi dong xong.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
