package Lab.Lab4socket.src.tcp;

import java.io.*;
import java.net.*;
import java.nio.file.Paths;
import java.security.MessageDigest;

public class SecureFileServer {
    private static final int PORT = 8000;
    private static final String UPLOAD_DIR = "upload";

    public static void main(String[] args) {
        new File(UPLOAD_DIR).mkdirs(); // Tạo thư mục upload cố định
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Secure File Server chay tai port " + PORT);
            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // Nhận Header: Tên file, Kích thước, Mã băm SHA-256
            String fileName = in.readUTF();
            long fileSize = in.readLong();
            String clientHash = in.readUTF();

            // Ngăn chặn tấn công Path Traversal (vd: ../../windows/system32/...)
            String safeFileName = Paths.get(fileName).getFileName().toString();
            File outputFile = new File(UPLOAD_DIR, safeFileName);

            out.writeUTF("READY"); // Báo cho client bắt đầu gửi dữ liệu

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[8192];
                long remaining = fileSize;

                // Đọc đúng số lượng byte đã khai báo (không dùng EOF)
                while (remaining > 0) {
                    int readSize = (int) Math.min(buffer.length, remaining);
                    int bytesRead = in.read(buffer, 0, readSize);
                    if (bytesRead == -1)
                        break;

                    fos.write(buffer, 0, bytesRead);
                    digest.update(buffer, 0, bytesRead);
                    remaining -= bytesRead;
                }
            }

            // Tính toán mã băm sau khi nhận xong và so sánh
            StringBuilder serverHash = new StringBuilder();
            for (byte b : digest.digest()) {
                serverHash.append(String.format("%02x", b));
            }

            if (serverHash.toString().equals(clientHash)) {
                out.writeUTF("OK");
                System.out.println("Luu thanh cong: " + safeFileName);
            } else {
                out.writeUTF("ERR HASH_MISMATCH");
                outputFile.delete(); // Xóa file lỗi
                System.out.println("Loi Hash: " + safeFileName);
            }
        } catch (Exception e) {
            System.err.println("Loi ket noi: " + e.getMessage());
        }
    }
}