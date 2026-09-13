import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class lab1basic {
    public static boolean copyFile(String source, String dest) throws IOException {
        File sourceFile = new File(source);
        File destFile = new File(dest);

        if (!sourceFile.exists() || !sourceFile.isFile()) {
            System.out.println("File nguồn không tồn tại hoặc không phải file");
            return false;
        }

        File parent = destFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }

        try (FileInputStream fis = new FileInputStream(sourceFile);
                FileOutputStream fos = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
        }

        System.out.println("Copy thành công");
        return true;
    }

    public static void main(String[] args) throws IOException {
        copyFile("input.txt", "output.txt");
    }
}