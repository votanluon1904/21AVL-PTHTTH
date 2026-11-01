package Lab.Lab6.Bai9;

import java.rmi.Naming;
import java.util.Scanner;

public class AuctionClient {
    public static void main(String[] args) {
        try {
            AuctionInterface stub = (AuctionInterface) Naming.lookup("rmi://localhost/Auction");
            Scanner sc = new Scanner(System.in);

            System.out.println("Ban la: 1. Nguoi ban (Seller)   2. Nguoi mua (Bidder)");
            int role = sc.nextInt();
            sc.nextLine();

            while (true) {
                if (role == 1) {
                    System.out.println("\n1. Them san pham   2. Thoat");
                    System.out.print("Lua chon: ");
                    int c = sc.nextInt();
                    sc.nextLine();
                    if (c == 1) {
                        System.out.print("Ten san pham: ");
                        String pName = sc.nextLine();
                        System.out.print("Gia khoi diem: ");
                        double price = sc.nextDouble();
                        stub.addProduct(pName, price);
                        System.out.println("Da dang san pham!");
                    } else if (c == 2) {
                        break;
                    }
                } else if (role == 2) {
                    System.out.println("\n1. Xem san pham   2. Dat gia (Bid)   3. Thoat");
                    System.out.print("Lua chon: ");
                    int c = sc.nextInt();
                    sc.nextLine();
                    if (c == 1) {
                        System.out.println(stub.getProducts());
                    } else if (c == 2) {
                        System.out.print("Ten san pham muon dau gia: ");
                        String pName = sc.nextLine();
                        System.out.print("Gia ban dua ra: ");
                        double bid = sc.nextDouble();
                        if (stub.placeBid(pName, bid)) {
                            System.out.println("Dat gia thanh cong! Ban dang giu muc gia cao nhat.");
                        } else {
                            System.out.println(
                                    "That bai! Gia thau phai cao hon gia hien tai hoac san pham khong ton tai.");
                        }
                    } else if (c == 3) {
                        break;
                    }
                }
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
