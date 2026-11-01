package Lab.Lab6.Bai7;

import java.rmi.Naming;
import java.util.Scanner;

public class GeometryClient {
    public static void main(String[] args) {
        try {
            GeometryInterface stub = (GeometryInterface) Naming.lookup("rmi://localhost/Geometry");
            Scanner sc = new Scanner(System.in);
            System.out.println("Chon hinh de tinh dien tich: 1.Chu nhat  2.Hinh tron  3.Tam giac");
            int choice = sc.nextInt();
            if (choice == 1) {
                System.out.print("Chieu rong: ");
                double w = sc.nextDouble();
                System.out.print("Chieu cao: ");
                double h = sc.nextDouble();
                System.out.println("Dien tich la: " + stub.rectangleArea(w, h));
            } else if (choice == 2) {
                System.out.print("Ban kinh: ");
                double r = sc.nextDouble();
                System.out.println("Dien tich la: " + stub.circleArea(r));
            } else if (choice == 3) {
                System.out.print("Chieu dai day: ");
                double b = sc.nextDouble();
                System.out.print("Chieu cao: ");
                double h = sc.nextDouble();
                System.out.println("Dien tich la: " + stub.triangleArea(b, h));
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
