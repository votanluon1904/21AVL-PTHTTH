package Lab.Lab4socket.src.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ChatClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5005);
                BufferedReader console = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
                PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            // Khởi tạo luồng LẮNG NGHE tin nhắn từ Server chạy song song
            Thread readThread = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("\n[Da ngat ket noi khoi Server]");
                }
            });
            readThread.start(); // Bắt đầu luồng nghe

            // Luồng chính GỬI tin nhắn lên Server
            String userInput;
            while ((userInput = console.readLine()) != null) {
                out.println(userInput);
                if (userInput.equalsIgnoreCase("QUIT")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Khong the ket noi toi Chat Server: " + e.getMessage());
        }
    }
}