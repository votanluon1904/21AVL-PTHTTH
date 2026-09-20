/* Bài tập đề xuất 2 */
package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DigitToTextServer {
    private static final int PORT = 5001; // Dùng port 5001 để tránh trùng với bài trước

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(10);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server doi chu so dang chay tai port " + PORT + " (UTF-8)");
            while (true) {
                Socket socket = server.accept();
                pool.submit(() -> handleClient(socket));
            }
        } catch (IOException e) {
            System.err.println("Loi server: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    private static void handleClient(Socket socket) {
        String clientInfo = socket.getRemoteSocketAddress().toString();
        System.out.println("Client ket noi: " + clientInfo);

        try (socket;
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            String line;
            // Đọc từng dòng dữ liệu từ Client (mô hình 1 thông điệp / 1 dòng)
            while ((line = in.readLine()) != null) {
                // Thoát vòng lặp nếu client gửi QUIT
                if (line.equalsIgnoreCase("QUIT")) {
                    break;
                }

                // Kiểm tra điều kiện: Đúng 1 ký tự và phải là chữ số
                if (line.length() != 1 || !Character.isDigit(line.charAt(0))) {
                    out.println("ERR INVALID_DIGIT");
                } else {
                    // Ánh xạ chữ số sang tiếng Việt
                    String[] words = { "không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín" };
                    int digit = line.charAt(0) - '0';
                    out.println(words[digit]);
                }
            }
        } catch (IOException e) {
            System.err.println("Loi ket noi voi " + clientInfo + ": " + e.getMessage());
        } finally {
            System.out.println("Client ngat ket noi: " + clientInfo);
        }
    }
}