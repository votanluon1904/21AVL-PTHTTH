package DeMauGK;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;

public class ExamUdpClient {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 7000;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket();
                BufferedReader consoleInput = new BufferedReader(
                        new InputStreamReader(System.in, StandardCharsets.UTF_8))) {

            // Timeout 5 giây. Đề phòng gửi đi mà Server chết/rớt mạng thì Client không bị
            // treo vĩnh viễn
            socket.setSoTimeout(5000);
            InetAddress serverAddress = InetAddress.getByName(SERVER_IP);

            System.out.println("Da khoi tao UDP Client.");

            // Bước khởi đầu: Client phải chủ động "đánh tiếng" trước để Server biết IP/Port
            // mà gửi Menu về
            sendPacket(socket, serverAddress, "HELLO_SERVER");
            System.out.println(receivePacket(socket));

            // Vòng lặp giao tiếp chính
            while (true) {
                String userInput = consoleInput.readLine();
                if (userInput == null) {
                    break;
                }

                sendPacket(socket, serverAddress, userInput);

                if (userInput.equals("EXIT.")) {
                    System.out.println("Da gui yeu cau thoat.");
                    break;
                }

                String response = receivePacket(socket);
                if (response != null) {
                    System.out.println("Server: " + response);
                }
            }

        } catch (IOException e) {
            System.err.println("Loi UDP Client: " + e.getMessage());
        }
    }

    private static void sendPacket(DatagramSocket socket, InetAddress address, String message) throws IOException {
        byte[] sendData = message.getBytes(StandardCharsets.UTF_8);
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, PORT);
        socket.send(sendPacket);
    }

    private static String receivePacket(DatagramSocket socket) {
        try {
            byte[] receiveBuffer = new byte[2048];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);
            return new String(receivePacket.getData(), 0, receivePacket.getLength(), StandardCharsets.UTF_8).trim();
        } catch (SocketTimeoutException e) {
            return "[Loi] Khong nhan duoc phan hoi tu Server (Timeout).";
        } catch (IOException e) {
            return "[Loi] Doc phan hoi that bai: " + e.getMessage();
        }
    }
}