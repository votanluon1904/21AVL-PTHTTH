import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class WriteBinaryFile {

    public static void saveSV(String src, ArrayList<SinhVien> listSV)
            throws IOException {

        DataOutputStream dos = new DataOutputStream(
                new FileOutputStream(new File(src)));

        // Ghi số lượng sinh viên
        dos.writeInt(listSV.size());

        // Duyệt danh sách sinh viên
        for (SinhVien sv : listSV) {

            // Ghi thông tin sinh viên
            dos.writeUTF(sv.getMssv());
            dos.writeUTF(sv.getTen());
            dos.writeInt(sv.getTuoi());

            // Ghi số lượng môn học
            dos.writeInt(sv.getListMH().size());

            // Duyệt danh sách môn học
            for (MonHoc mh : sv.getListMH()) {

                // Ghi thông tin môn học
                dos.writeUTF(mh.getTenMonHoc());
                dos.writeInt(mh.getTinChi());
                dos.writeDouble(mh.getDiem());
            }
        }

        dos.flush();
        dos.close();
    }

    public static void main(String[] args) throws IOException {

        // Tạo các môn học
        MonHoc mh = new MonHoc("Lap trinh can ban", 3, 6.7);
        MonHoc mh1 = new MonHoc("Lap trinh Web", 3, 7.5);
        MonHoc mh2 = new MonHoc("Thiet ke he thong", 3, 8.0);

        // Tạo danh sách môn học
        ArrayList<MonHoc> listMH = new ArrayList<>();

        listMH.add(mh);
        listMH.add(mh1);
        listMH.add(mh2);

        // Tạo danh sách sinh viên
        ArrayList<SinhVien> listSV = new ArrayList<>();

        SinhVien sv = new SinhVien(
                "11329078",
                "Nguyen Van A",
                23,
                listMH);

        SinhVien sv1 = new SinhVien(
                "11329079",
                "Nguyen Van B",
                23,
                listMH);

        listSV.add(sv);
        listSV.add(sv1);

        // Ghi danh sách sinh viên xuống file nhị phân
        saveSV("C:\\Users\\votan\\OneDrive\\Máy tính\\sinhvien.dat", listSV);

        System.out.println("Da ghi danh sach sinh vien vao file!");
    }
}