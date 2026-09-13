import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Class_Images {

    // Đọc file ảnh thành mảng byte
    public static byte[] readFile(File path) {

        try {
            FileInputStream fis = new FileInputStream(path);

            byte[] buf = new byte[1024];

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            int readNum;

            while ((readNum = fis.read(buf)) != -1) {
                bos.write(buf, 0, readNum);
            }

            fis.close();

            return bos.toByteArray();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    // Ghi mảng byte thành file ảnh
    public static void saveFile(File path, String tfile, byte[] bfile) {

        try {

            // Chuyển byte[] thành BufferedImage
            BufferedImage img = ImageIO.read(
                    new ByteArrayInputStream(bfile));

            // Ghi ảnh ra file
            ImageIO.write(img, tfile, path);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // Hàm main để kiểm tra
    public static void main(String[] args) {

        // Ảnh gốc
        File fileGoc = new File("C:\\Users\\votan\\OneDrive\\Máy tính\\save.jpg");

        // Đọc ảnh thành byte[]
        byte[] data = readFile(fileGoc);

        // Kiểm tra đọc thành công
        if (data != null) {

            System.out.println("Doc file anh thanh cong!");
            System.out.println("Kich thuoc: " + data.length + " bytes");

            // Ghi ảnh mới
            File fileMoi = new File("C:\\\\Users\\\\votan\\\\OneDrive\\\\Máy tính\\\\save_copy.jpg");

            saveFile(fileMoi, "jpg", data);

            System.out.println("Ghi file anh thanh cong!");
        }
    }
}