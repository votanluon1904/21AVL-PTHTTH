package Lab.Lab4socket.src.tcp;

import java.io.*;
import java.net.*;
import java.security.MessageDigest;

public class SecureFileClient {
    public static void main(String[] args) {
        // Thay đổi đường dẫn này để trỏ tới các file dùng để kiểm thử (rỗng, ảnh,
        // txt...)
        File fileToSend = new File("test_file.txt");

        // Tạo file ảo để test nếu chưa có
        if (!fileToSend.exists()) {
            try (PrintWriter pw = new PrintWriter(fileToSend)) {
                pw.print("Test file tiếng Việt có dấu");
            } catch (Exception e) {
            }
        }

        try (Socket socket = new Socket("localhost", 8000);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // Giả lập thử file chứa ../
            String unsafeName = "../" + fileToSend.getName();
            long fileSize = fileToSend.length();
            String hash = calculateSHA256(fileToSend);

            out.writeUTF(unsafeName);
            out.writeLong(fileSize);
            out.writeUTF(hash);

            if ("READY".equals(in.readUTF())) {
                try (FileInputStream fis = new FileInputStream(fileToSend)) {
                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
                System.out.println("Server phan hoi: " + in.readUTF());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String calculateSHA256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[8192];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : digest.digest())
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}