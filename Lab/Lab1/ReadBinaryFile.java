import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ReadBinaryFile {

    public static void loadSV(String src) throws IOException {

        // Mở file để đọc
        DataInputStream dis = new DataInputStream(
                new FileInputStream(new File(src)));

        // Đọc số lượng sinh viên
        int size = dis.readInt();

        ArrayList<SinhVien> listSV = new ArrayList<SinhVien>();

        // Đọc từng sinh viên
        for (int i = 0; i < size; i++) {

            // Đọc thông tin sinh viên
            String mssv = dis.readUTF();
            String name = dis.readUTF();
            int age = dis.readInt();

            // Đọc số lượng môn học
            int sizemh = dis.readInt();

            ArrayList<MonHoc> listMH = new ArrayList<MonHoc>();

            // Đọc danh sách môn học
            for (int j = 0; j < sizemh; j++) {

                String tenMonHoc = dis.readUTF();
                int tinChi = dis.readInt();
                double diem = dis.readDouble();

                MonHoc mh = new MonHoc(
                        tenMonHoc,
                        tinChi,
                        diem);

                listMH.add(mh);
            }

            // Tạo sinh viên và thêm vào danh sách
            SinhVien sv = new SinhVien(
                    mssv,
                    name,
                    age,
                    listMH);

            listSV.add(sv);
        }

        // In danh sách sinh viên
        for (SinhVien sv : listSV) {
            System.out.println(sv);
        }

        // Đóng file
        dis.close();
    }

    public static void main(String[] args) throws IOException {

        loadSV("C:\\\\Users\\\\votan\\\\OneDrive\\\\Máy tính\\\\sinhvien.dat");
    }
}