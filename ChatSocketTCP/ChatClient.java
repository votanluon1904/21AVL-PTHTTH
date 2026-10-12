import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner scanner = new Scanner(System.in)) {

            System.out.println("Da ket noi toi Server Chat.");

            // Luồng để nhận tin nhắn từ server một cách độc lập
            Thread readThread = new Thread(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    System.out.println("\nDa ngat ket noi voi Server.");
                }
            });
            readThread.start();

            // Vòng lặp chính để đọc từ bàn phím và gửi đi
            while (scanner.hasNextLine()) {
                String message = scanner.nextLine();
                out.println(message);

                if (message.equalsIgnoreCase("/exit")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Khong the ket noi den Server: " + e.getMessage());
        }
    }
}