/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package lab1;

/**
 *
 * @author votan
 */
import java.util.Scanner;
import java.io.File;
import java.io.EOFException;

public class Lab1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Hello, World!");

    }

    private void deleteFile(String cUsersvotanOneDriveMáy_tínhIUHKy3PhatTrie) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public class nhapten {

        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            System.out.println("What's your name?");
            String str = sc.nextLine();
            System.out.println("Hi, I am " + str);
        }
    }

    public class nhapso {

        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Vui long nhap so thu nhat: ");
            int soA = sc.nextInt();
            System.out.println("Vui long nhap so thu hai: ");
            int soB = sc.nextInt();
            int kq = soA + soB;
            System.out.println(" + soA + " + " + soB + " + " = " + kq);
        }
    }

    public class chanle {

        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Kiem tra so chan hay le");
            System.out.println("Vui long nhap so can kiem tra: ");
            int so = sc.nextInt();
            if (so % 2 == 0) {
                System.out.println("So " + so + "la so chan");
            } else {
                System.out.println("So " + so + "la so le");
            }
        }
    }

    public class thang {

        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            boolean isrun = true;
            while (isrun) {
                System.out.println("Vui long nhap thang: ");
                int so = sc.nextInt();
                switch (so) {
                    case 1:
                        System.out.println("January");
                        break;
                    case 2:
                        System.out.println("February");
                        break;
                    case 3:
                        System.out.println("March");
                        break;
                    case 4:
                        System.out.println("April");
                        break;
                    case 5:
                        System.out.println("May");
                        break;
                    case 6:
                        System.out.println("June");
                        break;
                    case 7:
                        System.out.println("July");
                        break;
                    case 8:
                        System.out.println("August");
                        break;
                    case 9:
                        System.out.println("September");
                        break;
                    case 10:
                        System.out.println("October");
                        break;
                    case 11:
                        System.out.println("November");
                        break;
                    case 12:
                        System.out.println("December");
                        break;
                    default:
                        isrun = false;
                        sc.close();
                        System.out.println("Stop");
                        break;
                }
            }
        }
    }

    public class xoafile {

        private void deleteFile(String source) {
            File file = new File(source);
            if (file.exists()) {
                System.out.println("File ton tai");
                file.delete();
                System.out.println("Xoa file thanh cong");
            } else {
                System.out.println("File khong ton tai");
            }
        }

        public boolean deleteEmptyFolder(String source) {
            File folder = new File(source);
            if (folder.exists()) {
                folder.delete();
                System.out.println("Folder khong ton tai\n Xoa Folder thanh cong");
            } else {
                System.out.println("Folder khong ton tai");
            }
            return false;
        }

        public boolean deleteListFileInfolder(String source) {
            File folder = new File(source);
// folder tồn tại
            if (folder.exists()) {
// danh sách file
                File[] listFile = folder.listFiles();
                if (listFile.length != 0) {
                    for (File f : listFile) {
// file thì xóa
                        if (f.isFile()) {
                            f.delete();
                        }
                    }
                }
                folder.delete();
                System.out.println("Delete folder thành công!");
                return true;
            } else {
                System.out.println("folder không tồn tại");
                return false;
            }
        }

        public static void main(String[] args) {
            Lab1 deleteFileIO = new Lab1();
            deleteFileIO.deleteFile("C:/Users/votan/OneDrive/Máy tính/IUH/Ky3/PhatTrienHeThongTichHop/Lab/Lab1/lab1basic.java");
        }

    }

    public class timfile {

        public void finFile(String source, String key) {
            File file = new File(source);
            if (file.exists()) {
                if (file.isFile()) {
                    if (file.getName().endsWith(key)) {
                        System.out.println(file.getAbsolutePath());
                    }
                }
                File[] listFile = file.listFiles();
                if (listFile != null) {
                    for (File f : listFile) {
                        finFile(f.getAbsolutePath(), key);
                    }
                }
            } else {
                System.out.println("source không tồn tại");
            }
        }
    }

    
}
