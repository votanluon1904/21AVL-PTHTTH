package Lab.Lab6.Bai8;

import java.rmi.Naming;
import java.util.Scanner;

public class FlightClient {
    public static void main(String[] args) {
        try {
            FlightInterface stub = (FlightInterface) Naming.lookup("rmi://localhost/Flight");
            Scanner sc = new Scanner(System.in);
            while (true) {
                System.out.println("\n1. Kiem tra ghe  2. Dat ve  3. Huy ve  4. Thoat");
                System.out.print("Chon hanh dong: ");
                int action = sc.nextInt();
                sc.nextLine();

                if (action == 1) {
                    System.out.print("Ma chuyen bay (VD: VN123): ");
                    String f = sc.nextLine();
                    int seats = stub.availableSeats(f);
                    if (seats >= 0)
                        System.out.println("So ghe trong: " + seats);
                    else
                        System.out.println("Ma chuyen bay khong hop le.");
                } else if (action == 2) {
                    System.out.print("Ma chuyen bay: ");
                    String f = sc.nextLine();
                    System.out.print("So luong ghe dat: ");
                    int s = sc.nextInt();
                    if (stub.bookTicket(f, s))
                        System.out.println("Dat ve thanh cong!");
                    else
                        System.out.println("That bai (Het ghe hoac sai ma chuyen).");
                } else if (action == 3) {
                    System.out.print("Ma chuyen bay: ");
                    String f = sc.nextLine();
                    System.out.print("So luong ghe huy: ");
                    int s = sc.nextInt();
                    if (stub.cancelBooking(f, s))
                        System.out.println("Huy ve thanh cong!");
                    else
                        System.out.println("That bai.");
                } else if (action == 4) {
                    break;
                }
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
