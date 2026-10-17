package DeMauGK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ExamTcpClient {
    private static final String SERVER_IP = "127.0.0.1"; // Câu 2: Client kết nối tới IP cho trước
    private static final int PORT = 7000; // Câu 2: Client kết nối tới port cho trước

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter serverOut = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Da ket noi den server " + SERVER_IP + ":" + PORT);

            // 1. Nhận menu dịch vụ từ server
            String serverLine;
            while ((serverLine = serverIn.readLine()) != null) {
                if (serverLine.equals("END_MENU")) {
                    break;
                }
                System.out.println(serverLine);
            }

            // 2. Gửi lựa chọn dịch vụ (1, 2, 3, hoặc 4)
            String choice = consoleInput.readLine();
            serverOut.println(choice);

            // Nhận thông báo xác nhận từ server
            String confirmation = serverIn.readLine();
            System.out.println("Server: " + confirmation);

            // 3. Bắt đầu quá trình nhập liệu (Duy trì kết nối)
            while (true) {
                String userInput = consoleInput.readLine();
                if (userInput == null) {
                    break;
                }

                serverOut.println(userInput);

                // Câu 5: Thoát hoàn toàn chương trình nếu gửi "EXIT."
                if (userInput.equals("EXIT.")) {
                    System.out.println("Da gui yeu cau dong ket noi.");
                    break;
                }

                // Nếu gửi ".", chờ server tổng hợp kết quả (đặc biệt cho DV3) hoặc xác nhận
                // hoàn thành đợt nhập
                if (userInput.equals(".")) {
                    String finalResult = serverIn.readLine();
                    if (finalResult != null) {
                        System.out.println("Server: " + finalResult);
                    }
                    // Kết nối vẫn được duy trì, client có thể tiếp tục nhập
                } else {
                    // Nhận kết quả từng dòng (cho DV 1, 2, 4)
                    String response = serverIn.readLine();
                    if (response != null) {
                        System.out.println("Server: " + response);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Loi ket noi: " + e.getMessage());
        }
    }
}