package Lab.Lab6.Bai4;

import java.rmi.Naming; // Thu vien tra cuu ten may chu
import java.util.Scanner; // Thu vien nhap lieu tu ban phim

public class ContactClient { // Lop client cho nguoi dung
    public static void main(String[] args) { // Ham chay chinh cua trinh khach
        try { // Khoi kiem soat loi chuong trinh
            ContactInterface stub = (ContactInterface) Naming.lookup("rmi://localhost/Contact"); // Tim den doi tuong
                                                                                                 // Contact tu xa
            Scanner sc = new Scanner(System.in); // Khoi tao bo doc ban phim
            while (true) { // Vong lap chay menu lien tuc
                System.out.println("\n1. Them  2. Tim  3. Xoa  4. Sua  5. Thoat"); // In ra cac lua chon (co muc so 4 la
                                                                                   // Sua)
                System.out.print("Chon chuc nang: "); // Nhac nguoi dung nhap lua chon (khong dau)
                int choice = sc.nextInt(); // Doc so do nguoi dung chon
                sc.nextLine(); // Xoa bo dem ky tu xuong dong

                if (choice == 1) { // Neu chon them moi
                    System.out.print("Nhap ten: "); // Nhac nhap ten (khong dau)
                    String name = sc.nextLine(); // Doc chuoi ten
                    System.out.print("Nhap so dien thoai: "); // Nhac nhap sdt (khong dau)
                    String phone = sc.nextLine(); // Doc chuoi so dien thoai
                    stub.addContact(name, phone); // Truyen yeu cau len may chu
                    System.out.println("Da them thanh cong."); // Bao ket qua (khong dau)
                } else if (choice == 2) { // Neu chon tim kiem
                    System.out.print("Nhap ten can tim: "); // Nhac nhap ten can tim (khong dau)
                    String name = sc.nextLine(); // Doc chuoi
                    System.out.println("Ket qua: " + stub.findContact(name)); // Goi tim kiem va in ket qua
                } else if (choice == 3) { // Neu chon xoa danh ba
                    System.out.print("Nhap ten can xoa: "); // Nhac nhap ten de xoa (khong dau)
                    String name = sc.nextLine(); // Doc chuoi ten
                    if (stub.deleteContact(name)) { // Kiem tra neu ham xoa tren server tra ve true
                        System.out.println("Xoa thanh cong."); // In thong bao thanh cong (khong dau)
                    } else { // Neu xoa that bai
                        System.out.println("Khong tim thay de xoa."); // In thong bao that bai (khong dau)
                    } // Ket thuc if
                } else if (choice == 4) { // Neu chon SUA danh ba
                    System.out.print("Nhap ten can sua: "); // Yeu cau nhap ten lien he hien tai (khong dau)
                    String name = sc.nextLine(); // Doc ten vao bien
                    System.out.print("Nhap so dien thoai moi: "); // Yeu cau nhap sdt moi de thay the (khong dau)
                    String newPhone = sc.nextLine(); // Doc so dien thoai moi
                    if (stub.updateContact(name, newPhone)) { // Goi ham cap nhat tren server
                        System.out.println("Cap nhat thanh cong."); // Neu true in ra bao thanh cong (khong dau)
                    } else { // Truong hop server bao false
                        System.out.println("Khong tim thay lien he de sua."); // Thong bao khong co ten nay trong danh
                                                                              // ba (khong dau)
                    } // Ket thuc phan sua
                } else if (choice == 5) { // Neu chon thoat chuong trinh
                    break; // Ngat vong lap while
                } // Ket thuc dieu kien
            } // Ket thuc vong lap hien thi menu
            sc.close(); // Giai phong tai nguyen scanner
        } catch (Exception e) { // Bat ngoai le do dut mang hoac mat ket noi
            e.printStackTrace(); // In bieu do loi de xu ly
        } // Ket thuc khoi try-catch
    } // Ket thuc main
}
