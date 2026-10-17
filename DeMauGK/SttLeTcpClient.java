package DeMauGK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class SttLeTcpClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 7000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_IP, PORT);
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8));
                BufferedReader serverIn = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter serverOut = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Da ket noi den Server.");

            // Nhận menu
            String serverLine;
            while ((serverLine = serverIn.readLine()) != null) {
                if (serverLine.equals("END_MENU"))
                    break;
                System.out.println(serverLine);
            }

            // Gửi lựa chọn dịch vụ (1 hoặc 3)
            String choice = consoleInput.readLine();
            serverOut.println(choice);

            String confirmation = serverIn.readLine();
            System.out.println("Server: " + confirmation);
            if (confirmation != null && confirmation.startsWith("Loi"))
                return;

            // Vòng lặp nhập liệu liên tục
            while (true) {
                String userInput = consoleInput.readLine();
                if (userInput == null)
                    break;

                serverOut.println(userInput);

                if (userInput.equals("EXIT.")) {
                    System.out.println("Da gui lenh dong ket noi.");
                    break;
                }

                String response = serverIn.readLine();
                if (response != null) {
                    System.out.println("Server: " + response);
                }
            }

        } catch (IOException e) {
            System.err.println("Loi ket noi: " + e.getMessage());
        }
    }
}