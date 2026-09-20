/* Bài tập đề xuất 2 */
package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class DigitToTextClient {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5001;

        try (Socket socket = new Socket(host, port);
                // Bắt buộc dùng UTF-8 cho cả console và kết nối mạng
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Da ket noi toi Server. Nhap mot chu so (0-9) hoac gõ QUIT de thoat.");
            String request;

            while ((request = console.readLine()) != null) {
                // Gửi nguyên bản request (kể cả chuỗi rỗng hay khoảng trắng)
                out.println(request);

                if (request.equalsIgnoreCase("QUIT")) {
                    break;
                }

                String response = in.readLine();
                if (response == null) {
                    System.out.println("Server da dong ket noi.");
                    break;
                }
                System.out.println("Server phan hoi: " + response);
            }
        } catch (IOException e) {
            System.err.println("Loi ket noi: " + e.getMessage());
        }
    }
}